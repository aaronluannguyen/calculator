package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node<T> addItem = new Node<T>(item);
        if (this.size != 0) {
            this.back.next = addItem;
            addItem.prev = this.back;
            this.back = this.back.next;
        } else {
            setFrontBack(addItem);
        }
        this.size++;
    }

    @Override
    public T remove() {
        if (this.size == 0) {
            throw new EmptyContainerException();
        }
        T result = back.data;
        if (this.size == 1) {
            setFrontBack(null);
        } else {
            this.back = this.back.prev;
            this.back.next = null;
        }
        this.size--;
        return result;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        if (index == 0) {
            return this.front.data;
        } else if (index == this.size - 1) {
            return this.back.data;
        }

        T result = null;
        if (index < this.size / 2) {
            Node<T> current = this.front.next;
            for (int i = 1; i <= index; i++) {
                if (i == index) {
                    result = current.data;
                }
                current = current.next;
            }
        } else {
            Node<T> current = this.back;
            for (int i = this.size - 1; i >= index; i--) {
                if (i == index) {
                    result = current.data;
                }
                current = current.prev;
            }
        }
        return result;
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        Node<T> newNode = new Node<T>(item);
        if (index == 0) {
            if (this.size == 1) {
                setFrontBack(newNode);
            } else {
                newNode.next = this.front.next;
                this.front.next.prev = newNode;
                this.front = newNode;
            }
        } else if (index == this.size - 1) {
            newNode.prev = this.back.prev;
            this.back.prev.next = newNode;
            this.back = newNode;
        } else if (index < this.size / 2) {
            Node<T> current = this.front.next;
            for (int i = 1; i <= index; i++) {
                if (i == index) {
                    setHelper(current, newNode);
                }
                current = current.next;
            }
        } else {
            Node<T> current = this.back.prev;
            for (int i = this.size - 2; i >= index; i--) {
                if (i == index) {
                    setHelper(current, newNode);
                }
                current = current.prev;
            }
        }
    }
    
    private void setHelper(Node<T> current, Node<T> newNode) {
        current.prev.next = newNode;
        newNode.prev = current.prev;
        newNode.next = current.next;
        current.next.prev = newNode;
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size() + 1) {
            throw new IndexOutOfBoundsException();
        }
        
        Node<T> newNode = new Node<T>(item);
        if (index == 0) {
            if (this.size == 0) {
                setFrontBack(newNode);
            } else {
                newNode.next = this.front;
                this.front.prev = newNode;
                this.front = newNode;
            }
        } else if (index == this.size) { 
            this.back.next = newNode;
            newNode.prev = this.back;
            this.back = newNode;
        } else if (index <= this.size / 2) {
            Node<T> temp = this.front.next;
            for (int i = 1; i <= index; i++) {
                if (i == index) {
                    insertHelper(temp, newNode);
                } else {
                    temp = temp.next;
                }
            }
        } else {
            for (int i = this.size() - 1; i >= index; i--) {
                Node<T> temp = this.back;
                if (i == index) {
                    insertHelper(temp, newNode);
                } else {
                    temp = temp.prev;
                }
            }
        }
        this.size++;
    }
    
    private void insertHelper(Node<T> current, Node<T> newNode) {
        newNode.next = current;
        newNode.prev = current.prev;
        current.prev.next = newNode;
        current.prev = newNode;
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        
        T temp = null;
        if (index == 0) {
            temp = this.front.data;
            if (this.size == 1) {
                setFrontBack(null);
            } else {
                this.front = this.front.next;
            }
        } else if (index == this.size - 1) {
            temp = this.back.data;
            this.back = this.back.prev;
            this.back.next = null;
        } else if (index <= this.size / 2) {
            Node<T> tempNode = front.next;
            for (int i = 1; i <= index; i++) {
                if (i == index) {
                    temp = tempNode.data;
                    deleteHelper(tempNode);
                } else {
                    tempNode = tempNode.next;
                }
            }
        } else {
            Node<T> tempNode = back.prev;
            for (int i = this.size - 2; i >= index; i--) {
                tempNode = back.prev;
                if (i == index) {
                    temp = tempNode.data;
                    deleteHelper(tempNode);
                } else {
                    tempNode = tempNode.prev;
                }
            }
        }
        this.size--;
        return temp;
    }
    
    private void deleteHelper(Node<T> current) {
        current.prev.next = current.next;
        current.next.prev = current.prev;
    }

    @Override
    public int indexOf(T item) {
        int currIndex = 0;
        Node<T> current = this.front;
        while (current != null) {
            if (current.data == item || current.data.equals(item)) {
                return currIndex;
            }
            currIndex++;
            current = current.next; 
        }
        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        Node<T> current = this.front;
        while (current != null) {
            if (current.data == other || current.data.equals(other)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T temp = current.data;
            current = current.next;
            return temp;
        }
    }
    
    private void setFrontBack(Node<T> node) {
        this.front = node;
        this.back = node;
    }
}
