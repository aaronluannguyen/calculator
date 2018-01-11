package datastructures.concrete.dictionaries;

import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    private static final int SIZE = 5;
    private int size;
    private int space;

    // You're encouraged to add extra fields (and helper methods) though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(SIZE);
        this.size = 0;
        this.space = SIZE;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            throw new NoSuchKeyException();
        }
        
        for (int i = 0; i < this.size; i++) {
            if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return this.pairs[i].value;
            }
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            for (int i = 0; i < this.size; i++) {
                if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                    this.pairs[i].value = value;
                }
            }
        } else {
            if (size >= space) {
                Pair<K, V>[] temp = this.pairs;
                this.space = this.space * 2;
                this.pairs = makeArrayOfPairs(space);
                for (int i = 0; i< temp.length; i++) {
                    this.pairs[i] = temp[i];
                }
            }
            this.pairs[size] = new Pair<K, V>(key, value);
            this.size++;
        }
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            throw new NoSuchKeyException();
        }
        
        V temp = null;
        boolean found = false;
        for (int i = 0; i < this.size; i++) {
            if (!found) {
                if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                    temp = this.pairs[i].value;
                    this.pairs[i] = this.pairs[i + 1];
                    found = !found;
                }
            } else {
                this.pairs[i] = this.pairs[i + 1];
            }
        }
        this.size--;
        return temp;
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < this.size; i++) {
            if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
