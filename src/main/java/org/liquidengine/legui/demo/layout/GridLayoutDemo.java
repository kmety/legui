package org.liquidengine.legui.demo.layout;

import java.util.List;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.demo.Demo;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.layout.LayoutManager;
import org.liquidengine.legui.layout.gridlayout.GridLayout;
import org.liquidengine.legui.layout.gridlayout.GridLayoutConstraint;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.color.ColorUtil;
import org.lwjgl.glfw.GLFW;

/**
 * @author Aliaksandr_Shcherbin.
 */
public class GridLayoutDemo extends Demo {

    private Frame frame;

    public GridLayoutDemo(int width, int height, String title) {
        super(width, height, title);
    }

    public static void main(String[] args) {
        Demo demo = new GridLayoutDemo(800, 600, GridLayoutDemo.class.getSimpleName());
        demo.run();
    }

    @Override
    protected void update() {
        LayoutManager.getInstance().layout(frame);
    }

    @Override
    protected void createGuiElements(Frame frame, int width, int height) {
        this.frame = frame;

        Component container = frame.getContainer();

        int rows = 5;
        int columns = 3;

        GridLayout layout = new GridLayout(columns, rows);
        layout.setColumnWidth(0, 60f);
        layout.setColumnWidth(1, 80f);
        layout.setColumnWidth(2, 100f);

        layout.setRowHeight(0, 60f);
        layout.setRowHeight(1, 60f);
        layout.setRowHeight(2, 120f);
        layout.setRowHeight(3, 60f);
        layout.setRowHeight(4, 60f);

        container.setLayout(layout);

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                if (2 == y && x == 1) {
                    continue;
                }
                Panel panel = new Panel(10 + x * 100, 10 + y * 100, 50, 50);
                panel.getStyle().setMinimumSize(10, 10);
                panel.getStyle().setMaximumSize(210, 150);
                panel.getStyle().getBackground().setColor(ColorUtil.randomColor());
                container.add(panel, new GridLayoutConstraint(x, y));
                if (y == 2) {
                    panel.getStyle().setMaximumSize(210, 200);
                }
            }
        }

        Component component = container.getChildComponents().get(0);

        Button b = new Button("+R", 10, 10, 30, 30);
        b.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickAction.CLICK)) {
                addRow(container, layout);
            }
        });

        component.add(b);
        component = container.getChildComponents().get(5);

        Button bR = new Button("-R", 10, 10, 30, 30);
        bR.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickAction.CLICK)) {
                removeRow(container, layout);
            }
        });

        component.add(bR);

        component = container.getChildComponents().get(9);

        Button bRr = new Button("-RR", 10, 10, 30, 30);
        bRr.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
            if (event.getAction().equals(MouseClickAction.CLICK)) {
                removeMidEl(container, layout);
            }
        });

        component.add(bRr);

        keeper.getChainKeyCallback().add((window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_KP_ADD && action == GLFW.GLFW_RELEASE) {
                addRow(container, layout);
            }
            if (key == GLFW.GLFW_KEY_KP_MULTIPLY && action == GLFW.GLFW_RELEASE) {
                layout.addColumn(50);
                int x = layout.getColumnCount() - 1;
                for (int y = 0; y < layout.getRowCount(); y++) {
                    Panel panel = new Panel(10 + x * 100, 20 + y * 100, 50, 50);
                    panel.getStyle().setMinimumSize(10, 10);
                    panel.getStyle().setMaximumSize(20, 20);
                    panel.getStyle().getBackground().setColor(ColorConstants.lightGray());
                    container.add(panel, new GridLayoutConstraint(x, y));
                }
            }
        });
        keeper.getChainKeyCallback().add((window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_KP_SUBTRACT && action == GLFW.GLFW_RELEASE) {
                removeRow(container, layout);
            }
            if (key == GLFW.GLFW_KEY_KP_DIVIDE && action == GLFW.GLFW_RELEASE) {
                int x = 3;
                if (layout.getColumnCount() > x) {
                    List<Component> column = layout.getColumn(x);
                    layout.removeColumn(x);
                    for (Component colCell : column) {
                        if (colCell != null) {
                            container.remove(colCell);
                        }
                    }
                    System.out.println("done");
                }
            }
        });
    }

    private void addRow(Component container, GridLayout layout) {
        int y = 1;
        layout.addRow(y, 50);
        for (int x = 0; x < layout.getColumnCount(); x++) {
            Panel panel = new Panel(10 + x * 100, 20 + y * 100, 50, 50);
            panel.getStyle().setMinimumSize(10, 10);
            panel.getStyle().setMaximumSize(20, 20);
            panel.getStyle().getBackground().setColor(ColorConstants.lightGray());
            container.add(panel, new GridLayoutConstraint(x, y));
        }
    }

    private void removeMidEl(Component container, GridLayout layout) {
        Component c = layout.getMatrix().get(1).get(1);

        if (c != null) {
            container.remove(c);
        }
    }

    private void removeRow(Component container, GridLayout layout) {
        int y = 1;
        if (layout.getRowCount() > 1) {
            List<Component> row = layout.getRow(y);
            layout.removeRow(y);
            for (Component rowCell : row) {
                if (rowCell != null) {
                    container.remove(rowCell);
                }
            }
            System.out.println("done");

        }
    }
}
