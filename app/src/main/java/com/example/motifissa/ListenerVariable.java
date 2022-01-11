package com.example.motifissa;

import java.util.ArrayList;

public class ListenerVariable<T> {
    private T variable;
    private ArrayList<ChangeListener<T>> listeners = new ArrayList<>();

    public ListenerVariable(T startValue) {
        variable = startValue;
    }

    public ListenerVariable() {
        variable = null;
    }

    public T get() {
        return variable;
    }

    public void set(T variable) {
        this.variable = variable;
        if (listeners != null){
            for (ChangeListener<T> listener : listeners)
                listener.onChange(this.variable);
        }
    }

    public void removeListener(ChangeListener<T> listener){
        this.listeners.remove(listener);
    }

    public void addListener(ChangeListener<T> listener) {
        this.listeners.add(listener);
    }


    public interface ChangeListener<T> {
        void onChange(T value);
    }

}
