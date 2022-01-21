package com.example.motifissa.HelperClasses;

import java.util.ArrayList;

public class ListenerVariable<T> {
    private T variable;
    private ArrayList<ChangeListener<T>> changeListeners;
    private ArrayList<SuccessListener> successListeners;

    public ListenerVariable(T startValue) {
        variable = startValue;
        changeListeners = new ArrayList<>();
        successListeners = new ArrayList<>();
    }

    public ListenerVariable() {
        variable = null;
        changeListeners = new ArrayList<>();
        successListeners = new ArrayList<>();
    }

    public T get() {
        return variable;
    }

    public void set(T variable) {
        this.variable = variable;


        if (changeListeners != null){
            for (ChangeListener<T> listener : this.changeListeners)
                listener.onChange(this.variable);
        }
        if (successListeners != null && this.variable instanceof Boolean && (Boolean) this.variable){
            for (SuccessListener listener : this.successListeners)
                listener.onSuccess();
            this.successListeners.clear();
        }
    }

    public void removeListener(ChangeListener<T> listener){
        this.changeListeners.remove(listener);
    }

    public void removeListeners(){
        this.changeListeners.clear();
    }

    public void addListener(ChangeListener<T> listener) {
        this.changeListeners.add(listener);

        if (variable instanceof Boolean)
            listener.onChange(variable);
    }

    public void removeSuccessListener(SuccessListener listener){
        this.successListeners.remove(listener);
    }

    public void addSuccessListener(SuccessListener listener) {
        this.successListeners.add(listener);
    }

    public ArrayList<ChangeListener<T>> getChangeListeners() {
        return changeListeners;
    }

    public ArrayList<SuccessListener> getSuccessListeners() {
        return successListeners;
    }

    public interface ChangeListener<T> {
        void onChange(T value);
    }

    public interface SuccessListener{
        void onSuccess();
    }

}
