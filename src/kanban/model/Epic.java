package kanban.model;

import kanban.service.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    protected ArrayList<Integer> subtasksIds = new ArrayList<>(); //ID сабтасков по эпику

    public Epic(String name, String description) {
        super(name, description);
    }

    public void addSubtask(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void removeSubtask (int subId) { //удаление сабтаски из эпика
        subtasksIds.remove(subtasksIds.indexOf(subId));
    }

    @Override
    public String toString() {
        String result = "Epic{uid=" + uid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'';

        if (subtasksIds.isEmpty()) {
            result += ", subtasksIds=[null]";
        } else result += ", subtasksIds=" + subtasksIds;

        result += '}';
        return result;
    }

    @Override
    public String toSave() { //для сохранения вводим отдельный метод, чтобы проверять по удобному toString
        return (uid + "," + TaskType.EPIC + "," + name + "," + status + "," + description + ",");
    }
}
