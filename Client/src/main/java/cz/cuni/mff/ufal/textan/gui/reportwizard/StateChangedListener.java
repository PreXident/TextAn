package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.IStateChangedListener;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.core.processreport.State;
import cz.cuni.mff.ufal.textan.core.processreport.State.StateType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import jfxtras.labs.scene.control.window.Window;
import org.controlsfx.dialog.Dialogs;

/**
 * Implementation of IStateChangedListener.
 */
public class StateChangedListener implements IStateChangedListener {

    protected Map<StateType, String> fxmlMapping = new HashMap<>();

    private final Window window;

    private final ReportWizardStage stage;

    private final Properties settings;

    private final ProcessReportPipeline pipeline;

    {
        fxmlMapping.put(StateType.LOAD, "01_ReportLoad.fxml");
        fxmlMapping.put(StateType.EDIT_REPORT, "02_ReportEdit.fxml");
        fxmlMapping.put(StateType.EDIT_ENTITIES, "03_ReportEntities.fxml");
    }

    private StateChangedListener(final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage, final Window window) {
        this.settings = settings;
        this.pipeline = pipeline;
        this.stage = stage;
        this.window = window;
    }

    public StateChangedListener(final Properties settings, final ProcessReportPipeline pipeline, final ReportWizardStage stage) {
        this(settings, pipeline, stage, null);
    }

    public StateChangedListener(final Properties settings, final ProcessReportPipeline pipeline, final Window window) {
        this(settings, pipeline, null, window);
    }

    @Override
    public void stateChanged(State newState) {
        try {
            final String fxml = fxmlMapping.get(newState.getType());
            final FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
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
                stage.getReportWizardWindow().getContentPane().getChildren().clear();
                stage.getReportWizardWindow().getContentPane().getChildren().add(loadedRoot);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title("Problém při načítání další stránky!")
                    .showException(e);
        }
    }
}
