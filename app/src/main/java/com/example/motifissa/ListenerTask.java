package com.example.motifissa;

import android.util.Log;
import android.widget.Toast;

public class ListenerTask<T> {
    private SuccessListener<T> successListener;
    private boolean completed;
    private T result;
//    private final ListenerVariable<Boolean> mBounded;

    public ListenerTask(ServiceListener serviceListener, ServiceFunction<T> serviceFunction) {
        if(serviceListener.mBounded.get()){
            completed = true;
            result = serviceFunction.function();
        } else{
            completed = false;
            serviceListener.mBounded.addSuccessListener(() -> {
                result = serviceFunction.function();
                completed = true;

                // call the success listener
                if (successListener != null){
                    successListener.onSuccess(result);
                    successListener = null;
                }
            });

            // reconnect to the service if it isn't already
            if(!serviceListener.mIsConnecting)
                serviceListener.connectToService();
        }
    }




    public void setSuccessListener(SuccessListener<T> successListener) {
        this.successListener = successListener;

        if(completed){ // when the result was already obtained cal the success listener immediately
            if (successListener != null){
                successListener.onSuccess(result);
                this.successListener = null;
            }
        }
    }

    public interface SuccessListener<T> {
        void onSuccess(T value);
    }

    public interface ServiceFunction<T>{
        T function();
    }
}
