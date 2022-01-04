package com.example.motifissa;

public class ListenerVariable<T> {
    private T variable;
    private ChangeListener<T> listener;

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
        if (listener != null) listener.onChange(this.variable);
    }

    public ChangeListener<T> getListener() {
        return listener;
    }

    public void setListener(ChangeListener<T> listener) {
        this.listener = listener;
    }


    public interface ChangeListener<T> {
        void onChange(T value);
    }

}
