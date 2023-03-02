package kanban.model;

import kanban.service.TaskType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Task {
    protected String name;
    protected String description;
    protected Integer uid;
    protected TaskStatus status;
    protected int duration;
    protected LocalDateTime startTime;

    //вычисляем дату и время окончания работы над задачей
    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else return null;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
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

    public Task(String name, String description, int id, int duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.uid = id;
        this.duration = duration;
        this.startTime = startTime;
        this.status = TaskStatus.NEW; //все задачи изначально NEW
    }


    @Override
    public String toString() {
        return "Task{uid=" + uid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    public String toSave() { //для сохранения вводим отдельный метод, чтобы проверять по удобному toString
        return (uid + "," + TaskType.TASK + "," + name + "," + status + "," + description + ","
                + startTime + "," + duration);
    }
}
