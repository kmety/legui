package org.liquidengine.legui.layout.gridlayout;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.liquidengine.legui.layout.LayoutConstraint;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class GridLayoutConstraint implements LayoutConstraint {

    private final int row;
    private final int column;

    public GridLayoutConstraint(int column, int row) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GridLayoutConstraint that = (GridLayoutConstraint) o;

        return new EqualsBuilder()
            .append(getRow(), that.getRow())
            .append(getColumn(), that.getColumn())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(getRow())
            .append(getColumn())
            .toHashCode();
    }
}
