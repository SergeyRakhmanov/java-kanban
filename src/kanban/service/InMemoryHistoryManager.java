package kanban.service;

import kanban.model.Task;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    protected final LinkedList<Task> history = new LinkedList<>(); //список для хранения истории

    @Override
    public void addTask(Task task) {
        //если в иcтории уже есть 10 элементов, то удаляем последний в истории == первый в списке
        if (history.size()==10) {
            history.removeFirst();
        }

        //добавляем элемент в массив на последнее место
        history.addLast(task);
    }

    @Override
    public LinkedList<Task> getHistory() {
        return history;
    }
}
