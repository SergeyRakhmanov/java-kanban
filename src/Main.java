import kanban.model.*;
import kanban.service.Manager;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

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

        //печатаем все списки
        System.out.println(manager.getAllTasc());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtasc());

        //меняем статус и апдейтим объекты
        task1.setStatus("DONE");
        manager.updateTask(task1);

        task2.setStatus("DONE");
        manager.updateTask(task2);

        subtask1.setStatus("DONE");
        manager.updateSubtask(subtask1);

        subtask2.setStatus("DONE");
        manager.updateSubtask(subtask2);

        subtask3.setStatus("DONE");
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
    }
}
