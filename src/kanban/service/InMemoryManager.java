package kanban.service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;

import kanban.model.*;

public class InMemoryManager implements TaskManager {
    protected HashMap<Integer, Task> listOfTask = new HashMap<>(); //мапа для тасков
    protected HashMap<Integer, Epic> listOfEpic = new HashMap<>(); //мапа для эпиков
    protected HashMap<Integer, Subtask> listOfSubtask = new HashMap<Integer, Subtask>(); //мапа для сабтасков
    protected  int guid = 1; // общий глобальный идентификатор задач всех типов
    public void setGuid(int newGuid) {
        guid = newGuid;
    }

    //создадим компаратор для сортировки тасков
    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            if ((t1.getStartTime() == null) || (t2.getStartTime() == null)) {
                return -1;
            }
            if (t1.getStartTime().isAfter(t2.getStartTime())) {
                return 1;
            } else return -1;
        }
    };
    //сортированная коллекция для приоретизированного списка
    protected TreeSet<Task> listOfPrioritizeTasks = new TreeSet(comparator);

    @Override
    public int createTask(Task task) { //создание таски
        //проверяем возможность сохранения (пересечение времени задач)
        if (!validate(task)) {
            return false;
        }
        task.setUid(guid);
        listOfTask.put(task.getUid(),task);
        listOfPrioritizeTasks.add(task);
        guid++;
        return task.getUid();
    }

    @Override
    public int createEpic(Epic epic) { //создание эпика
        epic.setUid(guid);
        listOfEpic.put(epic.getUid(),epic);
        guid++;
        return epic.getUid();
    }

    @Override
    public int createSubtask(Subtask subtask) { //создание сабтаски
        //проверяем возможность сохранения (пересечение времени задач)
        if (!validate(subtask)) {
            return false;
        }

        subtask.setUid(guid);
        listOfSubtask.put(subtask.getUid(),subtask);
        guid++;

        //добавляю в эпик информацию по новому сабтаску
        listOfEpic.get(subtask.getEpicID()).addSubtask(subtask.getUid());

        //обновляем статус эпика, т.к. добавили новый сабтаск, то статус ставим NEW
        listOfEpic.get(subtask.getEpicID()).setStatus(TaskStatus.NEW);

        //добавляем в приоретизированный список
        listOfPrioritizeTasks.add(subtask);

        return subtask.getUid();
    }

    @Override
    public HashMap<Integer, Task> getAllTasc() { //получение списка всех тасков
        return listOfTask;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpic() { //получение списка всех эпиков
        return listOfEpic;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSubtasc() { //получение списка всех сабтасков
        return listOfSubtask;
    }

    @Override
    public void deleteTaskById(int id) {//удаление таски по ид
        listOfPrioritizeTasks.remove(listOfTask.get(id));
        listOfTask.remove(id);

    }

    @Override
    public void clearAllTasc() { //удаление всех тасков
        for (Task item: listOfTask.values()) {
            listOfPrioritizeTasks.remove(item);
        }
        listOfTask.clear();
    }

    @Override
    public void deleteSubtaskById(int id) { //удаление сабтаски по ид
        listOfPrioritizeTasks.remove(listOfSubtask.get(id)); //удаляем из списка приоретизированных задач
        //кроме удаления сабтаски необходимо удалить информацию о ней из массива связанных сабтасков в эпике
        //для этого в массиве сабтаски по ид находим эпикИд и передаем его в метод удаления сабтаски из эпика
        listOfEpic.get(listOfSubtask.get(id).getEpicID()).removeSubtask(id);

        //после того как удалили (то есть фактически обновили) эпик, надо пересчитать его расчетные параметры
        updateEpicAfterChange(listOfEpic.get(listOfSubtask.get(id).getEpicID()));

        //теперь удаляем сабтаску
        listOfSubtask.remove(id);
        Managers.getDefaultHistory().remove(id); //удаляем из истории просмотров
    }

    protected void updateEpicStatus(int id) { //проверка и обновление статуса эпика, на вход принимаем ид эпика
        //проверяем что под эпиком есть сабтаски
        if (listOfEpic.get(id).getSubtasksIds().isEmpty()) {//если список связанных сабтасков пуст, то статус эпика new
            listOfEpic.get(id).setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasNEW = false; //переменная для определения наличия сабтасков со статусом NEW
        boolean hasDONE = false; //переменная для определения наличия сабтасков со статусом DONE

        for (int i = 0; i < listOfEpic.get(id).getSubtasksIds().size(); i++) {
            //берем в массиве эпика ид сабтаски и проверяем её статус, проходимся по всему массиву сабтасков в эпике
            if (listOfSubtask.get(listOfEpic.get(id).getSubtasksIds().get(i)).getStatus().equals(TaskStatus.NEW)) {
                hasNEW = true;
            } else if
            (listOfSubtask.get(listOfEpic.get(id).getSubtasksIds().get(i)).getStatus().equals(TaskStatus.DONE)) {
                hasDONE = true;
            }
        }

        //если есть закрытые и открытые сабтаски то статус in_progress
        if (hasDONE && hasNEW) {
            listOfEpic.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }

        //если есть открытые и нет закрытых то статус NEW
        else if (hasNEW && !hasDONE) {
            listOfEpic.get(id).setStatus(TaskStatus.NEW);
        }

        //если есть закрытые и нет открытых то статус DONE
        else if (!hasNEW && hasDONE) {
            listOfEpic.get(id).setStatus(TaskStatus.DONE);
        } else //в остальных случаях статус IN_PROGRESS
            listOfEpic.get(id).setStatus(TaskStatus.IN_PROGRESS);
    }

    @Override
    public void clearAllSubtasc() { //удаление всех сабтасков
        //поскольку надо чистить не только сам список сабтасков, но и удалить информацию о них в эпиках,
        //вызываем в цикле удаление сабтасков по ид. При этом под конец список сабтасков будет чист,
        //отдельно его очищать не надо
        for (Integer id : listOfTask.keySet()) {
            listOfPrioritizeTasks.remove(listOfSubtask.get(id));
            deleteSubtaskById(id);
        }
    }

    @Override
    public void deleteEpicById(int id) { //удаление эпика по ид
        //удаляем связанные с эпиком сабтаски. Для этого пробегаемся по всему массиву ид сабтасков внутри переданного
        // эпика, полученный ид сабтаски удаляем из массива сабтасков
        for (int i = 0; i < listOfEpic.get(id).getSubtasksIds().size(); i++) {
            //удаляем сабтаск из приоретизированного списка
            listOfPrioritizeTasks.remove(listOfSubtask.get(listOfEpic.get(id).getSubtasksIds().get(i)));

            listOfSubtask.remove(listOfEpic.get(id).getSubtasksIds().get(i));
            Managers.getDefaultHistory().remove(listOfEpic.get(id).getSubtasksIds().get(i));//удаляем сабтаск из истории
        }

        //удаляем эпик из списка эпиков
        listOfEpic.remove(id);
        Managers.getDefaultHistory().remove(id); //удаляем эпик из истории
    }

    @Override
    public void clearAllEpic() { //удаление всех эпиков
        //при удалении всех эпиков надо также удалить все связанные с ними сабтаски. Поскольку мы считаем
        // что не может существовать сабтасков не связанных с эпиками, то мы просто чистим два массива -
        // массив эпиков и массив сабтасков
        listOfEpic.clear();
        listOfSubtask.clear();
    }

    //получение списка всех сабтасков определенного эпика
    @Override
    public HashMap<Integer, Subtask> getAllSubtasksByEpicId(int id) {
        HashMap<Integer, Subtask> list = new HashMap<>();

        //проверяем что сабтаски в эпике есть, если массив ид сабтасков в эпике пустой то возвращаем пустой лист
        if (!listOfEpic.containsKey(id)) {
            return null;
        } else if (listOfEpic.get(id).getSubtasksIds().isEmpty()) return list;

        //пробегаемся по всему массиву ИД сабтасков в указанном эпике
        for (int i = 0; i < listOfEpic.get(id).getSubtasksIds().size(); i++) {
            list.put(listOfEpic.get(id).getSubtasksIds().get(i),
                    listOfSubtask.get(listOfEpic.get(id).getSubtasksIds().get(i)));
        }
        return list;
    }

    @Override
    public Task getTaskById(int id) { //получение таски по ид
        if (!listOfTask.containsKey(id)) return null;
        Managers.getDefaultHistory().addTask(listOfTask.get(id)); //записываем обращение к таске в историю
        return listOfTask.get(id);
    }

    @Override
    public Epic getEpicById(int id) { //получение эпика по ид
        if (!listOfEpic.containsKey(id)) return null;
        Managers.getDefaultHistory().addTask(listOfEpic.get(id)); //записываем обращение к эпику в историю
        return listOfEpic.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) { //получение сабтаски по ид
        if (!listOfSubtask.containsKey(id)) return null;
        Managers.getDefaultHistory().addTask(listOfSubtask.get(id)); //записываем обращение к сабтаске в историю
        return listOfSubtask.get(id);
    }

    @Override
    public Boolean updateTask(Task task) { //обновление таски
        //проверяем возможность сохранения (пересечение времени задач)
        if (!validate(task)) {
            return false;
        }

        //заменяю значения в таске из существующего листа новыми. По условию ид верный, то есть его менять не надо
        listOfTask.get(task.getUid()).setName(task.getName());
        listOfTask.get(task.getUid()).setDescription(task.getDescription());
        listOfTask.get(task.getUid()).setStatus(task.getStatus());
        listOfTask.get(task.getUid()).setDuration(task.getDuration());
        listOfTask.get(task.getUid()).setStartTime(task.getStartTime());

        //удаляем и заново добавляем в приоретизированный список
        listOfPrioritizeTasks.remove(task);
        listOfPrioritizeTasks.add(task);

        return true;
    }

    @Override
    public Boolean updateEpic(Epic epic) { //обновление эпика
        //заменяю значения в эпике из существующего листа новыми. По условию ид верный, то есть его менять не надо
        listOfEpic.get(epic.getUid()).setName(epic.getName());
        listOfEpic.get(epic.getUid()).setDescription(epic.getDescription());
        updateEpicAfterChange(epic);

        return true;
    }

    @Override
    public Boolean updateSubtask(Subtask sub) { //обновление таски
        //проверяем возможность сохранения (пересечение времени задач)
        if (!validate(sub)) {
            return false;
        }
        //заменяю значения в таске из существующего листа новыми. По условию ид верный, то есть его менять не надо
        listOfSubtask.get(sub.getUid()).setName(sub.getName());
        listOfSubtask.get(sub.getUid()).setDescription(sub.getDescription());
        listOfSubtask.get(sub.getUid()).setStatus(sub.getStatus());
        listOfSubtask.get(sub.getUid()).setDuration(sub.getDuration());
        listOfSubtask.get(sub.getUid()).setStartTime(sub.getStartTime());

        //пересчитываю статус эпика
        updateEpicAfterChange(listOfEpic.get(sub.getEpicID()));

        //удаляем и заново добавляем в приоретизированный список
        listOfPrioritizeTasks.remove(sub);
        listOfPrioritizeTasks.add(sub);

        return true;
    }

    //расчет времени выполнения эпика
    private void calcEpicDuration(Epic epic) {
        int newDuration = 0;
        epic.setDuration(newDuration); //обнуляем текущее значение продолжительности

        if (!epic.getSubtasksIds().isEmpty()) { //под эпиком должны быть сабтаски
            for (int i: epic.getSubtasksIds()) {
                newDuration += listOfSubtask.get(i).getDuration();
            }
        }
        epic.setDuration(newDuration);
    }

    //расчет даты начала работы над эпиком
    public void calcEpicStartDate(Epic epic) {
        LocalDateTime newStartTime = null;
        epic.setStartTime(newStartTime); //обнуляем текущее значение даты начала работы над эпиком

        if (!epic.getSubtasksIds().isEmpty()) { //под эпиком должны быть сабтаски
            for (int i: epic.getSubtasksIds()) {
                //если дата сабтаска не null, а дата эпика null ставим дату сабтаска в дату эпика
                if ((listOfSubtask.get(i).getStartTime() != null) && (epic.getStartTime() == null)) {
                    epic.setStartTime(listOfSubtask.get(i).getStartTime());
                } //иначе если дата сабтаска не null, и дата эпика не null ставим меньшую из дат
                else if ((listOfSubtask.get(i).getStartTime() != null) && (epic.getStartTime() != null)) {
                    if (listOfSubtask.get(i).getStartTime().isBefore(epic.getStartTime())) {
                        epic.setStartTime(listOfSubtask.get(i).getStartTime());
                    }
                }
            }
        }
    }

    //создадим метод обновляющий эпик после каких либо изменений
    public void updateEpicAfterChange(Epic epic) {
        calcEpicDuration(epic);
        calcEpicStartDate(epic);
        updateEpicStatus(epic.getUid());
    }

    @Override
    public TreeSet<Task> getPrioritizedSet() {
        return listOfPrioritizeTasks;
    }

    //валидация задач по пересечению
    protected boolean validate(Task task) {
        //если в задаче не заполнена дата старта, то сразу вернем true
        if (task.getStartTime() == null) return true;

        for (Task item: getPrioritizedSet()) {
            /*если ввыполнится условие, что таск заканчивается ранее старта элемента
            ИЛИ таск начинается после окончания элемента, то в условии IF получится false
            и мы продолжим цикл проверки, а если проверка даст false, то в условии оно обернётся
            в true и метод вернет false. ЕСЛИ ЖЕ во всем цикле полное условие не сработает ни разу,
            то есть все проверки на даты старт и окончания пройдут, то мы по итогу метода вернем true*/
            if (item.getStartTime() != null) { //проверку делаем только если item содержит время старта
                if (!(task.getEndTime().isBefore(item.getStartTime())
                    || task.getStartTime().isAfter(item.getEndTime()))) {
                return false;
                }
            }
        }
        return true;
    }
}

