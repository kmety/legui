package org.liquidengine.legui.component;

import org.joml.Vector2f;
import org.liquidengine.legui.event.system.SystemWindowSizeEvent;
import org.liquidengine.legui.listener.SystemEventListener;
import org.liquidengine.legui.util.ColorConstants;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Class represent frame layer. Every layer will be rendered in frame one by one, from latest(which on head) to first(which on tail).
 * Also layers can be used to render some overlay or popups.
 * <p>
 * Created by Shcherbin Alexander on 1/25/2017.
 */
public class Layer {
    /**
     * Layer container. Used to hold components.
     */
    protected LayerContainer container = new LayerContainer(this);

    /**
     * Previous layer (which lays under this)
     */
    protected Layer botLayer;

    /**
     * Next layer (which lays on top of this)
     */
    protected Layer topLayer;

    /**
     * Frame component of this layer
     */
    protected Frame frame;

    /**
     * Used to determine if events can be processed by bottom layer or not.
     */
    protected boolean permeable = true;

    public Layer() {
    }

    public Layer(boolean permeable) {
        this.permeable = permeable;
    }

    public LayerContainer getContainer() {
        return container;
    }

    public Frame getFrame() {
        return frame;
    }

    public Layer getBotLayer() {
        return botLayer;
    }

    public Layer getTopLayer() {
        return topLayer;
    }

    public boolean isPermeable() {
        return permeable;
    }

    public void setPermeable(boolean permeable) {
        this.permeable = permeable;
    }

    /**
     * Returns count of child components.
     *
     * @return count of child components.
     * @see List#size()
     */
    public int componentsCount() {
        return container.componentsCount();
    }

    /**
     * Returns true if container contains no elements.
     *
     * @return true if container contains no elements.
     * @see List#isEmpty()
     */
    public boolean isContainerEmpty() {
        return container.isContainerEmpty();
    }

    /**
     * Returns true if container contains specified component.
     *
     * @param component component to check.
     * @return true if container contains specified component.
     * @see List#contains(Object)
     */
    public boolean containsComponent(Component component) {
        return container.containsComponent(component);
    }

    /**
     * Returns an iterator over the elements in this container.
     * The elements are returned in no particular order.
     *
     * @return an iterator over the elements in this container.
     * @see List#iterator()
     */
    public Iterator<Component> containerIterator() {
        return container.containerIterator();
    }

    /**
     * Used to add component to container.
     *
     * @param component component to add.
     * @return true if component is added.
     * @see List#add(Object)
     */
    public boolean addComponent(Component component) {
        return container.addComponent(component);
    }

    /**
     * Used to add components.
     *
     * @param components components nodes to add.
     * @return true if added.
     * @see List#addAll(Collection)
     */
    public boolean addAllComponents(Collection<? extends Component> components) {
        return container.addAllComponents(components);
    }

    /**
     * Used to remove component.
     *
     * @param component component to remove.
     * @return true if removed.
     * @see List#remove(Object)
     */
    public boolean removeComponent(Component component) {
        return container.removeComponent(component);
    }

    /**
     * Used to remove components.
     *
     * @param components components to remove.
     * @see List#removeAll(Collection)
     */
    public void removeAllComponents(Collection<? extends Component> components) {
        container.removeAllComponents(components);
    }

    /**
     * Removes all of the elements of this container
     * that satisfy the given predicate.
     * Errors or runtime exceptions
     * thrown during iteration or by
     * the predicate are relayed to the caller.
     *
     * @param filter a predicate which returns true for elements to be removed.
     * @return true if any components were removed.
     * @see List#removeIf(Predicate)
     */
    public boolean removeComponentIf(Predicate<? super Component> filter) {
        return container.removeComponentIf(filter);
    }

    /**
     * Used to remove all child components from container.
     *
     * @see List#clear()
     */
    public void clearComponents() {
        container.clearComponents();
    }

