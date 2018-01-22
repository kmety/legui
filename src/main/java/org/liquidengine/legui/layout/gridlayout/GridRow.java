package org.liquidengine.legui.layout.gridlayout;

import java.util.HashMap;
import java.util.Map;
import org.liquidengine.legui.component.Component;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class GridRow {

    private Float height;
    private Map<Integer, Component> components = new HashMap<>();

    public GridRow() {
    }

    public GridRow(Float height) {
        this.height = height;
    }

    public Component getComponent(int index) {
        return components.get(index);
    }

    void putComponent(int index, Component component) {
        components.put(index, component);
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    void removeComponent(int index) {
        components.remove(index);
    }
}
