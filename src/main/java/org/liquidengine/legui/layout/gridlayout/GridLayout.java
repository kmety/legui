package org.liquidengine.legui.layout.gridlayout;

import static org.lwjgl.util.yoga.Yoga.YGDirectionLTR;
import static org.lwjgl.util.yoga.Yoga.YGDisplayFlex;
import static org.lwjgl.util.yoga.Yoga.YGDisplayNone;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumn;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRow;
import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;
import static org.lwjgl.util.yoga.Yoga.YGNodeFree;
import static org.lwjgl.util.yoga.Yoga.YGNodeInsertChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetLeft;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetTop;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeNew;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlex;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexGrow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidth;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.layout.Layout;
import org.liquidengine.legui.layout.LayoutConstraint;
import org.liquidengine.legui.style.Style;
import org.lwjgl.util.yoga.Yoga;

/**
 * Grid layout implementation. Used to lay out components as grid in
 */
public class GridLayout implements Layout {

    private GridMatrix<Component> components;
    private List<Float> rowHeights = new ArrayList<>();
    private List<Float> columnWidths = new ArrayList<>();

    public GridLayout() {
        components = new GridMatrix<>();
        columnWidths.add(100f);
        rowHeights.add(50f);
    }

    public GridLayout(int columns, int rows) {
        components = new GridMatrix<>(columns, rows);
        for (int i = 0; i < columns; i++) {
            columnWidths.add(100f);
        }
        for (int i = 0; i < rows; i++) {
            rowHeights.add(50f);
        }
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

    public List<Component> getRow(int row) {
        return components.getRow(row);
    }

    public List<Component> getColumn(int column) {
        return components.getColumn(column);
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
        components.remove(component);
    }

    /**
     * Used to lay out child components for parent component.
     *
     * @param parent component to lay out.
     */
    @Override
    public void layout(Component parent) {

        long rootNode = YGNodeNew();
        List<Long> columnNodes = new ArrayList<>();
        List<Long> cellNodes = new ArrayList<>();
        List<Long> compNodes = new ArrayList<>();

        Vector2f size = parent.getSize();

        List<Float> rowHeights = new ArrayList<>(this.rowHeights);
        List<Float> columnWidths = new ArrayList<>(this.columnWidths);
        List<List<Component>> matrix = components.getMatrix();
        int columnCount = matrix.size();
        int rowCount = matrix.get(0).size();

        if (rowHeights.size() != rowCount || columnWidths.size() != columnCount) {
            return;
        }

        YGNodeStyleSetFlex(rootNode, YGDisplayFlex);
        YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionRow);
        YGNodeStyleSetWidth(rootNode, size.x);
        YGNodeStyleSetHeight(rootNode, size.y);
        YGNodeStyleSetFlexGrow(rootNode, 1);

        for (int column = 0; column < columnCount; column++) {
            long columnNode = YGNodeNew();
            columnNodes.add(columnNode);

            YGNodeStyleSetFlex(columnNode, YGDisplayFlex);
            YGNodeStyleSetFlexDirection(columnNode, YGFlexDirectionColumn);
            YGNodeStyleSetWidth(columnNode, columnWidths.get(column));
            YGNodeStyleSetHeight(columnNode, size.y);

            for (int row = 0; row < rowCount; row++) {
                long cellNode = YGNodeNew();
                cellNodes.add(cellNode);

                YGNodeStyleSetFlex(cellNode, YGDisplayFlex);
                YGNodeStyleSetFlexDirection(cellNode, YGFlexDirectionRow);
                YGNodeStyleSetWidth(cellNode, columnWidths.get(column));
                YGNodeStyleSetHeight(cellNode, rowHeights.get(row));
                YGNodeStyleSetAlignItems(cellNode, Yoga.YGAlignStretch);
                YGNodeStyleSetJustifyContent(cellNode, Yoga.YGJustifyCenter);

                long compNode = YGNodeNew();
                compNodes.add(compNode);
                YGNodeStyleSetFlex(compNode, YGDisplayNone);

                Component component = matrix.get(column).get(row);
                if (component != null) {
                    Style style = component.getStyle();
                    Vector2f minimumSize = style.getMinimumSize();
                    if (minimumSize != null) {
                        Yoga.YGNodeStyleSetMinWidth(compNode, minimumSize.x);
                        Yoga.YGNodeStyleSetMinHeight(compNode, minimumSize.y);
                    }

                    Vector2f maximumSize = style.getMaximumSize();
                    if (maximumSize != null) {
                        Yoga.YGNodeStyleSetMaxWidth(compNode, maximumSize.x);
                        Yoga.YGNodeStyleSetMaxHeight(compNode, maximumSize.y);
                    }

                    Vector2f preferredSize = style.getPreferredSize();
                    if (preferredSize != null) {
                        Yoga.YGNodeStyleSetWidth(compNode, preferredSize.x);
                        Yoga.YGNodeStyleSetHeight(compNode, preferredSize.y);
                    }

                    Yoga.YGNodeStyleSetFlexGrow(compNode, 1);
                    Yoga.YGNodeStyleSetFlexShrink(compNode, 1);

                    Vector4f margin = style.getMargin();
                    if (margin != null) {
                        Yoga.YGNodeStyleSetMargin(compNode, Yoga.YGEdgeLeft, margin.x);
                        Yoga.YGNodeStyleSetMargin(compNode, Yoga.YGEdgeTop, margin.y);
                        Yoga.YGNodeStyleSetMargin(compNode, Yoga.YGEdgeRight, margin.z);
                        Yoga.YGNodeStyleSetMargin(compNode, Yoga.YGEdgeBottom, margin.w);
                    }
                    YGNodeInsertChild(cellNode, compNode, 0);
                }
                YGNodeInsertChild(columnNode, cellNode, row);
            }
            YGNodeInsertChild(rootNode, columnNode, column);
        }

        complexLayouting(rootNode, columnNodes, cellNodes, compNodes, size, matrix, columnCount, rowCount);

        // free native memory
        for (Long columnNode : columnNodes) {
            YGNodeFree(columnNode);
        }
        for (Long cellNode : cellNodes) {
            YGNodeFree(cellNode);
        }
        for (Long compNode : compNodes) {
            YGNodeFree(compNode);
        }
        YGNodeFree(rootNode);
    }

