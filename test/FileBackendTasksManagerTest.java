import kanban.model.*;
import kanban.service.FileBackendTasksManager;
import kanban.service.Managers;
import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackendTasksManagerTest extends TaskManagerTest<FileBackendTasksManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackendTasksManager();
        history = Managers.getDefaultHistory();
        history.clearAllHistory();
    }

    @Test
    public void shouldSaveFileWithoutDataAndHistoryForSave() {
        manager.save(); //сохраняем в файл (должен сохраниться только заголовок)
        File file = new File("tasks.csv");
        assertTrue(file.exists());
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

    @Test
    public void shouldNoExceptionWhenLoadVoidFile() {
        //"обнуляем" файл от предыдущих тестов
        File testFile = new File("tasks.csv");
        try {
            PrintWriter pw = new PrintWriter(testFile.getName());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager = FileBackendTasksManager.loadFromFile(testFile);

        boolean result  = (manager.getAllEpic().isEmpty())
                && (manager.getAllSubtasc().isEmpty())
                && (manager.getAllTasc().isEmpty()
        );

        assertTrue(result);
    }

    @Test
    public void shouldGetEpicWithoutSubtasksFormSavedFile() {
        createTestEpic(1);
        manager.save();

        File testFile = new File("tasks.csv");
        manager = FileBackendTasksManager.loadFromFile(testFile);

        assertEquals("Эпик1",manager.getEpicById(1).getName());
    }

    @Test
    public void shouldGetTasksAndHistoryFormSavedFile() {
        Epic epic = createTestEpic(1);
        Subtask subtask1 = createTestSubtask(2,epic);
        Subtask subtask2 = createTestSubtask(3,epic);
        Task task = createTestTask(4);

        history.addTask(epic);
        history.addTask(subtask1);
        history.addTask(subtask2);
        history.addTask(task);

        manager.save();

        File testFile = new File("tasks.csv");
        manager = FileBackendTasksManager.loadFromFile(testFile);

        //проверка корректности загрузки истории
        //преобразуем загруженную историю в строку последовательных ИД задач
        StringBuilder sb = new StringBuilder(history.getHistory().size());

        String str = new String();
        for (Task item: history.getHistory()) {
            sb.append(item.getUid());
            sb.append(",");
        }
        str = sb.toString();

        //сравниваем ожидаемую строку с преобразованной из загруженной истории
        assertEquals("1,2,3,4,",str);

        //проверка корректности загрузки задач
        assertTrue(task.toString().equals(manager.getTaskById(4).toString()));
        assertTrue(epic.toString().equals(manager.getEpicById(1).toString()));
        assertTrue(subtask1.toString().equals(manager.getSubtaskById(2).toString()));
        assertTrue(subtask2.toString().equals(manager.getSubtaskById(3).toString()));
    }
}
