package org.liquidengine.legui.component;

import java.io.Serializable;
import org.liquidengine.legui.component.optional.Orientation;

public interface Viewport extends Serializable {

    /**
     * Used to update viewport content position.
     */
    void updateViewport();

    /**
     * Used to get visible amount in specified orientation.
     *
     * @param orientation orientation to use.
     *
     * @return visible amount in specified orientation.
     */
    float getVisibleAmount(Orientation orientation);
}