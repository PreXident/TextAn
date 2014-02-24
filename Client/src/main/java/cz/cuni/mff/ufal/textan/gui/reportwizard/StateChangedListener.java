package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.IStateChangedListener;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.State;
import cz.cuni.mff.ufal.textan.core.processreport.State.StateType;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.utils.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.controlsfx.dialog.Dialogs;

/**
 * Implementation of IStateChangedListener.
 */
public class StateChangedListener implements IStateChangedListener {

    /** Contains fxml and resource bundle for each StateType. */
    protected Map<StateType, Pair<String, String>> fxmlMapping = new HashMap<>();

    private final ReportWizardWindow window;

    private final ReportWizardStage stage;

    private final Properties settings;

    private final ProcessReportPipeline pipeline;

    private final ResourceBundle resourceBundle;

    {
        fxmlMapping.put(StateType.LOAD, new Pair<>("01_ReportLoad.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.01_ReportLoad"));
        fxmlMapping.put(StateType.EDIT_REPORT, new Pair<>("02_ReportEdit.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.02_ReportEdit"));
        fxmlMapping.put(StateType.EDIT_ENTITIES, new Pair<>("03_ReportEntities.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.03_ReportEntities"));
        fxmlMapping.put(StateType.EDIT_OBJECTS, new Pair<>("04_ReportObjects.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.04_ReportObjects"));
        fxmlMapping.put(StateType.EDIT_RELATIONS, new Pair<>("05_ReportRelations.fxml", "cz.cuni.mff.ufal.textan.gui.reportwizard.05_ReportRelations"));
    }

    private StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage, final ReportWizardWindow window) {
        this.resourceBundle = resourceBundle;
        this.settings = settings;
        this.pipeline = pipeline;
        this.stage = stage;
        this.window = window;
    }

    public StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage) {
        this(resourceBundle, settings, pipeline, stage, null);
    }

    public StateChangedListener(final ResourceBundle resourceBundle, final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardWindow window) {
        this(resourceBundle, settings, pipeline, null, window);
    }

    @Override
    public void stateChanged(State newState) {
        if (newState.getType() == StateType.DONE) {
            if (window != null) {
                window.close();
            } else {
                stage.close();
            }
        }
        try {
            final Pair<String, String> pair = fxmlMapping.get(newState.getType());
            final String fxml = pair.getFirst();
            final String rbName = pair.getSecond();
            final ResourceBundle rb = ResourceBundle.getBundle(rbName);
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), rb);
            final Parent loadedRoot = (Parent) loader.load();
            ReportWizardController controller = loader.getController();
            controller.setSettings(settings);
            controller.setPipeline(pipeline);
            if (window != null) {
                window.getContentPane().getChildren().clear();
                controller.setWindow(window);
                window.getContentPane().getChildren().add(loadedRoot);
            } else /* if (stage != null) */ {
                controller.setStage(stage);
                stage.getInnerWindow().getContentPane().getChildren().clear();
                stage.getInnerWindow().getContentPane().getChildren().add(loadedRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title(Utils.localize(resourceBundle, "error.next.page"))
                    .showException(e);
        }
    }
}
