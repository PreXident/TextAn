package cz.cuni.mff.ufal.textan.gui.reportwizard;

import cz.cuni.mff.ufal.textan.core.processreport.DocumentAlreadyProcessedException;
import cz.cuni.mff.ufal.textan.core.processreport.DocumentChangedException;
import cz.cuni.mff.ufal.textan.core.processreport.ProcessReportPipeline;
import cz.cuni.mff.ufal.textan.gui.InnerWindow;
import cz.cuni.mff.ufal.textan.gui.OuterStage;
import cz.cuni.mff.ufal.textan.gui.TextAnController;
import cz.cuni.mff.ufal.textan.gui.Utils;
import cz.cuni.mff.ufal.textan.gui.WindowController;
import java.io.File;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import jfxtras.util.PlatformUtil;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;

/**
 * Common ancestor of controllers in this package.
 */
public abstract class ReportWizardController extends WindowController {

    /** Initial title of the wizard. */
    static protected final String TITLE = "Report Wizard";

    /** {@link #propertyID Identifier} used to store properties in {@link #settings}. */
    static protected final String PROPERTY_ID = "report.wizard";

    /** Path to resource bundle containing localization. */
    static private final String RESOURCE_BUNDLE_PATH = "cz.cuni.mff.ufal.textan.gui.reportwizard.ReportWizard";

    /** Pipeline controlling the report processing. */
    protected ProcessReportPipeline pipeline;

    @FXML
    TextFlow textFlow;

    /** Parent controller. */
    protected TextAnController textAnController;

    @Override
    public void setWindow(final InnerWindow window) {
        super.setWindow(window);
        if (textFlow != null) {
            window.boundsInLocalProperty().addListener(e -> {
                textFlow.layoutChildren();
            });
        }
    }

    @Override
    public void setStage(final OuterStage stage) {
        super.setStage(stage);
        if (textFlow != null) {
            stage.getInnerWindow().boundsInLocalProperty().addListener(e -> {
                textFlow.layoutChildren();
            });
        }
    }

    /**
     * Sets pipeline for this controller.
     * @param pipeline new pipeline
     */
    public void setPipeline(final ProcessReportPipeline pipeline) {
        this.pipeline = pipeline;
    }

    /**
     * Sets parent controller
     * @param textAnController new parent controller
     */
    public void setTextAnController(final TextAnController textAnController) {
        this.textAnController = textAnController;
    }

    /**
     * Calls callable and handle DocumentChangedException if needed.
     * Other exceptions are wrapped into RuntimeException.
     * @param root owner of the error dialog
     * @param callable method that may throw DocumentChangedException
     */
    public void handleDocumentChangedException(final Object root, final Callable<?> callable) {
        try {
            callable.call();
        } catch (DocumentChangedException e) {
            final ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
            final Action result = PlatformUtil.runAndWait(() -> {
                return callWithContentBackup(() -> {
                    return createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(rb, "error.documentchanged.title"))
                            .message(Utils.localize(rb, "error.documentchanged.message"))
                            .actions(Actions.YES, Actions.CLOSE)
                            .showConfirm();
                });
            });
            if (result == Actions.YES) {
                pipeline.switchToReplacingReport();
                try {
                    callable.call();
                } catch (Exception ex) {
                    wrapException(ex);
                }
            } else /*if (result == Actions.CLOSE)*/ {
                Platform.runLater(() -> closeContainer());
            }
        } catch (DocumentAlreadyProcessedException e) {
            final ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
            PlatformUtil.runAndWait(() -> {
                callWithContentBackup(() -> {
                    createDialog()
                            .owner(getDialogOwner(root))
                            .title(Utils.localize(rb, "error.documentprocessed.title"))
                            .message(Utils.localize(rb, "error.documentprocessed.message"))
                            .showError();
                });
                closeContainer();
            });
        } catch (Exception e) {
            wrapException(e);
        }
    }

    /**
     * Informs controller that it is now in control of the container.
     */
    public void nowInControl() {
        //nothing
    }

    /**
     * Prompts for save.
     * @param root node for displaying dialogs
     */
    public void promptSave(final Node root) {
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
            final ResourceBundle finalRB = rb;
            final Action result = callWithContentBackup(() -> {
                return createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(finalRB, "save.title"))
                        .message(Utils.localize(finalRB, "save.message"))
                        .actions(Actions.YES, Actions.NO, Actions.CANCEL)
                        .showConfirm();
            });
            if (result == Actions.CANCEL) {
                return;
            }
            if (result == Actions.YES) {
                final FileChooser chooser = new FileChooser();
                chooser.setTitle(Utils.localize(rb, "save.report.prompt"));
                final String dir = settings.getProperty("loadreport.dir");
                if (dir != null && !dir.isEmpty()) {
                    chooser.setInitialDirectory(new File(dir));
                } else {
                    chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                }
                final javafx.stage.Window w = window != null ? window.getScene().getWindow() : stage;
                final File file = chooser.showSaveDialog(w);
                if (file != null) {
                    settings.setProperty("loadreport.dir", file.getParent());
                    pipeline.save(file.getAbsolutePath());
                } else {
                    return;
                }
            }
            closeContainer();
        } catch(Exception e) {
            e.printStackTrace();
            final ResourceBundle finalRB = rb;
            callWithContentBackup(() -> {
                createDialog()
                        .owner(getDialogOwner(root))
                        .title(Utils.localize(finalRB, "error"))
                        .showException(e);
            });
        }
    }

    /**
     * Returns runnable prompting report save.
     * @param root node for displaying dialogs
     * @return runnable prompting report save
     */
    public Runnable getSavePrompter(final Node root) {
        return () -> {
            promptSave(root);
        };
    }

    /**
     * Wraps the exception e into RuntimeException and rethrows.
     * @param e exception to wrap and rethrow
     */
    public void wrapException(final Exception e) {
        final ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);
        throw new RuntimeException(Utils.localize(rb, "error"), e);
    }

    /**
     * Simple convertor to provide labels to progress sliders.
     */
    protected static class SliderLabelFormatter extends StringConverter<Double> {

        /** Localization container. */
        final protected ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_BUNDLE_PATH);

        @Override
        public String toString(Double val) {
            if (val < 0.5) {
                return Utils.localize(rb, "report.wizard.selectfile.label");
            }
            if (val < 1.5) {
                return Utils.localize(rb, "report.wizard.edit.label");
            }
            if (val < 2.5) {
                return Utils.localize(rb, "report.wizard.entities.label");
            }
            if (val < 3.5) {
                return Utils.localize(rb, "report.wizard.objects.label");
            }
            if (val < 4.5) {
                return Utils.localize(rb, "report.wizard.relations.label");
            }
            if (val < 5.5) {
                return Utils.localize(rb, "report.wizard.errors.label");
            }
            return "";
        }

        @Override
        public Double fromString(String string) {
            if (string == null || string.isEmpty()) {
                return 0D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.edit.label"))) {
                return 1D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.entities.label"))) {
                return 2D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.objects.label"))) {
                return 3D;
            }
            if (string.equals(Utils.localize(rb, "report.wizard.relations.label"))) {
                return 4D;
            }
           if (string.equals(Utils.localize(rb, "report.wizard.relations.label"))) {
                return 5D;
            }
            return 0D;
        }

    }
}