    private void complexLayouting(long rootNode, List<Long> columnNodes, List<Long> cellNodes, List<Long> compNodes, Vector2f size,
        List<List<Component>> matrix, int columnCount, int rowCount) {
        YGNodeCalculateLayout(rootNode, size.x, size.y, YGDirectionLTR);

        // apply calculations

        for (int column = 0; column < columnCount; column++) {
            Long columnNode = columnNodes.get(column);
            float left = YGNodeLayoutGetLeft(columnNode);
            for (int row = 0; row < rowCount; row++) {
                Component component = matrix.get(column).get(row);
                if (component != null) {
                    int index = column * rowCount + row;
                    Long compNode = compNodes.get(index);

                    float width = YGNodeLayoutGetWidth(compNode);
                    float height = component.getSize().y;
                    float x = left + YGNodeLayoutGetLeft(compNode);
                    float y = component.getPosition().y;
                    component.setSize(width, height);
                    component.setPosition(x, y);
                }
            }
        }

        for (Long cellNode : cellNodes) {
            YGNodeStyleSetFlexDirection(cellNode, YGFlexDirectionColumn);
        }
        YGNodeCalculateLayout(rootNode, size.x, size.y, YGDirectionLTR);

        // apply calculations

        for (int column = 0; column < columnCount; column++) {
            for (int row = 0; row < rowCount; row++) {
                Component component = matrix.get(column).get(row);
                if (component != null) {
                    int index = column * rowCount + row;
                    Long compNode = compNodes.get(index);
                    float top = YGNodeLayoutGetTop(cellNodes.get(index));

                    float width = component.getSize().x;
                    float height = YGNodeLayoutGetHeight(compNode);
                    float x = component.getPosition().x;
                    float y = top + YGNodeLayoutGetTop(compNode);
                    component.setSize(width, height);
                    component.setPosition(x, y);
                }
            }
        }
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

    public float getRowHeight(int index) {
        return rowHeights.get(index);
    }

    public float getColumnWidth(int index) {
        return columnWidths.get(index);
    }

    public int getColumnCount() {
        return components.getColumnCount();
    }

    public int getRowCount() {
        return components.getRowCount();
    }

    public List<List<Component>> getMatrix() {
        return components.getMatrix();
    }
}
