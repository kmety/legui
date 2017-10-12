package org.liquidengine.legui.system.renderer;

import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.system.context.Context;

/**
 * Renderer interface.
 * Created by ShchAlexander on 19.09.2017.
 */
public interface Renderer {

    /**
     * Used to initialize renderer. Called once per renderer life.
     */
    void initialize();

    /**
     * Used to render frame.
     * @param frame frame to render.
     * @param context context.
     */
    void render(Frame frame, Context context);

    /**
     * Used to destroy renderer. Called once per renderer life.
     */
    void destroy();
}
