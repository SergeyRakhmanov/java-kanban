import kanban.model.*;
import static org.junit.jupiter.api.Assertions.*;
import kanban.service.InMemoryHistoryManager;
import kanban.service.TaskManager;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

public abstract class TaskManagerTest <T extends TaskManager> {

    //создаем переменную менеджера на которой будем тестировать функционал
    T manager;
    InMemoryHistoryManager history;

    //вспомогательные методы по созданию тасков, эпиков и сабтасков
    protected Epic createTestEpic(int i) {
        Epic epic = new Epic("Эпик"+i, "Описание эпика"+i);
        manager.createEpic(epic);
        return epic;
    }

    protected Subtask createTestSubtask(int i, Epic epic) {
        Subtask subtask = new Subtask("Сабтаск"+i, "Описание сабтаска"+i, epic.getUid());
        manager.createSubtask(subtask);
        return subtask;
    }

    protected Task createTestTask(int i) {
        Task task = new Task("Таск"+i, "Описание таска"+i);
        manager.createTask(task);
        return task;
    }


    //методы для тестирвоания
    @Test
    public void shouldCreateNewTask() {
        Task task = createTestTask(1);

        //проверяем что таска записалась в менеджер
        assertEquals(1,manager.getAllTasc().size());
    }

    @Test
    public void shouldCreateNewEpic() {
        Epic epic = createTestEpic(1);

        //проверяем что эпик записался в менеджер
        assertEquals(1,manager.getAllEpic().size());
    }

    @Test
    public void shouldCreateNewSubtask() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(1,epic);

