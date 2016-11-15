package info.futureme.abs.ui;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class UIWeakRefHandler<T> extends Handler{
    private final WeakReference<T> weakReference;
    public UIWeakRefHandler(T obj) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<T>(obj);
    }
    /**
     * Handle system messages here when ui is avaliable.
     */
    public void dispatchMessage(Message msg) {
        T service = weakReference.get();
        if (service != null) {
            super.dispatchMessage(msg);
        }
    }

    @Override
    public void handleMessage(Message msg)
    {
        if (weakReference.get() == null)
            return;
        handleMessage(weakReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
