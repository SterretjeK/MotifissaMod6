package com.example.motifissa;

public class ListenerTask<T> {
    T value = null;
    SuccessListener<T> successListener;

    public ListenerTask() {

    }

    public void finish(T value){
        this.value = value;
        if(successListener != null)
            successListener.onSuccess(value);
    }

    public SuccessListener<T> getSuccessListener() {
        return successListener;
    }

    public void setSuccessListener(SuccessListener<T> successListener) {
        this.successListener = successListener;
    }

    public interface SuccessListener<T> {
        void onSuccess(T value);
    }
}
