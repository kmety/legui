package org.liquidengine.legui.system.handler;

import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.event.SystemEvent;

/**
 * Event handler interface for {@link SystemEvent}
 */
public interface SystemEventHandler<E extends SystemEvent> {

    /**
     * Used to handle system event.
     *
     * @param event event to handle.
     * @param frame target frame.
     * @param context target context.
     */
    void handle(E event, Frame frame, Context context);
}

