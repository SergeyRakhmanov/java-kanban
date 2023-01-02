package kanban.service;

import kanban.model.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected final List<Task> history = new ArrayList<>(10); //список для хранения истории

    @Override
    public void addTask(Task task) {
        //если в иcтории уже есть 10 элементов, то удаляем последний
        if (history.size()==10) {
            history.remove(9);
        }

        //добавляем элемент в массив на первое место, остальные при этом подвинутся на 1 позицию
        history.add(0, task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
