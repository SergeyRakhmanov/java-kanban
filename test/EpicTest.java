import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.TaskStatus;
import kanban.service.HistoryManager;
import kanban.service.Managers;
import kanban.service.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    //создаем переменные менеджера и истории на которых будем тестировать функционал
    private static TaskManager manager = Managers.getDefaultFile();
    private static HistoryManager history = Managers.getDefaultHistory();
    private static Epic epic; //переменную эпика вынесем в общие для удобства работы с ним в методах тестирования

    //перед каждым тестом солздаем эпик заново
    @BeforeEach
    public void beforeEach(){
        //создаем новый (пустой) эпик
        epic = new Epic("Имя эпика", "Описание эпика");
        manager.createEpic(epic);
    }

    @Test
    public void shouldEpicStatusIsNewIfListOfSubtasksIsEmpty() {
        //проверяем что статус только что созданного эпика рассчитался как NEW
        Assertions.assertEquals(TaskStatus.NEW,epic.getStatus());
    }

    @Test
    public void shouldEpicStatusIsNewIfAllSubtasksIsNew() {
        //создаем два сабтаска
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1",epic.getUid());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2",epic.getUid());
        manager.createSubtask(subtask2);

        //и проверяем статус эпика
        assertEquals(TaskStatus.NEW,epic.getStatus());
    }

    @Test
    public void shouldEpicStatusIsDoneIfAllSubtasksIsDone() {
        //создаем два сабтаска
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1",epic.getUid());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2",epic.getUid());
        manager.createSubtask(subtask2);

        //меняем статус сабтасков
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        //апдейтим менеджер передавая обновленные объекты сабтасков (иначе статус эпика не пересчитается)
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        //и проверяем статус эпика
        assertEquals(TaskStatus.DONE,epic.getStatus());
    }

    @Test
    public void shouldEpicStatusIsInProgressIfSubtasksIsInNewAndDone() {
        //создаем два сабтаска
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1",epic.getUid());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2",epic.getUid());
        manager.createSubtask(subtask2);

        //меняем статус одного сабтаска
        subtask1.setStatus(TaskStatus.DONE);

        //апдейтим менеджер передавая обновленный объект сабтаски (иначе статус эпика не пересчитается)
        manager.updateSubtask(subtask1);

        //и проверяем статус эпика
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus());
    }

    @Test
    public void shouldEpicStatusIsInProgressIfAllSubtasksIsInProgress() {
        //создаем два сабтаска
        Subtask subtask1 = new Subtask("Сабтаск 1", "Описание сабтаска 1",epic.getUid());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Сабтаск 2", "Описание сабтаска 2",epic.getUid());
        manager.createSubtask(subtask2);

        //меняем статус сабтасков
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);

        //апдейтим менеджер передавая обновленные объекты сабтасков (иначе статус эпика не пересчитается)
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        //и проверяем статус эпика
        assertEquals(TaskStatus.IN_PROGRESS,epic.getStatus());
    }

    @Test
    public void shouldGet20230203T0000AfterUpdateSubtask() {
        Subtask subtask = new Subtask("Сабтаск","Описание сабтаска", epic.getUid());
        manager.createSubtask(subtask);

        LocalDateTime startSubTime = LocalDateTime.of(2023,02,3,0,0);
        subtask.setStartTime(startSubTime);
        manager.updateSubtask(subtask);

        assertEquals("2023-02-03T00:00",epic.getStartTime().toString());
    }
}