package org.liquidengine.legui.layout.gridlayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ShchAlexander on 23.01.2018.
 */
public class GridMatrix<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 1;
    private Lock lock = new ReentrantLock();

    private Object[] mat;
    private int columnCount;
    private int rowCount;

    public GridMatrix(int columns, int rows) {
        if (columns < 0) {
            columns = DEFAULT_INITIAL_CAPACITY;
        }
        if (rows < 0) {
            rows = DEFAULT_INITIAL_CAPACITY;
        }

        this.columnCount = columns;
        this.rowCount = rows;
        mat = new Object[columnCount * rowCount];
    }

    public GridMatrix() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_INITIAL_CAPACITY);
    }

    public void addRow() {
        doInLock(() -> {
            int oldCC = columnCount;
            int oldRC = rowCount;

            int newRC = oldRC + 1;

            Object[] oldMat = mat;
            Object[] newMat = new Object[oldCC * newRC];
            for (int row = 0; row < oldRC; row++) {
                for (int col = 0; col < oldCC; col++) {
                    newMat[getIndex(col, row, oldCC, oldRC)] = oldMat[getIndex(col, row, oldCC, oldRC)];
                }
            }
            this.mat = newMat;
            this.rowCount = newRC;
        });
    }

    public void addRow(int index) {
        doInLock(() -> {
            int oldCC = columnCount;
            int oldRC = rowCount;

            int newRC = oldRC + 1;

            Object[] oldMat = mat;
            Object[] newMat = new Object[oldCC * newRC];
            for (int row = 0; row < index; row++) {
                System.arraycopy(oldMat, row * oldCC, newMat, row * oldCC, oldCC);
            }
            for (int row = index; row < oldRC; row++) {
                System.arraycopy(oldMat, row * oldCC, newMat, (row + 1) * oldCC, oldCC);
            }
            this.mat = newMat;
            this.rowCount = newRC;
        });
    }

    public void removeRow(int index) {
        doInLock(() -> {
            if (this.columnCount > 0 && this.rowCount > 1) {
                int oldCC = this.columnCount;
                int oldRC = this.rowCount;

                int newRC = oldRC - 1;

                Object[] oldMat = mat;
                Object[] newMat = new Object[oldCC * newRC];
                for (int row = 0; row < index; row++) {
                    System.arraycopy(oldMat, row * oldCC, newMat, row * oldCC, oldCC);
                }
                for (int row = index; row < newRC; row++) {
                    System.arraycopy(oldMat, (row + 1) * oldCC, newMat, row * oldCC, oldCC);
                }
                this.mat = newMat;
                this.rowCount = newRC;
            }
        });
    }

    public void addColumn() {
        doInLock(() -> {
            int oldCC = columnCount;
            int oldRC = rowCount;

            int newCC = oldCC + 1;

            Object[] oldMat = mat;
            Object[] newMat = new Object[newCC * oldRC];
            for (int row = 0; row < oldRC; row++) {
                System.arraycopy(oldMat, row * oldCC, newMat, row * newCC, oldCC);
            }
            this.mat = newMat;
            this.columnCount = newCC;
        });
    }

    public void addColumn(int index) {
        doInLock(() -> {
            int oldCC = columnCount;
            int oldRC = rowCount;

            int newCC = oldCC + 1;

            Object[] oldMat = mat;
            Object[] newMat = new Object[newCC * oldRC];
            for (int row = 0; row < oldRC; row++) {
                for (int col = 0; col < index; col++) {
                    newMat[row * newCC + col] = oldMat[row * oldCC + col];
                }
                for (int col = index; col < oldCC; col++) {
                    newMat[getIndex((col + 1), row, newCC, oldRC)] = oldMat[getIndex(col, row, oldCC, oldRC)];
                }
            }
            this.mat = newMat;
            this.columnCount = newCC;
        });
    }

    public void removeColumn(int index) {
        doInLock(() -> {
            if (columnCount > 1 && rowCount > 0) {
                int oldCC = columnCount;
                int oldRC = rowCount;

                int newCC = oldCC - 1;

                Object[] oldMat = mat;
                Object[] newMat = new Object[newCC * oldRC];
                for (int row = 0; row < oldRC; row++) {
                    for (int col = 0; col < index; col++) {
                        newMat[getIndex(col, row, newCC, oldRC)] = oldMat[getIndex(col, row, oldCC, oldRC)];
                    }
                    for (int col = index; col < newCC; col++) {
                        newMat[getIndex(col, row, newCC, oldRC)] = oldMat[getIndex(col + 1, row, oldCC, oldRC)];
                    }
                }
                this.mat = newMat;
                this.columnCount = newCC;
            }
        });
    }

    public T set(int column, int row, T value) {
        lock.lock();
        try {
            check(column, row);
            T t = get(column, row);
            mat[getIndex(column, row, columnCount, rowCount)] = value;
            return t;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Adds object to first free position in matrix.
     *
     * @param object object to add.
     *
     * @return true if added.
     */
    public boolean add(T object) {
        lock.lock();
        try {
            if (object != null) {
                int columnCount = getColumnCount();
                int rowCount = getRowCount();
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < columnCount; col++) {
                        if (mat[getIndex(col, row, columnCount, rowCount)] == null) {
                            mat[getIndex(col, row, columnCount, rowCount)] = object;
                            return true;
                        }
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public T remove(int column, int row) {
        lock.lock();
        try {
            check(column, row);
            T t = get(column, row);
            mat[getIndex(column, row, columnCount, rowCount)] = null;
            return t;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(T object) {
        lock.lock();
        try {
            if (object != null) {
                for (int row = 0; row < rowCount; row++) {
                    for (int col = 0; col < columnCount; col++) {
                        int index = getIndex(col, row, columnCount, rowCount);
                        if (object.equals(mat[index])) {
                            mat[index] = null;
                            return true;
                        }
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void check(int column, int row) {
        if (column > columnCount || row > rowCount) {
            throw new IndexOutOfBoundsException(
                "Column (Index: " + column + ", Size: " + columnCount + ")," +
                    "Row (Index: " + row + ", Size: " + rowCount + ")."
            );
        }
    }

    public void removeAll() {
        lock.lock();
        try {
            for (int row = 0; row < columnCount; row++) {
                for (int col = 0; col < rowCount; col++) {
                    mat[getIndex(col, row, columnCount, rowCount)] = null;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public T get(int column, int row) {
        lock.lock();
        try {
            check(column, row);
            return innerGet(column, row);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    private T innerGet(int column, int row) {
        return (T) mat[getIndex(column, row, columnCount, rowCount)];
    }

    public int getColumnCount() {
        return columnCount;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<List<T>> getMatrix() {
        lock.lock();
        try {
            List<List<T>> matrixL = new ArrayList<>();
            for (int col = 0; col < columnCount; col++) {
                List<T> e = new ArrayList<>();
                for (int row = 0; row < rowCount; row++) {
                    int index = getIndex(col, row, columnCount, rowCount);
                    e.add((T) mat[index]);
                }
                matrixL.add(e);
            }
            return matrixL;
        } finally {
            lock.unlock();
        }
    }

    public List<T> getRow(int row) {
        lock.lock();
        try {
            if (row > rowCount) {
                throw new IndexOutOfBoundsException(
                    "Row (Index: " + row + ", Size: " + rowCount + ")."
                );
            }
            List<T> rowElements = new ArrayList<>();
            int columnCount = getColumnCount();
            for (int column = 0; column < columnCount; column++) {
                rowElements.add(innerGet(column, row));
            }
            return rowElements;
        } finally {
            lock.unlock();
        }
    }

    public List<T> getColumn(int column) {
        if (column > columnCount) {
            throw new IndexOutOfBoundsException(
                "Column (Index: " + column + ", Size: " + columnCount + ")."
            );
        }
        List<T> columnElements = new ArrayList<>();
        int rowCount = getRowCount();
        for (int row = 0; row < rowCount; row++) {
            columnElements.add(innerGet(column, row));
        }
        return columnElements;
    }


    private void doInLock(Runnable r) {
        if (r == null) {
            return;
        }
        lock.lock();
        try {
            r.run();
        } finally {
            lock.unlock();
        }
    }

    private int getIndex(int column, int row, int columnCount, int rowCount) {
        return row * columnCount + column;
    }
}
