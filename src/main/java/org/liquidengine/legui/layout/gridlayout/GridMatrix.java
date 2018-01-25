package org.liquidengine.legui.layout.gridlayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShchAlexander on 23.01.2018.
 */
public class GridMatrix<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 1;
    private Object[][] matrix;

    public GridMatrix(int columns, int rows) {
        if (columns < 0) {
            columns = DEFAULT_INITIAL_CAPACITY;
        }
        if (rows < 0) {
            rows = DEFAULT_INITIAL_CAPACITY;
        }
        matrix = new Object[columns][rows];
    }

    public GridMatrix() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
    }

    public void addRow() {
        Object[][] oldMatrix = matrix;
        Object[][] newMatrix = new Object[oldMatrix.length][oldMatrix[0].length + 1];

        for (int col = 0; col < oldMatrix.length; col++) {
            for (int row = 0; row < oldMatrix[0].length; row++) {
                newMatrix[col][row] = oldMatrix[col][row];
            }
        }
        this.matrix = newMatrix;
    }

    public void addRow(int index) {
        Object[][] oldMatrix = matrix;
        Object[][] newMatrix = new Object[oldMatrix.length][oldMatrix[0].length + 1];

        for (int col = 0; col < oldMatrix.length; col++) {
            for (int row = 0; row < index; row++) {
                newMatrix[col][row] = oldMatrix[col][row];
            }
            for (int row = index; row < oldMatrix[0].length; row++) {
                newMatrix[col][row + 1] = oldMatrix[col][row];
            }
        }
        this.matrix = newMatrix;
    }

    public void removeRow(int index) {
        if (matrix.length > 1 && matrix[0].length > 1) {

            Object[][] oldMatrix = matrix;
            Object[][] newMatrix = new Object[oldMatrix.length][oldMatrix[0].length - 1];

            for (int col = 0; col < oldMatrix.length; col++) {
                for (int row = 0; row < index; row++) {
                    newMatrix[col][row] = oldMatrix[col][row];
                }
                for (int row = index; row < newMatrix[0].length; row++) {
                    newMatrix[col][row] = oldMatrix[col][row + 1];
                }
            }
            this.matrix = newMatrix;
        }
    }

    public void addColumn() {
        Object[][] oldMatrix = matrix;
        Object[][] newMatrix = new Object[oldMatrix.length + 1][oldMatrix[0].length];

        for (int row = 0; row < oldMatrix[0].length; row++) {
            for (int col = 0; col < oldMatrix.length; col++) {
                newMatrix[col][row] = oldMatrix[col][row];
            }
        }
        this.matrix = newMatrix;
    }

    public void addColumn(int index) {
        Object[][] oldMatrix = matrix;
        Object[][] newMatrix = new Object[oldMatrix.length + 1][oldMatrix[0].length];

        for (int row = 0; row < oldMatrix[0].length; row++) {
            for (int col = 0; col < index; col++) {
                newMatrix[col][row] = oldMatrix[col][row];
            }
            for (int col = index; col < oldMatrix.length; col++) {
                newMatrix[col + 1][row] = oldMatrix[col][row];
            }
        }
        this.matrix = newMatrix;
    }

    public void removeColumn(int index) {
        if (matrix.length > 1 && matrix[0].length > 1) {

            Object[][] oldMatrix = matrix;
            Object[][] newMatrix = new Object[oldMatrix.length - 1][oldMatrix[0].length];

            for (int row = 0; row < oldMatrix[0].length; row++) {
                for (int col = 0; col < index; col++) {
                    newMatrix[col][row] = oldMatrix[col][row];
                }
                for (int col = index; col < newMatrix.length; col++) {
                    newMatrix[col][row] = oldMatrix[col + 1][row];
                }
            }
            this.matrix = newMatrix;
        }
    }

    public T set(int column, int row, T value) {
        check(column, row);
        T t = get(column, row);
        matrix[column][row] = value;
        return t;
    }

    /**
     * Adds object to first free position in matrix.
     *
     * @param object object to add.
     *
     * @return true if added.
     */
    public boolean add(T object) {
        if (object != null) {
            int columnCount = getColumnCount();
            int rowCount = getRowCount();
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < columnCount; col++) {
                    if (matrix[col][row] == null) {
                        matrix[col][row] = object;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public T remove(int column, int row) {
        check(column, row);
        T t = get(column, row);
        matrix[column][row] = null;
        return t;
    }

    public boolean remove(T object) {
        if (object != null) {
            int columnCount = getColumnCount();
            int rowCount = getRowCount();
            for (int col = 0; col < columnCount; col++) {
                for (int row = 0; row < rowCount; row++) {
                    if (object.equals(matrix[col][row])) {
                        matrix[col][row] = null;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void check(int column, int row) {
        if (column > matrix.length || row > matrix[0].length) {
            throw new IndexOutOfBoundsException(
                "Column (Index: " + column + ", Size: " + matrix.length + ")," +
                    "Row (Index: " + row + ", Size: " + matrix[0].length + ")."
            );
        }
    }

    public void removeAll() {
        for (int row = 0; row < matrix[0].length; row++) {
            for (int col = 0; col < matrix.length; col++) {
                matrix[col][row] = null;
            }
        }
    }

    public T get(int column, int row) {
        check(column, row);
        return innerGet(column, row);
    }

    @SuppressWarnings("unchecked")
    private T innerGet(int column, int row) {
        return (T) matrix[column][row];
    }

    public int getColumnCount() {
        return matrix.length;
    }

    public int getRowCount() {
        return matrix[0].length;
    }

    public List<List<T>> getMatrix() {
        List<List<T>> matrixL = new ArrayList<>();
        for (Object[] objects : matrix) {
            List<T> e = new ArrayList<>();
            for (Object object : objects) {
                e.add((T) object);
            }
            matrixL.add(e);
        }
        return matrixL;
    }

    public List<T> getRow(int row) {
        if (row > matrix[0].length) {
            throw new IndexOutOfBoundsException(
                "Row (Index: " + row + ", Size: " + matrix[0].length + ")."
            );
        }
        List<T> rowElements = new ArrayList<>();
        int columnCount = getColumnCount();
        for (int column = 0; column < columnCount; column++) {
            rowElements.add(innerGet(column, row));
        }
        return rowElements;
    }

    public List<T> getColumn(int column) {
        if (column > matrix.length) {
            throw new IndexOutOfBoundsException(
                "Column (Index: " + column + ", Size: " + matrix.length + ")."
            );
        }
        List<T> columnElements = new ArrayList<>();
        int rowCount = getRowCount();
        for (int row = 0; row < rowCount; row++) {
            columnElements.add(innerGet(column, row));
        }
        return columnElements;
    }
}
