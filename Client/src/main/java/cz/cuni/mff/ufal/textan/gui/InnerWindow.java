package cz.cuni.mff.ufal.textan.gui;

import java.util.Properties;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

/**
 * Ancestor of all inner windows.
 */
public class InnerWindow extends Window {

    /** First class of the InnerWindow to provide ClickableWindowSkin. */
    protected final String CLICKABLE_CLASS = "clickable-window";

    /** Minimal width of inner widows. */
    static final int MIN_WIDTH = 450;

    /** Minimal height of inner widows. */
    static final int MIN_HEIGHT = 300;

    /**
     * Settings of the application.
     * Handle with care, they're shared!
     */
    final protected Properties settings;

    /** Identifier used to store properties in {@link #settings}. */
    final protected String propertyID;

    /** Flag whether the window maximized. */
    final protected BooleanProperty maximized = new SimpleBooleanProperty();

    /** Stored width if window is maximized to restore it if needed. */
    protected double unmaximizedWidth;

    /** Stored height if window is maximized to restore it if needed. */
    protected double unmaximizedHeight;

    /** Stored x-coord if window is maximized to restore it if needed. */
    protected double unmaximizedX;

    /** Stored y-coord if window is maximized to restore it if needed. */
    protected double unmaximizedY;

    /**
     * Change listener for parent size changes.
     */
    final protected ChangeListener<Number> sizeListener =
        (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
            final Parent parent = getParent();
            if (parent instanceof Region || !maximized.get()) {
                adjustX(getLayoutX());
                adjustY(getLayoutY());
                adjustHeight(getPrefHeight());
                adjustWidth(getPrefWidth());
            }
        };

