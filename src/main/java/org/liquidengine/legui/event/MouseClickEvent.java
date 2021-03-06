package org.liquidengine.legui.event;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.system.context.Context;

/**
 * Created by ShchAlexander on 2/10/2017.
 */
public class MouseClickEvent<T extends Component> extends Event<T> {

    private final MouseClickAction action;
    private final Mouse.MouseButton button;
    private final Vector2f position;
    private final Vector2f absolutePosition;

    public MouseClickEvent(T component, Context context, Frame frame, MouseClickAction action, Mouse.MouseButton button, Vector2f position,
        Vector2f absolutePosition) {
        super(component, context, frame);
        this.action = action;
        this.button = button;
        this.position = position;
        this.absolutePosition = absolutePosition;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("action", action)
            .append("button", button)
            .append("position", position)
            .append("absolutePosition", absolutePosition)
            .toString();
    }

    public MouseClickAction getAction() {
        return action;
    }

    public Mouse.MouseButton getButton() {
        return button;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getAbsolutePosition() {
        return absolutePosition;
    }

    public enum MouseClickAction {
        PRESS,
        CLICK,
        RELEASE,;
    }
}
