package cz.cuni.mff.ufal.textan.tui;

import static cz.cuni.mff.ufal.textan.Utils.extractExtension;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.load.IImporter;
import cz.cuni.mff.ufal.textan.core.processreport.load.ImportManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Callable;

/**
 * Factory for creating tasks processing reports.
 */
public class ProcessReportTaskFactory {

    /**
     * Creates new instance of the ProcessReportFactory.
     * @param settings application settings
     * @param newObjects indicator whether new object should be created if no object is assigned to entities
     * @param force indicator whether reports should be force saved
     * @return new instance of the ProcessReportFactory
     */
    public static ProcessReportTaskFactory createNewInstance(
            final Properties settings, final boolean newObjects,
            final boolean force) {
        return new ProcessReportTaskFactory(settings, newObjects, force);
    }

    /**
     * Application settings.
     * Used for creating the Client.
     */
    protected final Properties settings;

    /** Indicator whether new object should be created if no object is assigned to entities. */
    protected final boolean newObjects;

    /** Indicator whether reports should be force saved. */
    protected final boolean force;

    /**
     * Client for communication with the server.
     * It's thread local, just to be sure.
     */
    final protected ThreadLocal<Client> client =
        new ThreadLocal<Client>() {
            @Override
            protected Client initialValue() {
                return new Client(settings);
            }
    };

    /**
     * Only constructor.
     * @param settings application settings
     * @param newObjects indicator whether new object should be created if no object is assigned to entities
     * @param force indicator whether reports should be force saved
     */
    private ProcessReportTaskFactory(final Properties settings,
            final boolean newObjects, final boolean force) {
        this.settings = settings;
        this.newObjects = newObjects;
        this.force = force;
    }

    /**
     * Returns thread local client.
     * @return thread local client
     */
    private Client getClient() {
        return client.get();
    }

    /**
     * Creates new task for processing given file.
     * @param file file with report
     * @return new task for processing given file
     */
    public ProcessReportTask newTask(final File file) {
        return new ProcessReportTask(file);
    }

    /**
     * Processes report from given file.
     * If any error occurs, logs to error output.
     */
    public class ProcessReportTask implements Callable<Void> {

        /** File to process. */
        protected final File file;

        /**
         * Only constructor.
         * @param file file to process
         */
        protected ProcessReportTask(final File file) {
            this.file = file;
        }

        @Override
        public Void call() throws Exception {
            final String extension = extractExtension(file.getName());
            final ProcessReportPipeline pipeline = getClient().createNewReportPipeline();
            pipeline.selectFileDatasource();
            final IImporter importer = ImportManager.getDefaultForExtension(extension);
            final byte[] data;
            try {
                data = Files.readAllBytes(file.toPath());
                pipeline.setReportTextAndParse(importer.extractText(data));
                pipeline.setReportTextAndParse(pipeline.getReportText());
                pipeline.setReportWords(pipeline.getReportWords());
                if (newObjects) {
                    int counter = 0;
                    for (Entity ent : pipeline.getReportEntities()) {
                        if (ent.getCandidate() == null) {
                            ent.setCandidate(
                                    new cz.cuni.mff.ufal.textan.core.Object(--counter,
                                            ent.getType(),
                                            Arrays.asList(ent.getValue())));
                        }
                    }
                }
                pipeline.setReportObjects(pipeline.getReportEntities(), TxtRelationBuilder::new);
                pipeline.setReportRelations(pipeline.getReportWords(), Collections.emptyList());
                if (pipeline.getProblems() != null) {
                    if (force) {
                        pipeline.forceSave();
                    } else {
                        synchronized(System.err) {
                            System.err.printf("Problems while processing \"%s\"\n", file.getAbsoluteFile());
                            System.err.println(pipeline.getProblems());
                        }
                    }
                }
            } catch (IOException | DocumentChangedException | DocumentAlreadyProcessedException ex) {
                synchronized(System.err) {
                    System.err.printf("Error while processing \"%s\"\n", file.getAbsoluteFile());
                    ex.printStackTrace();
                }
            }
            return null;
        }
    }
}
