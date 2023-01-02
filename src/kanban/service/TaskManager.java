package kanban.service;

import java.util.HashMap;
import kanban.model.*;

public interface TaskManager {

    public int createTask(Task task);

    public int createEpic(Epic epic);

    public int createSubtask(Subtask subtask);

    public HashMap<Integer, Task> getAllTasc();

    public HashMap<Integer, Epic> getAllEpic();

    public HashMap<Integer, Subtask> getAllSubtasc();

    public void deleteTaskById(int id);

    public void clearAllTasc();

    public void deleteSubtaskById(int id);

    public void clearAllSubtasc();

    public void deleteEpicById(int id);

    public void clearAllEpic();

    public HashMap<Integer, Subtask> getAllSubtasksByEpicId(int id);

    public Task getTaskById(int id);

    public Epic getEpicById(int id);

    public Subtask getSubtaskById(int id);

    public Boolean updateTask(Task task);

    public Boolean updateEpic(Epic epic);

    public Boolean updateSubtask(Subtask sub);
}
