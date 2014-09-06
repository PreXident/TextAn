package cz.cuni.mff.ufal.textan.tui;

import static cz.cuni.mff.ufal.textan.Utils.extractExtension;
import cz.cuni.mff.ufal.textan.core.Client;
import cz.cuni.mff.ufal.textan.core.Entity;
import cz.cuni.mff.ufal.textan.core.Object;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
     * @param heuristic indicator whether to use heuristic not to insert duplicite new objects
     * @param force indicator whether reports should be force saved
     * @return new instance of the ProcessReportFactory
     */
    public static ProcessReportTaskFactory createNewInstance(
            final Properties settings, final boolean newObjects,
            final boolean heuristic, final boolean force) {
        return new ProcessReportTaskFactory(settings, newObjects, heuristic, force);
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

    /** Flag indicating whether heuristic not to insert duplicite new objects should be used. */
    protected final boolean heuristic;

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
     * @param heuristic
     * @param force indicator whether reports should be force saved
     */
    private ProcessReportTaskFactory(final Properties settings,
            final boolean newObjects, final boolean heuristic, final boolean force) {
        this.settings = settings;
        this.newObjects = newObjects;
        this.heuristic = heuristic;
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

        /**
         * Guesses which object from mapping would go to entity.
         * @param entity entity without candidate
         * @param mapping alias -&gt; new object mapping
         * @return guessed object or null
         */
        protected Object guessObject(final Entity entity,
                final Map<String, Object> mapping) {
            final String alias = entity.getValue().toUpperCase();
            for (Entry<String, Object> entry : mapping.entrySet()) {
                final String a = entry.getKey();
                final boolean match = a.length() <= alias.length()
                        ? alias.contains(a)
                        : a.contains(alias);
                if (match) {
                    final Object object = entry.getValue();
                    mapping.put(alias, object);
                    return object;
                }
            }
            return null;
        }

        /**
         * Creates new objects for entities. Guesses if heuristic is true.
         * @param entities list of entities to create new candidates for
         */
        protected void newObjects(final List<Entity> entities) {
            int counter = 0;
            final Map<String, Object> mapping = new HashMap<>();
            for (Entity ent : entities) {
                if (ent.getCandidate() == null) {
                    Object cand;
                    if (!heuristic || (cand = guessObject(ent, mapping)) == null) {
                        cand = new Object(--counter, ent.getType(),
                                        Arrays.asList(ent.getValue()));
                        mapping.put(ent.getValue().toUpperCase(), cand);
                    }
                    ent.setCandidate(cand);
                }
            }
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
                    newObjects(pipeline.getReportEntities());
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
