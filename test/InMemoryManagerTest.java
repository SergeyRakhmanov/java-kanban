import kanban.service.InMemoryHistoryManager;
import kanban.service.InMemoryManager;
import org.junit.jupiter.api.BeforeEach;

public class InMemoryManagerTest extends TaskManagerTest<InMemoryManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryManager();
        history = new InMemoryHistoryManager();
    }
}
