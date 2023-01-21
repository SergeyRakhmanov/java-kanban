import kanban.model.*;
import kanban.service.HistoryManager;
import kanban.service.Managers;
import kanban.service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();

        //создаем таски, эпики и сабтаски
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1",epic1.getUid());
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2",epic1.getUid());
        manager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Сабтаск 3", "Описание сабтаска 3",epic2.getUid());
        manager.createSubtask(subtask3);

        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        manager.createEpic(epic3);

        Subtask subtask4 = new Subtask("Сабтаск 4", "Описание сабтаска 4",epic3.getUid());
        manager.createSubtask(subtask4);

        Subtask subtask5 = new Subtask("Сабтаск 5", "Описание сабтаска 5",epic3.getUid());
        manager.createSubtask(subtask5);

        Subtask subtask6 = new Subtask("Сабтаск 6", "Описание сабтаска 6",epic3.getUid());
        manager.createSubtask(subtask6);

        //печатаем все списки
        System.out.println(manager.getAllTasc());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasc());

        //меняем статус и апдейтим объекты
        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);

        task2.setStatus(TaskStatus.DONE);
        manager.updateTask(task2);

        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask3);

        //печатаем все списки
        System.out.println();
        System.out.println(manager.getAllTasc());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasc());

        //удаляем объекты
        manager.deleteTaskById(task1.getUid());
        manager.deleteEpicById(epic1.getUid());

        //печатаем все списки
        System.out.println();
        System.out.println(manager.getAllTasc());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasc());

        //смотрим историю
        System.out.println();
        System.out.println("Проверка истории");

        manager.getEpicById(4); // смотрим эпик 4
        System.out.println("1. " + history.getHistory());

        manager.getTaskById(2); //смотрим таску 2
        System.out.println("2. " + history.getHistory());

        manager.getSubtaskById(7); //смотрим сабтаску 7
        System.out.println("3. " + history.getHistory());

        manager.getEpicById(4); // снова смотрим эпик 4
        System.out.println("4. " + history.getHistory());

        manager.deleteSubtaskById(7); //удаляем сабтаск 7
        System.out.println("5. " + history.getHistory());

        manager.getEpicById(8); // смотрим эпик 8
        System.out.println("6. " + history.getHistory());

        manager.getSubtaskById(9); //смотрим сабтаску 9
        manager.getSubtaskById(10); //смотрим сабтаску 10
        manager.getSubtaskById(11); //смотрим сабтаску 11
        System.out.println("7. " + history.getHistory());

        manager.getEpicById(4); // снова смотрим эпик с id=4
        System.out.println("8. " + history.getHistory());

        manager.deleteEpicById(8); //удаляем эпик с тремя сабтасками
        System.out.println("9. " + history.getHistory());
    }
}
