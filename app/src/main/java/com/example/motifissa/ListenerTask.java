package com.example.motifissa;

public class ListenerTask<T> {
    private SuccessListener<T> successListener;
    private ServiceFunction<T> serviceFunction;
    private boolean completed;
    private T result;
    private final ListenerVariable<Boolean> mBounded;

    public ListenerTask(ListenerVariable<Boolean> mBounded, ServiceFunction<T> serviceFunction) {
        this.mBounded = mBounded;
        if(mBounded.get()){
            completed = true;
            result = serviceFunction.function();
        } else{
            completed = false;
            mBounded.addListener(changeListener);

        }
    }

    ListenerVariable.ChangeListener<Boolean> changeListener = new ListenerVariable.ChangeListener<Boolean>() {
        @Override
        public void onChange(Boolean value) {
            if(value) {
                result = serviceFunction.function();
                completed = true;

                // call the success listener
                if (successListener != null){
                    successListener.onSuccess(result);
                    successListener = null;
                }

                //remove it self
                mBounded.removeListener(changeListener);
            }
        }
    };




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
