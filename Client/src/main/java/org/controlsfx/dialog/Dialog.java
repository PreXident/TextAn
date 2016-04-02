package org.controlsfx.dialog;

import javafx.scene.control.ButtonType;

/**
 * 
 */
public final class Dialog {
    
    public enum Action {
        YES(ButtonType.YES),
        NO(ButtonType.NO),
        CANCEL(ButtonType.CANCEL),
        CLOSE(ButtonType.CLOSE);
        
        private final ButtonType buttonType;
        
        public static Action valueOf(final ButtonType buttonType) {
            for (Action action : Action.values()) {
                if (action.toButtonType() == buttonType) {
                    return action;
                }
            }
            return CANCEL;
        }
        
        Action(final ButtonType buttonType) {
            this.buttonType = buttonType;
        }
        
        public ButtonType toButtonType() {
            return buttonType;
        }
    }
    
    /**
     * Utility classes need no contructor.
     */
    private Dialog() {
        //nothing
    }
}
