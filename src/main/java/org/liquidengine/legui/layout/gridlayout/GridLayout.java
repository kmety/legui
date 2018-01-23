package org.liquidengine.legui.layout.gridlayout;

import java.util.ArrayList;
import java.util.List;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.layout.Layout;
import org.liquidengine.legui.layout.LayoutConstraint;

public class GridLayout implements Layout {

    private GridMatrix<Component> components;
    private List<Float> rowHeights = new ArrayList<>();
    private List<Float> columnWidths = new ArrayList<>();

    public GridLayout() {
        components = new GridMatrix<>();
        rowHeights.add(100f);
        columnWidths.add(100f);
    }

    public GridLayout(int columns, int rows) {
        components = new GridMatrix<>(columns, rows);
    }

    /**
     * Used to add component to layout.
     *
     * @param component component to add.
     * @param constraint layout constraint.
     */
    @Override
    public void addComponent(Component component, LayoutConstraint constraint) {
        checkConstraint(constraint);

        GridLayoutConstraint gridLayoutConstraint = (GridLayoutConstraint) constraint;

        int row = gridLayoutConstraint.getRow();
        int column = gridLayoutConstraint.getColumn();

        components.set(column, row, component);
    }

    private void checkConstraint(LayoutConstraint constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException("Layout constraint cannot be null! "
                + "Constraint should be instance of " + GridLayoutConstraint.class.getSimpleName() +
                " for " + GridLayout.class.getSimpleName() + " class.");
        } else if (!(constraint instanceof GridLayoutConstraint)) {
            throw new IllegalArgumentException("Layout constraint should be instance of " + GridLayoutConstraint.class.getSimpleName() +
                " for " + GridLayout.class.getSimpleName() + " class.");
        }
    }

    /**
     * Used to remove component from layout.
     *
     * @param component component to remove.
     */
    @Override
    public void removeComponent(Component component) {
        component.remove(component);
    }

    /**
     * Used to lay out child components for parent component.
     *
     * @param parent component to lay out.
     */
    @Override
    public void layout(Component parent) {

    }

    public void addRow(int index, float rowHeight) {
        components.addRow(index);
        rowHeights.add(index, rowHeight);
    }

    public void addRow(float rowHeight) {
        components.addRow();
        rowHeights.add(rowHeight);
    }

    public void removeRow(int index) {
        components.removeRow(index);
        rowHeights.remove(index);
    }

    public void addColumn(float columnWidth) {
        components.addColumn();
        columnWidths.add(columnWidth);
    }

    public void addColumn(int index, float columnWidth) {
        components.addColumn(index);
        columnWidths.add(index, columnWidth);
    }

    public void removeColumn(int index) {
        components.removeColumn(index);
        columnWidths.remove(index);
    }

    public void setRowHeight(int index, float rowHeight) {
        rowHeights.set(index, rowHeight);
    }

    public void setColumnWidth(int index, float columnWidth) {
        columnWidths.set(index, columnWidth);
    }
}
