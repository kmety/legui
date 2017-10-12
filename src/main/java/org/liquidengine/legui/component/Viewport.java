package org.liquidengine.legui.component;

import java.io.Serializable;

/**
 * Viewport interface specifies that component could be updated by event listeners by calling {@link #updateViewport()} method.
 */
public interface Viewport extends Serializable {

    /**
     * Used to update viewport content position.
     */
    void updateViewport();
}