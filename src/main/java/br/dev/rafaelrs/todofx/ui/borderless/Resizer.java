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

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class Resizer {
    public static final boolean DEFAULT_MAXIMIZABLE_OPTION = true;
    public static final double DEFAULT_RESIZE_BORDER = 5;

    public static void addResizeListener(Stage stage, double resizeBorder, boolean isMaximizable) {
        if(resizeBorder < DEFAULT_RESIZE_BORDER) {
            throw new RuntimeException("Invalid border");
        }

        final var style = stage.getStyle();

        if(style != StageStyle.UNDECORATED && style != StageStyle.TRANSPARENT) {
            System.out.println("Not supported by resizer");
            return;
        }

        final var listener = new ResizeListener(stage, resizeBorder, isMaximizable);

        stage.addEventFilter(MouseEvent.MOUSE_DRAGGED, listener);
        stage.addEventFilter(MouseEvent.MOUSE_PRESSED, listener);
        stage.addEventFilter(MouseEvent.MOUSE_MOVED, listener);
    }

    public static void addResizeListener(Stage stage, double resizeBorder) {
        addResizeListener(stage, resizeBorder, DEFAULT_MAXIMIZABLE_OPTION);
    }

    public static void addResizeListener(Stage stage, boolean isMaximizable) {
        addResizeListener(stage, DEFAULT_RESIZE_BORDER, isMaximizable);
    }

    public static void addResizeListener(Stage stage) {
        addResizeListener(stage, DEFAULT_RESIZE_BORDER, DEFAULT_MAXIMIZABLE_OPTION);
    }
}
