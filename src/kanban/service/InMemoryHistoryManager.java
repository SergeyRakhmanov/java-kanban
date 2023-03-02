package kanban.service;

import kanban.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node> customLinkedList = new HashMap<>();
    private Task head;
    private Task tail;

    @Override
    public void addTask(Task task) {
        Node node = new Node(task); // создаем ноду

        if (customLinkedList.isEmpty()) { //если список истории пуст, то элемент первый, prev и next == null
            customLinkedList.put(task.getUid(),node);
            head = task; //указывем, что голова списка это переданный таск
            tail = task; //и хвост тоже
        }
            //если в истории операций уже есть такой элемент, то надо перезаписать в конец
            //перезапись в конец фактически означает переформирование ссылок
            //если переданный объект уже в хвосте, то ничего не делаем
        else if (customLinkedList.containsKey(task.getUid())) {
            if (!task.getUid().equals(tail.getUid())) { //проверка что переданная таска не лежит уже в хвосте истории
                remove(task.getUid()); //удаляем таску из истории
                linkLast(task); //вставляем таску в конец истории
            }
        } else { //а если такого элемента в списке ещё нет, то пишем в конец
            linkLast(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTask();
    }

    private ArrayList<Task> getTask() {
        //создаем лист с капасити равной размеру истории
        ArrayList<Task> history = new ArrayList<>(customLinkedList.size());

        if (customLinkedList.isEmpty()) return history;

        Integer index = head.getUid(); //индекс = ID таски из истории (ключу мапы) начинаем с головы
        for (int i = 0; i < customLinkedList.size(); i++) {
            history.add(customLinkedList.get(index).getTask()); //по индексу достаем из мапы ноду и из ноды берем таску
            index = customLinkedList.get(index).getNext(); //достаем из мапы ноду и из ноды берем ID след таски
        }
        return history;
    }

    private void linkLast(Task task) { //запись в конец истории
        //создаем новую ноду для записи в историю и сразу ставим ей значение предыдущей записи на текущий хвост
        Node node = new Node(task,tail.getUid());

        //текущей записи в хвосте ставим значение следующей записи на ID переданного таска
        customLinkedList.get(tail.getUid()).setNext(task.getUid());

        //пишем ноду в историю просмотров
        customLinkedList.put(task.getUid(),node);

        //обновлем хвост на переданную таску
        tail = task;
    }

    @Override
    public void remove(int id) { //удаляем из истории запись по ID
        if (customLinkedList.containsKey(id)) {
            Integer prevID = customLinkedList.get(id).getPrev(); //ID предыдущей записи
            Integer nextID = customLinkedList.get(id).getNext(); //ID следующей записи

            if ((prevID == null) && (nextID == null)) { //если удаляем единственную запись
                customLinkedList.remove(id);
                return;
            } else if (prevID == null) { //если удаляем первую запись
                customLinkedList.get(nextID).setPrev(null); //ставим следующей ноде null в ссылку на предыдущую ноду
                head = customLinkedList.get(nextID).getTask(); //обновляем голову истории
            }
            else if (nextID == null) { //если удаляем последнюю запись
                customLinkedList.get(prevID).setNext(null); //ставим предыдущей ноде null в ссылку на следующую ноду
                tail = customLinkedList.get(prevID).getTask(); //обновляем хвост истории
            }
            else { //иначе, связываем вместе предыдущую и следующую ноды
                customLinkedList.get(prevID).setNext(nextID);
                customLinkedList.get(nextID).setPrev(prevID);
            }
            //удаляем запись из истории
            customLinkedList.remove(id);
        }
    }

    public void clearAllHistory() {
        customLinkedList.clear();
    }

}
