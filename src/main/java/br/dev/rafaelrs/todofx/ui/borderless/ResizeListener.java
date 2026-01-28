/**
 * Copyright 2026 Rafael Rodrigues Sanches
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.dev.rafaelrs.todofx.ui.borderless;

import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.function.BiConsumer;

final class ResizeListener implements EventHandler<MouseEvent> {
    private final double resizeBorder;
    private final Stage stage;
    private final boolean isMaximizable;

    private BiConsumer<Double, Double> resizeAction;
    private Rectangle2D screenBounds;
    private double startScreenX;
    private double startScreenY;
    private double startStageX;
    private double startStageY;
    private double startStageH;
    private double startStageW;
    private double minStageH;
    private double minStageW;
    private double maxStageH;
    private double maxStageW;

    public ResizeListener(Stage stage, double resizeBorder, boolean isMaximizable) {
        this.resizeBorder = resizeBorder;
        this.stage = stage;
        this.isMaximizable = isMaximizable;
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        if(!stage.isResizable() || stage.isMaximized() || stage.isFullScreen()) {
            return;
        }

        final var eventType = mouseEvent.getEventType();

        if(MouseEvent.MOUSE_MOVED.equals(eventType)) {
            updateResizeActionAndCursor(mouseEvent);
        } else if(MouseEvent.MOUSE_PRESSED.equals(eventType)) {
            startResize(mouseEvent);
        } else if(MouseEvent.MOUSE_DRAGGED.equals(eventType)) {
            applyStageResize(mouseEvent);
            attemptMaximize();
        }
    }

    private void startResize(MouseEvent mouseEvent) {
        screenBounds = Screen.getPrimary().getVisualBounds();
        startScreenX = mouseEvent.getScreenX();
        startScreenY = mouseEvent.getScreenY();
        startStageX = stage.getX();
        startStageY = stage.getY();
        startStageH = stage.getHeight();
        startStageW = stage.getWidth();
        minStageH = Math.max(stage.getMinHeight(), resizeBorder * 2);
        minStageW = Math.max(stage.getMinWidth(), resizeBorder * 2);
        maxStageH = Math.min(stage.getMaxHeight(), screenBounds.getHeight());
        maxStageW = Math.min(stage.getMaxWidth(), screenBounds.getWidth());
    }

    private void updateResizeActionAndCursor(MouseEvent mouseEvent) {
        final var x = mouseEvent.getX();
        final var y = mouseEvent.getY();
        final var h = stage.getHeight();
        final var w = stage.getWidth();
        var cursor = Cursor.DEFAULT;
        resizeAction = null;

        if(x < resizeBorder) {
            if(y < resizeBorder) {
                resizeAction = this::resizeActionNorthwest;
                cursor = Cursor.NW_RESIZE;
            } else if(h - y < resizeBorder) {
                resizeAction = this::resizeActionSouthwest;
                cursor = Cursor.SW_RESIZE;
            } else {
                resizeAction = this::resizeActionWest;
                cursor = Cursor.W_RESIZE;
            }
        } else if(w - x < resizeBorder) {
            if(y < resizeBorder) {
                resizeAction = this::resizeActionNortheast;
                cursor = Cursor.NE_RESIZE;
            } else if(h - y < resizeBorder) {
                resizeAction = this::resizeActionSoutheast;
                cursor = Cursor.SE_RESIZE;
            } else {
                resizeAction = this::resizeActionEast;
                cursor = Cursor.E_RESIZE;
            }
        } else if(y < resizeBorder) {
            resizeAction = this::resizeActionNorth;
            cursor = Cursor.N_RESIZE;
        } else if(h - y < resizeBorder) {
            resizeAction = this::resizeActionSouth;
            cursor = Cursor.S_RESIZE;
        }

        final var scene = stage.getScene();

        if(scene != null) {
            scene.setCursor(cursor);
        }
    }

    private void resizeActionNorthwest(double screenX, double screenY) {
        resizeActionNorth(screenX, screenY);
        resizeActionWest(screenX, screenY);
    }

    private void resizeActionSouthwest(double screenX, double screenY) {
        resizeActionSouth(screenX, screenY);
        resizeActionWest(screenX, screenY);
    }

    private void resizeActionNortheast(double screenX, double screenY) {
        resizeActionNorth(screenX, screenY);
        resizeActionEast(screenX, screenY);
    }

    private void resizeActionSoutheast(double screenX, double screenY) {
        resizeActionSouth(screenX, screenY);
        resizeActionEast(screenX, screenY);
    }

    private void resizeActionWest(double screenX, double screenY) {
        final var deltaX = startScreenX - screenX;
        final var newStageW = Math.clamp(startStageW + deltaX, minStageW, maxStageW);
        final var deltaW = newStageW - startStageW;

        stage.setWidth(newStageW);
        stage.setHeight(stage.getHeight());
        stage.setX(startStageX - deltaW);
    }

    private void resizeActionEast(double screenX, double screenY) {
        final var deltaX = screenX - startScreenX;

        stage.setWidth(Math.clamp(startStageW + deltaX, minStageW, maxStageW));
        stage.setHeight(stage.getHeight());
    }

    private void resizeActionNorth(double screenX, double screenY) {
        final var deltaY = startScreenY - screenY;
        final var newStageH = Math.clamp(startStageH + deltaY, minStageH, maxStageH);
        final var deltaH = newStageH - startStageH;

        stage.setHeight(newStageH);
        stage.setWidth(stage.getWidth());
        stage.setY(startStageY - deltaH);
    }

    private void resizeActionSouth(double screenX, double screenY) {
        final var deltaY = screenY - startScreenY;

        stage.setHeight(Math.clamp(startStageH + deltaY, minStageH, maxStageH));
        stage.setWidth(stage.getWidth());
    }

    private boolean isDimensionsEqualScreenBounds() {
        return stage.getHeight() == screenBounds.getHeight() &&
               stage.getWidth() == screenBounds.getWidth();
    }

    private void attemptMaximize() {
        if(isMaximizable && isDimensionsEqualScreenBounds()) {
            final var scene = stage.getScene();

            stage.setMaximized(true);

            if(scene != null) {
                scene.setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void applyStageResize(MouseEvent mouseEvent) {
        if(resizeAction != null) {
            final var screenX = mouseEvent.getScreenX();
            final var screenY = mouseEvent.getScreenY();

            resizeAction.accept(screenX, screenY);
        }
    }
}
