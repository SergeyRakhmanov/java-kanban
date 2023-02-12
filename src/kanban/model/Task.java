package kanban.model;

import kanban.service.TaskType;

public class Task {
    protected String name;
    protected String description;
    protected Integer uid;
    protected TaskStatus status;

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getUid() {
        return uid;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW; //все задачи изначально NEW
    }

    @Override
    public String toString() {
        return "Task{uid=" + uid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String toSave() { //для сохранения вводим отдельный метод, чтобы проверять по удобному toString
        return (uid + "," + TaskType.TASK + "," + name + "," + status + "," + description + ",");
    }
}