        //проверяем что сабтаск записался в менеджер
        assertEquals(1,manager.getAllSubtasc().size());
    }

    @Test
    public void shouldGet2AsSizeOfMapOfAllEpics() {
        Epic epic1 = createTestEpic(1);
        Epic epic2 = createTestEpic(2);

        //проверяем по количеству записей (длине массива)
        assertEquals(2,manager.getAllEpic().size());
    }

    @Test
    public void shouldGet0AsSizeOfMapOfNoEpics() {
        //проверяем по количеству записей (длине массива)
        assertEquals(0,manager.getAllEpic().size());
    }

    @Test
    public void shouldGet2AsSizeOfMapOfAllSubtasks() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(1,epic);
        Subtask subtask2 = createTestSubtask(1,epic);

        //проверяем по количеству записей (длине массива)
        assertEquals(2,manager.getAllSubtasc().size());
    }

    @Test
    public void shouldGet0AsSizeOfMapOfNoSubtasks() {
        //проверяем по количеству записей (длине массива)
        assertEquals(0,manager.getAllSubtasc().size());
    }

    @Test
    public void shouldGet2AsSizeOfMapOfAllTasks() {
        Task task1 = createTestTask(1);
        Task task2 = createTestTask(2);

        //проверяем по количеству записей (длине массива)
        assertEquals(2,manager.getAllTasc().size());
    }

    @Test
    public void shouldGet0AsSizeOfMapOfNoTasks() {
        //проверяем по количеству записей (длине массива)
        assertEquals(0,manager.getAllTasc().size());
    }

    @Test
    public void shouldGet1OfSizeOfTasksAfterDelete() {
        Task task1 = createTestTask(1);
        Task task2 = createTestTask(2);

        //сначала убеждаемся что тасков два
        assertEquals(2,manager.getAllTasc().size());

        //удаляем один таск
        manager.deleteTaskById(task1.getUid());
        //проверяем что теперь размер массива тасков = 1
        assertEquals(1,manager.getAllTasc().size());
    }

    @Test
    public void shouldGet0OfSizeOfTasksAfterClearAll() {
        Task task1 = createTestTask(1);
        Task task2 = createTestTask(2);

        //сначала убеждаемся что тасков два
        assertEquals(2,manager.getAllTasc().size());

        //удаляем всё
        manager.clearAllTasc();
        //проверяем что теперь размер массива тасков = 0
        assertEquals(0,manager.getAllTasc().size());
    }

    @Test
    public void shouldGet1OfSizeOfEpicsAfterDelete() {
        Epic epic1 = createTestEpic(1);
        Epic epic2 = createTestEpic(2);

        //сначала убеждаемся что эпиков два
        assertEquals(2,manager.getAllEpic().size());

        //удаляем один эпик
        manager.deleteEpicById(epic1.getUid());
        //проверяем что теперь размер массива эпиков = 1
        assertEquals(1,manager.getAllEpic().size());
    }

    @Test
    public void shouldGet0OfSizeOfEpicsAfterClearAll() {
        Epic epic1 = createTestEpic(1);
        Epic epic2 = createTestEpic(2);

        //сначала убеждаемся что эпиков два
        assertEquals(2,manager.getAllEpic().size());

        //удаляем всё
        manager.clearAllEpic();
        //проверяем что теперь размер массива эпиков = 0
        assertEquals(0,manager.getAllEpic().size());
    }

    @Test
    public void shouldGet2OfSizeOfSubtasksFromEpicID() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(1,epic);
        Subtask subtask2 = createTestSubtask(2, epic);

        //проверка метода возвращающего массив сабтасков по эпикИД
        assertEquals(2,manager.getAllSubtasksByEpicId(epic.getUid()).size());
    }

    @Test
    public void shouldGet0OfSizeOfSubtasksFromEpicID() {
        Epic epic = createTestEpic(1);

        //проверка метода возвращающего массив сабтасков по эпикИД
        assertEquals(0,manager.getAllSubtasksByEpicId(epic.getUid()).size());
    }

    @Test
    public void shouldGetNullOfSubtasksFromEpicByWrongID() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(1,epic);
        Subtask subtask2 = createTestSubtask(2, epic);

        //проверка метода возвращающего массив сабтасков по эпикИД
        assertEquals(null,manager.getAllSubtasksByEpicId(epic.getUid() + 1));
    }

    @Test
    public void shouldGetNameOfTaskByTaskId() {
        Task task = createTestTask(1);
        assertEquals("Таск1",manager.getTaskById(task.getUid()).getName());
    }

    @Test
    public void shouldGetNullWithWrongTaskId() {
        Task task = createTestTask(1);
        assertEquals(null,manager.getTaskById(task.getUid() + 1));
    }

    @Test
    public void shouldGetNameOfEpicByEpicId() {
        Epic epic = createTestEpic(1);
        assertEquals("Эпик1",manager.getEpicById(epic.getUid()).getName());
    }

    @Test
    public void shouldGetNullWithWrongEpicId() {
        Epic epic = createTestEpic(1);
        assertEquals(null,manager.getEpicById(epic.getUid() + 1));
    }

    @Test
    public void shouldGetNameOfSubtaskBySubtaskId() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(1,epic);
        assertEquals("Сабтаск1",manager.getSubtaskById(subtask.getUid()).getName());
    }


    @Test
    public void shouldGetNullWithWrongSubtaskId() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(1,epic);
        assertEquals(null,manager.getSubtaskById(subtask.getUid() + 1));
    }

    @Test
    public void shouldGetNewNameAfterTaskUpdate() {
        Task task = createTestTask(1);
        task.setName("NewName");
        manager.updateTask(task);
        assertEquals("NewName",manager.getTaskById(task.getUid()).getName());
    }

    @Test
    public void shouldGetNewNameAfterEpicUpdate() {
        Epic epic = createTestEpic(1);
        epic.setName("NewName");
        manager.updateEpic(epic);
        assertEquals("NewName",manager.getEpicById(epic.getUid()).getName());
    }

    @Test
    public void shouldGetNewNameAfterSubtaskUpdate() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(2,epic);
        subtask.setName("NewName");
        manager.updateSubtask(subtask);
        assertEquals("NewName",manager.getSubtaskById(subtask.getUid()).getName());
    }

    @Test
    public void shouldFalseWhenTasksWorkPeriodCrossed() {
        Task task1 = createTestTask(1);
        Task task2 = createTestTask(2);

        manager.updateTask(new Task(task1.getName(),
                task1.getDescription(),
                task1.getUid(),
                30,
                LocalDateTime.of(2023,2,3,17,0)));

        assertFalse(manager.updateTask(new Task(task2.getName(),
                task2.getDescription(),
                task2.getUid(),
                80,
                LocalDateTime.of(2023,2,3,16,0)))
        );
    }

    @Test
    public void shouldTrueWhenTasksWorkPeriodNotCrossed() {
        Task task1 = createTestTask(1);
        Task task2 = createTestTask(2);

        manager.updateTask(new Task(task1.getName(),
                task1.getDescription(),
                task1.getUid(),
                30,
                LocalDateTime.of(2023,2,3,17,0)));

        assertFalse(manager.updateTask(new Task(task2.getName(),
                task2.getDescription(),
                task2.getUid(),
                40,
                LocalDateTime.of(2023,2,3,16,20)))
        );
    }

    @Test
    public void shouldGetSubtask2FromPrioritizedList() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(2,epic);
        Task task = createTestTask(3);
        Subtask subtask2 = createTestSubtask(4,epic);

        task.setStartTime(LocalDateTime.of(2023,2,3,17,0));
        task.setDuration(30);
        manager.updateTask(task);

        subtask1.setStartTime(LocalDateTime.of(2023,1,3,10,0));
        subtask1.setDuration(80);
        manager.updateSubtask(subtask1);

        subtask2.setStartTime(LocalDateTime.of(2023,2,3,10,0));
        subtask2.setDuration(80);
        manager.updateSubtask(subtask2);

        assertEquals("Сабтаск4",manager.getPrioritizedSet().first().getName());
    }

    @Test
    public void shouldGet100MinutesOfEpicDuration() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(2,epic);
        Subtask subtask2 = createTestSubtask(3,epic);

        subtask1.setDuration(50);
        manager.updateSubtask(subtask1);
        subtask2.setDuration(50);
        manager.updateSubtask(subtask2);

        assertEquals(100,manager.getEpicById(epic.getUid()).getDuration());
    }

    @Test
    public void shouldGet20230101T1000OfEpicStartDate() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(2,epic);
        Subtask subtask2 = createTestSubtask(3,epic);

        subtask1.setStartTime(LocalDateTime.of(2023,02,02,10,0));
        manager.updateSubtask(subtask1);
        subtask2.setStartTime(LocalDateTime.of(2023,01,01,10,0));
        manager.updateSubtask(subtask2);

        assertEquals("2023-01-01T10:00",manager.getEpicById(epic.getUid()).getStartTime().toString());
    }
}
