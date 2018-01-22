package org.liquidengine.legui.layout.gridlayout;

import static org.lwjgl.util.yoga.Yoga.YGAlignFlexStart;
import static org.lwjgl.util.yoga.Yoga.YGAlignStretch;
import static org.lwjgl.util.yoga.Yoga.YGDirectionLTR;
import static org.lwjgl.util.yoga.Yoga.YGEdgeBottom;
import static org.lwjgl.util.yoga.Yoga.YGEdgeLeft;
import static org.lwjgl.util.yoga.Yoga.YGEdgeRight;
import static org.lwjgl.util.yoga.Yoga.YGEdgeTop;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionColumn;
import static org.lwjgl.util.yoga.Yoga.YGFlexDirectionRow;
import static org.lwjgl.util.yoga.Yoga.YGJustifyFlexStart;
import static org.lwjgl.util.yoga.Yoga.YGJustifySpaceAround;
import static org.lwjgl.util.yoga.Yoga.YGNodeCalculateLayout;
import static org.lwjgl.util.yoga.Yoga.YGNodeFree;
import static org.lwjgl.util.yoga.Yoga.YGNodeInsertChild;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetLeft;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetTop;
import static org.lwjgl.util.yoga.Yoga.YGNodeLayoutGetWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeNew;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetAlignItems;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexDirection;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexGrow;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetFlexShrink;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetJustifyContent;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinHeight;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMinWidth;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetPadding;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetWidth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.layout.Layout;
import org.liquidengine.legui.layout.LayoutConstraint;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class GridLayout implements Layout {

    private Map<Integer, GridRow> rows = new HashMap<>();
    private Map<Integer, GridColumn> columns = new HashMap<>();
    private Map<Component, GridLayoutConstraint> layoutConstraintMap = new HashMap<>();

    private BidiMap<Component, GridLayoutConstraint> componentMap = new DualHashBidiMap<>();

    public GridLayout(Collection<GridColumn> columns, Collection<GridRow> rows) {
        int columnIndex = 0;

        for (GridColumn column : columns) {
            if (column != null) {
                boolean contains = false;
                Collection<GridColumn> values = this.columns.values();
                for (GridColumn value : values) {
                    if (contains = value == column) {
                        break;
                    }
                }

                if (!contains) {
                    this.columns.put(columnIndex++, column);
                }
            }
        }

        int rowIndex = 0;
        for (GridRow row : rows) {
            if (row != null) {
                boolean contains = false;
                Collection<GridRow> values = this.rows.values();
                for (GridRow value : values) {
                    if (contains = value == row) {
                        break;
                    }
                }

                if (!contains) {
                    this.rows.put(rowIndex++, row);
                }
            }
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
        if (constraint == null) {
            throw new IllegalArgumentException("Layout constraint cannot be null! "
                + "Constraint should be instance of " + GridLayoutConstraint.class.getSimpleName() +
                " for " + GridLayout.class.getSimpleName() + " class.");
        }
        if (constraint instanceof GridLayoutConstraint) {
            GridLayoutConstraint gridLayoutConstraint = (GridLayoutConstraint) constraint;

            int row = gridLayoutConstraint.getRow();
            int column = gridLayoutConstraint.getColumn();

            componentMap.put(component, gridLayoutConstraint);
            rows.get(row).putComponent(column, component);
            columns.get(column).putComponent(row, component);

            layoutConstraintMap.put(component, gridLayoutConstraint);
        } else {
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
        if (componentMap.containsKey(component)) {
            GridLayoutConstraint gridLayoutConstraint = componentMap.remove(component);
            int column = gridLayoutConstraint.getColumn();
            int row = gridLayoutConstraint.getRow();
            rows.get(row).removeComponent(column);
            columns.get(column).removeComponent(row);
        }
    }

    /**
     * Used to lay out child components for parent component.
     *
     * @param parent component to lay out.
     */
    @Override
    public void layout(Component parent) {
        long rootNode = YGNodeNew();
        Map<Integer, GridRow> rows = new HashMap<>(this.rows);
        Map<Integer, GridColumn> columns = new HashMap<>(this.columns);

        int rowCount = rows.size();
        int columnCount = columns.size();

        long[] rowNodes = new long[rowCount];
        long[] cellNodes = new long[rowCount * columnCount];

        prepareRootNode(parent, rootNode);

        float rwidth = 0;
        for (int x = 0; x < columnCount; x++) {
            GridColumn gridColumn = columns.get(x);
            rwidth += gridColumn.getWidth();
        }

        for (int y = 0; y < rowCount; y++) {
            GridRow gridRow = rows.get(y);
            long rowNode = YGNodeNew();
            rowNodes[y] = rowNode;
            prepareRowNode(gridRow, rowNode, rwidth);
            YGNodeInsertChild(rootNode, rowNode, y);

            for (int x = 0; x < columnCount; x++) {
                int index = y * columnCount + x;

                GridColumn gridColumn = columns.get(x);
                long cellNode = YGNodeNew();
                cellNodes[index] = cellNode;

                YGNodeStyleSetHeight(cellNode, gridRow.getHeight());
                YGNodeStyleSetWidth(cellNode, gridColumn.getWidth());

                YGNodeStyleSetMinHeight(cellNode, gridRow.getHeight());
                YGNodeStyleSetMinWidth(cellNode, gridColumn.getWidth());

                YGNodeStyleSetMaxHeight(cellNode, gridRow.getHeight());
                YGNodeStyleSetMaxWidth(cellNode, gridColumn.getWidth());

                YGNodeStyleSetAlignItems(cellNode, YGAlignFlexStart);
                YGNodeStyleSetJustifyContent(cellNode, YGJustifyFlexStart);

                YGNodeInsertChild(rowNode, cellNode, x);
            }
        }

        Vector2f size = parent.getSize();
        YGNodeCalculateLayout(rootNode, size.x, size.y, YGDirectionLTR);

        for (int y = 0; y < rowCount; y++) {
            long rowNode = rowNodes[y];
            float rowOffsetX = YGNodeLayoutGetLeft(rowNode);
            float rowOffsetY = YGNodeLayoutGetTop(rowNode);
            for (int x = 0; x < columnCount; x++) {
                int index = y * columnCount + x;
                Component key = componentMap.getKey(new GridLayoutConstraint(x, y));
                if (key != null) {
                    long cellNode = cellNodes[index];
                    float width = YGNodeLayoutGetWidth(cellNode);
                    float height = YGNodeLayoutGetHeight(cellNode);

                    float x1 = YGNodeLayoutGetLeft(cellNode) + rowOffsetX;
                    float y1 = YGNodeLayoutGetTop(cellNode) + rowOffsetY;

                    key.setSize(width, height);
                    key.setPosition(x1, y1);
                }
            }
        }

        // free native memory
        for (long rowNode : rowNodes) {
            YGNodeFree(rowNode);
        }
        for (long cellNode : cellNodes) {
            YGNodeFree(cellNode);
        }
        YGNodeFree(rootNode);
    }

    private void prepareRowNode(GridRow gridRow, long rowNode, float rwidth) {
        YGNodeStyleSetFlexDirection(rowNode, YGFlexDirectionRow);

        YGNodeStyleSetHeight(rowNode, gridRow.getHeight());
        YGNodeStyleSetWidth(rowNode, rwidth);
        YGNodeStyleSetMinHeight(rowNode, gridRow.getHeight());
        YGNodeStyleSetMinWidth(rowNode, rwidth);
        YGNodeStyleSetMaxHeight(rowNode, gridRow.getHeight());
        YGNodeStyleSetMaxWidth(rowNode, rwidth);

        YGNodeStyleSetAlignItems(rowNode, YGAlignFlexStart);
        YGNodeStyleSetJustifyContent(rowNode, YGJustifyFlexStart);
    }

    private void prepareRootNode(Component parent, long rootNode) {
        YGNodeStyleSetFlexDirection(rootNode, YGFlexDirectionColumn);

        Vector2f minimumSize = parent.getStyle().getMinimumSize();
        if (minimumSize != null) {
            YGNodeStyleSetMinHeight(rootNode, minimumSize.y);
            YGNodeStyleSetMinWidth(rootNode, minimumSize.x);
        }
        Vector2f maximumSize = parent.getStyle().getMaximumSize();
        if (maximumSize != null) {
            YGNodeStyleSetMaxHeight(rootNode, maximumSize.y);
            YGNodeStyleSetMaxWidth(rootNode, maximumSize.x);
        }
        Vector2f prefSize = parent.getSize();
        if (prefSize != null) {
            YGNodeStyleSetHeight(rootNode, prefSize.y);
            YGNodeStyleSetWidth(rootNode, prefSize.x);
        }

        YGNodeStyleSetAlignItems(rootNode, YGAlignFlexStart);
        YGNodeStyleSetJustifyContent(rootNode, YGJustifyFlexStart);

        Vector4f padding = parent.getStyle().getPadding();
        if (padding != null) {
            YGNodeStyleSetPadding(rootNode, YGEdgeLeft, padding.x);
            YGNodeStyleSetPadding(rootNode, YGEdgeTop, padding.y);
            YGNodeStyleSetPadding(rootNode, YGEdgeRight, padding.z);
            YGNodeStyleSetPadding(rootNode, YGEdgeBottom, padding.w);
        }
    }
}
