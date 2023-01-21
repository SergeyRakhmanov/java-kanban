package kanban.service;
import kanban.model.Task;

import java.util.Objects;

public class Node {
    private Task task;
    private Integer prev;
    private Integer next;

    public Node(Task task, Integer prev) {
        this.task = task;
        this.prev = prev;
    }

    public Node(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public Integer getPrev() {
        return prev;
    }

    public void setPrev(Integer prev) {
        this.prev = prev;
    }

    public Integer getNext() {
        return next;
    }

    public void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(task, node.task) &&
                Objects.equals(prev, node.prev) &&
                Objects.equals(next, node.next);
    }

    @Override
    public int hashCode() {
        return (Objects.hash(task) + 17 * prev + 19 * next);
    }
}
