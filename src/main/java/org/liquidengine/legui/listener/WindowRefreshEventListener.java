package org.liquidengine.legui.listener;

import org.liquidengine.legui.event.WindowRefreshEvent;

/**
 * Listener for window refresh event.
 */
public interface WindowRefreshEventListener extends EventListener<WindowRefreshEvent> {

    /**
     * Used to handle window refresh event.
     *
     * @param event window refresh event to handle.
     */
    void process(WindowRefreshEvent event);
}
