import kanban.model.*;
import kanban.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class HistoryManagerTest {

    InMemoryHistoryManager history;

    //вспомогательные методы по созданию тасков, эпиков и сабтасков
    protected Epic createTestEpic(int i) {
        Epic epic = new Epic("Эпик"+i, "Описание эпика"+i);
        epic.setUid(i);
        return epic;
    }

    protected Subtask createTestSubtask(int i, Epic epic) {
        Subtask subtask = new Subtask("Сабтаск"+i, "Описание сабтаска"+i, epic.getUid());
        subtask.setUid(i);
        return subtask;
    }

    protected Task createTestTask(int i) {
        Task task = new Task("Таск"+i, "Описание таска"+i);
        task.setUid(i);
        return task;
    }

    @BeforeEach
    public void beforeEach() {
        history = new InMemoryHistoryManager();
    }

    @Test
    public void shouldGetTask1WhenAddTaskInEmptyHistory() {
        history.addTask(createTestTask(1));
        assertEquals("Таск1",history.getHistory().get(0).getName());
    }

    @Test
    public void shouldGetTask2WhenAddTaskInNonEmptyHistory() {
        history.addTask(createTestTask(1));
        history.addTask(createTestEpic(2));
        assertEquals("Эпик2",history.getHistory().get(1).getName());

    }

    @Test
    public void shouldGetSubTask2WhenAddDuplicateTaskInHistory() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(2, epic);
        history.addTask(epic);
        history.addTask(subtask);
        history.addTask(createTestTask(3));
        history.addTask(subtask);
        assertEquals("Сабтаск2",history.getHistory().get(2).getName());

    }

    @Test
    public void shouldTrueIfArrayIsEmptyFromVoidHistory() {
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    public void shouldGet1AsSizeOfHistoryWithOneItem() {
        history.addTask(createTestTask(1));
        assertEquals(1,history.getHistory().size());

    }

    @Test
    public void shouldGet3AsSizeOfHistoryWithCoupleItems() {
        history.addTask(createTestTask(1));
        Epic epic = createTestEpic(2);
        history.addTask(epic);
        history.addTask(createTestSubtask(3, epic));
        assertEquals(3,history.getHistory().size());
    }


    @Test
    public void shouldNoExceptionsWhenRemoveTaskFromNullHistory() {
        history.remove(1);
        assertEquals(0,history.getHistory().size());
    }

    @Test
    public void shouldGet0AsSizeOfHistoryAfterRemoveAloneTask() {
        history.addTask(createTestTask(1));
        history.remove(1);
        assertEquals(0,history.getHistory().size());
    }

    @Test
    public void shouldGet2AsSizeOfHistoryAfterRemoveMedianTask() {
        history.addTask(createTestTask(1));
        Epic epic = createTestEpic(2);
        history.addTask(epic);
        history.addTask(createTestSubtask(3, epic));
        assertEquals(3,history.getHistory().size()); //проверка что размер истории =3

        history.remove(2);
        assertEquals(2,history.getHistory().size());
    }

    @Test
    public void shouldGet1AsSizeOfHistoryAfterRemoveFirstTask() {
        history.addTask(createTestTask(1));
        history.addTask(createTestEpic(2));
        assertEquals(2,history.getHistory().size()); //проверка что размер истории =2

        history.remove(1);
        assertEquals(1,history.getHistory().size());

    }

    @Test
    public void shouldGet1AsSizeOfHistoryAfterRemoveLastTask() {
        history.addTask(createTestTask(1));
        history.addTask(createTestEpic(2));
        assertEquals(2,history.getHistory().size()); //проверка что размер истории =2

        history.remove(2);
        assertEquals(1,history.getHistory().size());
    }

    @Test
    public void shouldNoExceptionWhenClearAllFromVoidHistory() {
        assertEquals(0,history.getHistory().size()); //проверка что размер истории =0

        history.clearAllHistory();
        assertEquals(0,history.getHistory().size());
    }

    @Test
    public void shouldClearAllFromHistoryWith1Item() {
        history.addTask(createTestTask(1));
        assertEquals(1,history.getHistory().size()); //проверка что размер истории =1

        history.clearAllHistory();
        assertEquals(0,history.getHistory().size());
    }

    @Test
    public void shouldClearAllFromHistoryWithCoupleItems() {
        history.addTask(createTestTask(1));
        history.addTask(createTestEpic(2));
        assertEquals(2,history.getHistory().size()); //проверка что размер истории =2

        history.clearAllHistory();
        assertEquals(0,history.getHistory().size());
    }
}
