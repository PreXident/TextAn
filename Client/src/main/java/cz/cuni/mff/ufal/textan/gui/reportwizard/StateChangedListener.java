package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.IStateChangedListener;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.State;
import cz.cuni.mff.ufal.textan.core.processreport.State.StateType;
import cz.cuni.mff.ufal.textan.gui.Utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Implementation of IStateChangedListener.
 */
public class StateChangedListener implements IStateChangedListener {

    /**
     * Ugly hack to prevent mouse events for TextFlow to be ignored
     * and some display errors.
     * TODO more systematic solution
     * @param controller controller that possible holds textflow
     */
    static private void hackFixTextFlow(final ReportWizardController controller) {
        if (controller.textFlow != null) {
            runFXlater(() -> {
                controller.textFlow.layoutChildren();
            });
        }
    }

    /**
     * Runs finalizer in FX thread after 100 ms sleep in other thread.
     * @param finalizer runnable to be run
     */
    static private void runFXlater(final Runnable finalizer) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (Exception e) { }
            Platform.runLater(finalizer);
        }).start();
    }

    /** Contains fxml and resource bundle for each StateType. */
    protected Map<StateType, StateInfo> fxmlMapping = new HashMap<>();

    /** Wizard's window. This field or stage must not be null. */
    private final ReportWizardWindow window;

    /** Wizard's stage. This field or window must not be null. */
    private final ReportWizardStage stage;

    /** Application settings. Handle with care, they are shared. */
    private final Properties settings;

    /** Wizard's pipeline. */
    private final ProcessReportPipeline pipeline;

    /** Localization container. */
    private final ResourceBundle resourceBundle;

    {
        fxmlMapping.put(StateType.LOAD, new StateInfo("01_ReportLoad.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.01_ReportLoad", "report.wizard.load.title"));
        fxmlMapping.put(StateType.EDIT_REPORT, new StateInfo("02_ReportEdit.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.02_ReportEdit", "report.wizard.edit.title"));
        fxmlMapping.put(StateType.EDIT_ENTITIES, new StateInfo("03_ReportEntities.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.03_ReportEntities", "report.wizard.entities.title"));
        fxmlMapping.put(StateType.EDIT_OBJECTS, new StateInfo("04_ReportObjects.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.04_ReportObjects", "report.wizard.objects.title"));
        fxmlMapping.put(StateType.EDIT_RELATIONS, new StateInfo("05_ReportRelations.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.05_ReportRelations", "report.wizard.relations.title"));
    }

    /**
     * Main constructor with all parameters.
     * @param resourceBundle localization
     * @param settings application settings
     * @param pipeline pipeline
     * @param stage wizard's stage
     * @param window wizard's window
     */
    private StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage, final ReportWizardWindow window) {
        this.resourceBundle = resourceBundle;
        this.settings = settings;
        this.pipeline = pipeline;
        this.stage = stage;
        this.window = window;
    }

    /**
     * Wizard is contained in a stage. Window property will be null.
     * @param resourceBundle localization
     * @param settings application settings
     * @param pipeline pipeline
     * @param stage wizard's stage
     */
    public StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage) {
        this(resourceBundle, settings, pipeline, stage, null);
    }

    /**
     * Wizard is contained in a window. Stage property will be null.
     * @param resourceBundle localization
     * @param settings application settings
     * @param pipeline pipeline
     * @param window wizard's window
     */
    public StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardWindow window) {
        this(resourceBundle, settings, pipeline, null, window);
    }

    @Override
    public void stateChanged(State newState) {
        Platform.runLater(() -> {
            if (newState.getType() == StateType.DONE) {
                if (window != null) {
                    window.close();
                } else /*if (stage != null) */ {
                    stage.close();
                }
                return;
            }
            try {
                final StateInfo stateInfo = fxmlMapping.get(newState.getType());
                final ResourceBundle rb = ResourceBundle.getBundle(stateInfo.rb);
                final FXMLLoader loader = new FXMLLoader(getClass().getResource(stateInfo.fxml), rb);
                final Parent loadedRoot = (Parent) loader.load();
                ReportWizardController controller = loader.getController();
                controller.setSettings(settings);
                controller.setPipeline(pipeline);
                final String title = Utils.localize(resourceBundle, stateInfo.title);
                if (window != null) {
                    window.getContentPane().getChildren().clear();
                    controller.setWindow(window);
                    window.getContentPane().getChildren().add(loadedRoot);
                    window.setTitle(title);
                } else /* if (stage != null) */ {
                    controller.setStage(stage);
                    stage.getInnerWindow().getContentPane().getChildren().clear();
                    stage.getInnerWindow().getContentPane().getChildren().add(loadedRoot);
                    stage.getInnerWindow().setTitle(title);
                }
                hackFixTextFlow(controller);
            } catch (IOException e) {
                e.printStackTrace();
                Dialogs.create()
                        .title(Utils.localize(resourceBundle, "error.next.page"))
                        .showException(e);
            }
            pipeline.lock.release();
        });
    }

    /**
     * Simple holder of information about states.
     */
    protected static class StateInfo {

        /** Fxml containing the state's view. */
        public final String fxml;

        /** Resource bundle containing localization for the view. */
        public final String rb;

        /** Key to view title localization. */
        public final String title;

        /**
         * Only constructor.
         * @param fxml fxml containing the state's view
         * @param rb resource bundle containing localization for the view
         * @param title key to view title localization
         */
        public StateInfo(final String fxml, final String rb, final String title) {
            this.fxml = fxml;
            this.rb = rb;
            this.title = title;
        }
    }
}