    /**
     * Returns true if this ComponentContainer contains all of the elements of the specified collection.
     *
     * @param components components collection to check.
     * @return true if this ComponentContainer contains all of the elements of the specified collection.
     * @see List#containsAll(Collection)
     */
    public boolean containerContainsAll(Collection<?> components) {
        return container.containerContainsAll(components);
    }

    /**
     * Returns a sequential Stream with this collection as its source.
     *
     * @return a sequential Stream with this collection as its source.
     * @see List#stream()
     */
    public Stream<Component> componentStream() {
        return container.componentStream();
    }

    /**
     * Returns a possibly parallel Stream with this collection as its source.
     * It is allowable for this method to return a sequential stream.
     *
     * @return possibly parallel Stream with this collection as its source.
     * @see List#parallelStream()
     */
    public Stream<Component> componentParallelStream() {
        return container.componentParallelStream();
    }

    /**
     * Performs the given action for each element of the Iterable
     * until all elements have been processed or the action throws an exception.
     *
     * @param action The action to be performed for each element.
     */
    public void forEachComponent(Consumer<? super Component> action) {
        container.forEachComponent(action);
    }

    /**
     * Used to retrieve child components as {@link List}
     * <p>
     * <p>
     * <span style="color:red">NOTE: this method returns {@link List} of components when components stored as {@link Set}</span>
     *
     * @return list of child components.
     */
    public List<Component> getComponents() {
        return container.getComponents();
    }

    /**
     * If this component is composite of several other components
     * it should return one of child components which intersects with cursor
     * else it should return {@code this}.
     * <p>
     * If cursor is outside of component method should return null.
     *
     * @param cursorPosition cursor position
     * @return component at cursor or null.
     */
    public Component getComponentAt(Vector2f cursorPosition) {
        return container.getComponentAt(cursorPosition);
    }

    //    public int componentsCount() {
//        return container.componentsCount();
//    }
//
//    public boolean isContainerEmpty() {
//        return container.isContainerEmpty();
//    }
//
//    public boolean containsComponent(Component component) {
//        return container.containsComponent(component);
//    }
//
//    public Iterator<Component> containerIterator() {
//        return container.containerIterator();
//    }
//
//    public boolean addComponent(Component component) {
//        return container.addComponent(component);
//    }
//
//    public boolean addAllComponents(Collection<? extends Component> components) {
//        return container.addAllComponents(components);
//    }
//
//    public boolean removeComponent(Component component) {
//        return container.removeComponent(component);
//    }
//
//    public void removeAllComponents(Collection<? extends Component> components) {
//        container.removeAllComponents(components);
//    }
//
//    public boolean removeComponentIf(Predicate<? super Component> filter) {
//        return container.removeComponentIf(filter);
//    }
//
//    public void clearComponents() {
//        container.clearComponents();
//    }
//
//    public boolean containerContainsAll(Collection<?> components) {
//        return container.containerContainsAll(components);
//    }
//
//    public Stream<Component> componentStream() {
//        return container.componentStream();
//    }
//
//    public Stream<Component> componentParallelStream() {
//        return container.componentParallelStream();
//    }
//
//    public void forEachComponent(Consumer<? super Component> action) {
//        container.forEachComponent(action);
//    }
//
//    public List<Component> getComponents() {
//        return container.getComponents();
//    }
//
//    public Component getComponentAt(Vector2f cursorPosition) {
//        return container.getComponentAt(cursorPosition);
//    }

    public static class LayerContainer extends ComponentContainer {
        protected Layer layer;

        public LayerContainer(Layer layer) {
            this.layer = layer;
            backgroundColor.set(ColorConstants.transparent);
            SystemEventListener<LayerContainer, SystemWindowSizeEvent> frameSystemWindowSizeEventSystemEventListener = (event, component, context) -> {
                LayerContainer.this.size.set(event.width, event.height);
                LayerContainer.this.components.forEach(c -> c.getSystemEventListeners().getListener(event.getClass()).update(event, c, context));
            };

            LayerContainer.this.systemEventListeners
                    .setListener(SystemWindowSizeEvent.class, frameSystemWindowSizeEventSystemEventListener);
        }

        public Layer getLayer() {
            return layer;
        }

    }
}
