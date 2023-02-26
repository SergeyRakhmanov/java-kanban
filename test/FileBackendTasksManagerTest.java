import kanban.model.Epic;
import kanban.model.Subtask;
import kanban.model.Task;
import kanban.service.FileBackendTasksManager;
import kanban.service.InMemoryHistoryManager;
import kanban.service.Managers;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackendTasksManagerTest extends TaskManagerTest<FileBackendTasksManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackendTasksManager();
        history = new InMemoryHistoryManager();
    }

    @Test
    public void shouldSaveFileWithoutDataAndHistoryForSave() {
        manager.save(); //сохраняем в файл (должен сохраниться только заголовок)
        File file = new File("tasks.csv");
        assertEquals(true,file.exists());
    }

    @Test
    public void shouldSaveFileWithDataAndNoHistoryForSave() {
        Epic epic = createTestEpic(1);
        Subtask subtask = createTestSubtask(1,epic);
        manager.save();

        String[] str = new String[5];
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("tasks.csv"))) {
            while (br.ready()) {
                str[i] = br.readLine();
                i++;
            }
        }catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //1 - заголовок, 2 - эпик, 3 - сабтаск, 4 - пустая строка
        assertEquals(4,i);
    }

    @Test
    public void shouldSaveFileWithDataAndHistoryForSave() {
        Epic epic = createTestEpic(1);
        manager.getEpicById(epic.getUid()); //запись в историю
        manager.save();

        String[] str = new String[8];
        int i = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("tasks.csv"))) {
            while (br.ready()) {
                str[i] = br.readLine();
                i++;
            }
        }catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(4,i); //4 из-за добавления строки на разделение заданий и истории
    }
}
