package kanban.service;

public class Managers {

    static InMemoryManager manager = new InMemoryManager();
    static InMemoryHistoryManager history = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return manager;
    }

    public static InMemoryHistoryManager getDefaultHistory(){
        return history;
    }

    //sprint 6
    static FileBackendTasksManager fileManager = new FileBackendTasksManager();

    public static TaskManager getDefaultFile() {
        return fileManager;
    }
}
