package cz.cuni.mff.ufal.textan.tui;

import com.beust.jcommander.JCommander;
import static cz.cuni.mff.ufal.textan.Utils.extractExtension;
import cz.cuni.mff.ufal.textan.commons.utils.UnclosableStream;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.load.ImportManager;
import cz.cuni.mff.ufal.textan.core.processreport.load.Importer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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

        //process reports
        final Client client = new Client(settings);
        for (File file : files) {
            System.out.printf("Processing \"%s\"...\n", file.getAbsoluteFile());
            final String extension = extractExtension(file.getName());
            final ProcessReportPipeline pipeline = client.createNewReportPipeline();
            pipeline.selectFileDatasource();
            final Importer importer = ImportManager.getDefaultForExtension(extension);
            final byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());
                pipeline.setReportTextAndParse(importer.extractText(data));
                pipeline.setReportTextAndParse(pipeline.getReportText());
                pipeline.setReportWords(pipeline.getReportWords());
                if (options.newObjects) {
                    int counter = 0;
                    for (Entity ent : pipeline.getReportEntities()) {
                        if (ent.getCandidate() == null) {
                            ent.setCandidate(
                                    new Object(--counter,
                                            ent.getType(),
                                            Arrays.asList(ent.getValue())));
                        }
                    }
                }
                pipeline.setReportObjects(pipeline.getReportEntities());
                pipeline.setReportRelations(pipeline.getReportWords(), Collections.emptyList());
                if (pipeline.getProblems() != null) {
                    if (options.force) {
                        pipeline.forceSave();
                    } else {
                        System.out.printf("Problems while processing \"%s\"\n", file.getAbsoluteFile());
                        System.out.println(pipeline.getProblems());
                    }
                }
            } catch (IOException | DocumentChangedException | DocumentAlreadyProcessedException ex) {
                System.out.printf("Error while processing \"%s\"\n", file.getAbsoluteFile());
                ex.printStackTrace();
            }
        }
    };
}