    /**
     * Only constructor.
     * @param title window's initial title
     * @param propertyID identifier used to store properties
     * @param settings properties with application settings
     */
    public InnerWindow(final String title, final String propertyID, final Properties settings) {
        super(title);
        this.propertyID = propertyID;
        this.settings = settings;
        this.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        this.setContainerCloser(() -> close());
        this.getStyleClass().add(0, CLICKABLE_CLASS);
        //
        parentProperty().addListener(
            (ObservableValue<? extends Parent> ov, Parent oldVal, Parent newVal) -> {
                if (oldVal instanceof Region) {
                    final Region region = (Region) oldVal;
                    region.widthProperty().removeListener(sizeListener);
                    region.heightProperty().removeListener(sizeListener);
                }
                if (newVal instanceof Region) {
                    final Region region = (Region) newVal;
                    region.widthProperty().addListener(sizeListener);
                    region.heightProperty().addListener(sizeListener);
                    if (maximized.get()) {
                        adjustMaximized();
                    }
                    sizeListener.changed(null, null, null); //adjust to new parent
                }
            }
        );
        maximized.addListener(
            (ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
                adjustMaximized();
            }
        );
        //init from settings
        maximized.set(settings.getProperty(propertyID + ".maximized", "false").equals("true"));
        setPrefWidth(Double.parseDouble(settings.getProperty(propertyID + ".width", String.valueOf(MIN_WIDTH))));
        setPrefHeight(Double.parseDouble(settings.getProperty(propertyID + ".height", String.valueOf(MIN_HEIGHT))));
        setLayoutX(Double.parseDouble(settings.getProperty(propertyID + ".x", "0")));
        setLayoutY(Double.parseDouble(settings.getProperty(propertyID + ".y", "0")));
        //
        getRightIcons().add(0, new MaximizeIcon(this));
        addEventFilter(MouseEvent.MOUSE_PRESSED, e -> this.toFront());
        layoutXProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                adjustX(newVal.doubleValue());
            }
        );
        layoutYProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                adjustY(newVal.doubleValue());
            }
        );
        prefWidthProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                adjustWidth(newVal.doubleValue());
            }
        );
        prefHeightProperty().addListener(
            (ObservableValue<? extends Number> ov, Number oldVal, Number newVal) -> {
                adjustHeight(newVal.doubleValue());
            }
        );
        //
        setTitleBarStyleClass("my-window-titlebar");
    }

    /**
     * Returns maximized property.
     * @return maximized property
     */
    public BooleanProperty maximizedProperty() {
        return maximized;
    }

    /**
     * Returns whether the window is maximized.
     * @return true if the window is maximized, false otherwise
     */
    public boolean isMaximized() {
        return maximized.get();
    }

    /**
     * Adjusts height to newVal if possible.
     * @param newVal new height
     */
    protected void adjustHeight(final double newVal) {
        if (!isResizableWindow()) {
            return;
        }
        if (!maximized.get()) {
            settings.setProperty(propertyID + ".height", Double.toString(newVal));
        }
        this.requestLayout();
        final Parent p = getParent();
        if (p != null ) {
            final Bounds b = p.getLayoutBounds();
            if (b.getHeight() < getLayoutY() + newVal) {
                setPrefHeight(b.getHeight() - getLayoutY());
            }
        }
    }

    /**
     * (Un)bind size and position according to {@link #maximized}.
     */
    protected void adjustMaximized() {
        this.setCursor(Cursor.DEFAULT);
        if (maximized.get()) {
            unmaximizedHeight = getPrefHeight();
            unmaximizedWidth = getPrefWidth();
            unmaximizedX = layoutXProperty().get();
            unmaximizedY = layoutYProperty().get();
            layoutXProperty().set(0);
            layoutYProperty().set(0);
            final Parent parent = getParent();
            setResizableWindow(false);
            if (parent instanceof Region) {
                bindPrefSize((Region) parent);
            } else {
                //TODO binding to non-region parent's size
            }
        } else {
            prefHeightProperty().unbind();
            prefWidthProperty().unbind();
            setResizableWindow(true);
            setPrefHeight(unmaximizedHeight);
            setPrefWidth(unmaximizedWidth);
            super.layoutChildren();
            setLayoutX(unmaximizedX);
            setLayoutY(unmaximizedY);
        }
    }

    /**
     * Adjusts width to newVal if possible.
     * @param newVal new width
     */
    protected void adjustWidth(final double newVal) {
        if (!isResizableWindow()) {
            return;
        }
        if (!maximized.get()) {
            settings.setProperty(propertyID + ".width", Double.toString(newVal));
        }
        this.requestLayout();
        final Parent p = getParent();
        if (p != null ) {
            final Bounds b = p.getLayoutBounds();
            if (b.getWidth() < getLayoutX() + newVal) {
                final double newWidth = b.getWidth() - getLayoutX();
                if (newWidth >= minWidth(0)) {
                    setPrefWidth(newWidth);
                }
            }
        }
    }

    /**
     * Adjusts x-coord to newVal if possible.
     * @param newVal new x-coord
     */
    protected void adjustX(final double newVal) {
        if (!maximized.get()) {
            settings.setProperty(propertyID + ".x", Double.toString(newVal));
        }
        if (newVal < 0) {
            layoutXProperty().set(0);
        } else {
            final Parent p = getParent();
            if (p != null ) {
                final Bounds b = p.getLayoutBounds();
                if (b.getWidth() < newVal + getPrefWidth() && newVal != 0) {
                    layoutXProperty().set(b.getWidth() - getPrefWidth());
                }
            }
        }
    }

    /**
     * Adjusts y-coord to newVal if possible.
     * @param newVal new y-coord
     */
    protected void adjustY(final double newVal) {
        if (!maximized.get()) {
            settings.setProperty(propertyID + ".y", Double.toString(newVal));
        }
        if (newVal < 0) {
            layoutYProperty().set(0);
        } else {
            final Parent p = getParent();
            if (p != null ) {
                final Bounds b = p.getLayoutBounds();
                if (b.getHeight() < newVal + getPrefHeight() && newVal != 0) {
                    layoutYProperty().set(b.getHeight()- getPrefHeight());
                }
            }
        }
    }

    /**
     * Should bind the prefHeight and prefWidth to the region's size.
     * @param region parent region
     */
    protected void bindPrefSize(final Region region) {
        prefHeightProperty().bind(region.heightProperty());
        prefWidthProperty().bind(region.widthProperty());
    }

    /**
     * Toggles the {@link #maximized} property.
     */
    public void toggleMaximize() {
        maximized.set(!maximized.get());
        settings.setProperty(propertyID + ".maximized", maximized.get() ? "true" : "false");
    }
}
