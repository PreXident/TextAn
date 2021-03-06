package cz.cuni.mff.ufal.textan.tui;

import com.beust.jcommander.JCommander;
import cz.cuni.mff.ufal.textan.commons.utils.UnclosableStream;
import cz.cuni.mff.ufal.textan.tui.ProcessReportTaskFactory.ProcessReportTask;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple class for batch report processing.
 */
public class TextAn {

    /**
     * Loads and returns default properties from default properties.
     * @return default properties
     */
    private static Properties loadDefaultJarProperties() {
        final Properties res = new Properties();
        try {
            res.load(TextAn.class.getClassLoader().getResourceAsStream("TextAn.defprops"));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Main method.
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        //parse command line options
        final Options options = new Options();
        final JCommander jCommander = new JCommander(options, args);
        jCommander.setProgramName("java -cp TextAn.jar cz.cuni.mff.ufal.textan.tui.TextAn"); //set name in usage
        if (options.help
                || (options.directory == null && options.files.isEmpty())) {
            jCommander.usage();
            return;
        }

        //load settings
        final Properties settings = new Properties(loadDefaultJarProperties());
        try (final InputStream is =
                options.settings.equals("-") ? new UnclosableStream(System.in)
                    : new FileInputStream(options.settings)) {
             settings.load(is);
        } catch(FileNotFoundException e) {
            System.out.printf("File \"%s\" not found!\n", options.settings);
        } catch(Exception e) {
            System.err.printf("Error while loading \"%s\"!\n", options.settings);
            e.printStackTrace();
        }

        //get username
        if (settings.getProperty("username", "").isEmpty()) {
            try (final BufferedReader is =
                    new BufferedReader(
                            new InputStreamReader(new UnclosableStream(System.in)))) {
                String line;
                do {
                    System.out.println("Username: ");
                    line = is.readLine();
                    if (line == null) {
                        System.out.println("Username not provided!");
                        return;
                    }
                } while (line.isEmpty() || line.trim().isEmpty());
                settings.setProperty("username", line.trim());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        //get report files
        final List<File> files = new ArrayList<>();
        if (options.files.isEmpty()) {
            final File directory = new File(options.directory);
            if (!directory.isDirectory()) {
                System.out.printf("Directory \"%s\" not found!\n", options.directory);
                return;
            }
            files.addAll(Arrays.asList(directory.listFiles()));
        } else {
            files.addAll(options.files);
        }

        if (options.threads <= 0) { //auto set the number of threads
            options.threads = Runtime.getRuntime().availableProcessors();
        }

        //process reports
        final List<ProcessReportTask> tasks = new ArrayList<>(files.size());
        final ProcessReportTaskFactory factory =
                ProcessReportTaskFactory.createNewInstance(
                        settings,
                        options.newObjects,
                        options.newObjectsHeuristic,
                        options.force);
        for (File file : files) {
            tasks.add(factory.newTask(file));
        }
        final ExecutorService pool = Executors.newFixedThreadPool(options.threads);
        try {
            pool.invokeAll(tasks);
        } catch (Exception e) {
            synchronized(System.err) {
                System.err.println("Error while processing!");
                e.printStackTrace();
            }
        } finally {
            pool.shutdownNow();
        }
    };
}
