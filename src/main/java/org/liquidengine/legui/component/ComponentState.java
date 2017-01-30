package org.liquidengine.legui.component;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This class represent three default states of ui component in interaction with mouse.
 * Created by Shcherbin Alexander on 11/17/2016.
 */
public class ComponentState {
    /**
     * Shows if component is currently focused.
     */
    protected transient boolean focused = false;
    /**
     * Shows if component is currently pressed.
     */
    protected transient boolean pressed = false;
    /**
     * Shows if component is currently hovered.
     */
    protected transient boolean hovered = false;

    /**
     * Returns true if component is focused.
     *
     * @return true if component is focused.
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Used to turn component to focused state.
     *
     * @param focused new focused state.
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * Returns true if component is pressed.
     *
     * @return true if component is pressed.
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Used to turn component to pressed state.
     *
     * @param pressed new pressed state.
     */
    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    /**
     * Returns true if component is hovered.
     *
     * @return true if component is hovered.
     */
    public boolean isHovered() {
        return hovered;
    }

    /**
     * Used to turn component to hovered state.
     *
     * @param hovered new hovered state.
     */
    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ComponentState)) return false;

        ComponentState that = (ComponentState) o;

        return new EqualsBuilder()
                .append(focused, that.focused)
                .append(pressed, that.pressed)
                .append(hovered, that.hovered)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(focused)
                .append(pressed)
                .append(hovered)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("focused", focused)
                .append("pressed", pressed)
                .append("hovered", hovered)
                .toString();
    }
}
