package org.liquidengine.legui.layout.gridlayout;

import java.util.HashMap;
import java.util.Map;
import org.liquidengine.legui.component.Component;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class GridColumn {

    private float width;
    private Map<Integer, Component> components = new HashMap<>();

    public GridColumn() {
    }

    public GridColumn(float width) {
        this.width = width;
    }

    public Component getComponent(int index) {
        return components.get(index);
    }

    void putComponent(int index, Component component) {
        components.put(index, component);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    void removeComponent(int index) {
        components.remove(index);
    }
}
