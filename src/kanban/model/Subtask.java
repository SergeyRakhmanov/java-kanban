package kanban.model;

import kanban.service.TaskType;

public class Subtask extends Task {
    protected Integer epicID;

    public Integer getEpicID() {
        return epicID;
    }

    public void setEpicID(Integer ID) {
        this.epicID = ID;
    }

    //при создании сабтаска принимаем на вход эпик в который он входит
    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{uid=" + uid +
                ", epicID=" + epicID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public String toSave() { //для сохранения вводим отдельный метод, чтобы проверять по удобному toString
        return (uid + "," + TaskType.SUBTASK + "," + name + "," + status + "," + description + "," + epicID + ",");
    }
}