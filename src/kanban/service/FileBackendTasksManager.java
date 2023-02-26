package kanban.service;

import kanban.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import static kanban.model.TaskStatus.*;

public class FileBackendTasksManager extends InMemoryManager {

    public void save() { //сохранение всех задачи и всей истории в файл
        try (Writer writer = new FileWriter("tasks.csv")) {
            writer.write("id,type,name,status,description,epic\n"); //пишем заглавную строку

            //сначала пишем в файл эпики, чтобы потом при восстановлении истории сначала восстановить эпики,
            // а потом сабтаски, так как сабтаски требуют прописать ID эпика при создании
            if (!listOfEpic.isEmpty()) {
                for (Epic epic : listOfEpic.values()) {
                    writer.write(epic.toSave() + "\n");
                }
            }

            if (!listOfSubtask.isEmpty()) {
                for (Subtask subTask : listOfSubtask.values()) { //пишем в файл сабтаски
                    writer.write(subTask.toSave() + "\n");
                }
            }

            if (!listOfTask.isEmpty()) {
                for (Task task : listOfTask.values()) { //пишем в файл таски
                    writer.write(task.toSave() + "\n");
                }
            }

            writer.write("\n"); //делаем пробел-разделитель

            //и пишем в файл историю просмотров
            try {
                writer.write(historyToString(Managers.getDefaultHistory()));
            } catch (NullPointerException e) {
                throw new ManagerSaveException("Нет истории для сохранения");
            }

        } catch (IOException e) {
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder(manager.getHistory().size() * 2); //2 на запятые между IDшниками

        for (int i = 0; i < manager.getHistory().size(); i++) {
            sb.append(manager.getHistory().get(i).getUid()); //добавляем в строку ID таски из истории
            sb.append(","); //и запятую между ними
        }

        return sb.toString();
    }

    //возвращает историю просмотра - массив значений ID задач из файла
    private static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        for (String item : value.split(",")) {
            history.add(Integer.parseInt(item));
        }
        return history;
    }

    //создает объект задачи из строки, котрая сохранена в файле
    private Task fromString(String str) throws ManagerSaveException {
        Task task = null;
        String[] items = str.split(",");
        switch (items[1]) {
            case ("EPIC"):
                task = new Epic(items[2], items[4]); //вызываю конструктор нового эпика
                task.setUid(Integer.parseInt(items[0])); //присваиваем ID эпику из сохраненного в файле
                switch (items[3]) { //определяем статус, сохраненный в файле и присваиваем его эпику
                    case ("NEW"):
                        task.setStatus(NEW);
                        break;
                    case ("DONE"):
                        task.setStatus(DONE);
                        break;
                    case ("IN_PROGRESS"):
                        task.setStatus(IN_PROGRESS);
                        break;
                    default:
                        throw new ManagerSaveException("Неизвестный тип статуса эпика ID=" + items[0]);
                }
                break;
            case ("SUBTASK"):
                task = new Subtask(items[2], items[4], Integer.parseInt(items[5]));//вызываю конструктор нового сабтаска
                task.setUid(Integer.parseInt(items[0])); //присваиваем ID сабтаске из сохраненного в файле
                switch (items[3]) { //определяем статус, сохраненный в файле и присваиваем его сабтаске
                    case ("NEW"):
                        task.setStatus(NEW);
                        break;
                    case ("DONE"):
                        task.setStatus(DONE);
                        break;
                    case ("IN_PROGRESS"):
                        task.setStatus(IN_PROGRESS);
                        break;
                    default:
                        throw new ManagerSaveException("Неизвестный тип статуса сабтаска ID=" + items[0]);
                }
                break;
            case ("TASK"):
                task = new Task(items[2], items[4]); //вызываю конструктор новой таски
                task.setUid(Integer.parseInt(items[0])); //присваиваем ID таски из сохраненного в файле
                switch (items[3]) { //определяем статус, сохраненный в файле и присваиваем его таске
                    case ("NEW"):
                        task.setStatus(NEW);
                        break;
                    case ("DONE"):
                        task.setStatus(DONE);
                        break;
                    case ("IN_PROGRESS"):
                        task.setStatus(IN_PROGRESS);
                        break;
                    default:
                        throw new ManagerSaveException("Неизвестный тип статуса таски ID=" + items[0]);
                }
                break;
            default:
                throw new ManagerSaveException("Неизвестный тип задачи");
        }
        return task;
    }

    //метод, который загружает файл и восстанавливает задачи и историю просмотров
    public static FileBackendTasksManager loadFromFile(File file) {
        FileBackendTasksManager loadedManager = new FileBackendTasksManager();

        try (BufferedReader br = new BufferedReader(new FileReader(file.getName()))) {
            boolean isHistory = false; //переменная для того чтобы переключится с создания задач на создание истории

            String str = br.readLine(); //это чтобы считать первую строку с заголовками и не обрабатывать её
            while (br.ready()) {
                str = br.readLine();
                int guid; //переменная для сохранения ID задач
                if (!isHistory) { //создаем задачу
                    if (str.isEmpty()) { //если строка пустая то меняем булеву переменную на создание истории
                        isHistory = true;
                    } else { //иначе создаем таску
                        Task task = loadedManager.fromString(str); //создаем объект из строки
                        guid = task.getUid();
                        //и в зависимости от типа созданной задачи записываем в менеджер
                        if (task instanceof Subtask) {
                            //guid ставим принудительно, иначе он перезатрется при создании
                            // и мы не сможем восстановить связи между эпиками и сабтасками
                            loadedManager.setGuid(guid);
                            loadedManager.createSubtask((Subtask) task);
                        } else if (task instanceof Epic) {
                            loadedManager.setGuid(guid);
                            loadedManager.createEpic((Epic) task);
                        } else {
                            loadedManager.setGuid(guid);
                            loadedManager.createTask(task);
                        }
                    }
                } else { //восстанавливаем историю просмотров
                    InMemoryHistoryManager history = new InMemoryHistoryManager();
                    for (int id : historyFromString(str)) { //преобразовали строку в список ID и проходимся по нему
                        //проверяем в каком из списков задач лежит ID из истории и вызываем соответсвувующий метод
                        if (loadedManager.getAllTasc().containsKey(id)) {
                            history.addTask(loadedManager.getTaskById(id));
                        } else if (loadedManager.getAllEpic().containsKey(id)) {
                            history.addTask(loadedManager.getEpicById(id));
                        } else if (loadedManager.getAllSubtasc().containsKey(id)){
                            history.addTask(loadedManager.getSubtaskById(id));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return loadedManager;
    }

    //далее переопределяем методы которые влияют на историю просмотров или на состояние задач
    // прописываем в них дополнительное действие по сохранению в файл
    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getUid();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic.getUid();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask.getUid();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void clearAllTasc() {
        super.clearAllTasc();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    protected void updateEpicStatus(int id) {
        super.updateEpicStatus(id);
        save();
    }

    @Override
    public void clearAllSubtasc() {
        super.clearAllSubtasc();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void clearAllEpic() {
        super.clearAllEpic();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Boolean updateTask(Task task) {
        Boolean res = super.updateTask(task);
        save();
        return res;
    }

    @Override
    public Boolean updateEpic(Epic epic) {
        Boolean res = super.updateEpic(epic);
        save();
        return res;
    }

    @Override
    public Boolean updateSubtask(Subtask sub) {
        Boolean res = super.updateSubtask(sub);
        save();
        return res;
    }
}
