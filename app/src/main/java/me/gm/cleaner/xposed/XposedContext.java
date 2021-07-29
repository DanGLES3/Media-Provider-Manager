package me.gm.cleaner.xposed;

import android.content.Context;
import android.content.res.Resources;

import java.lang.ref.WeakReference;

public abstract class XposedContext {
    protected static WeakReference<Context> sContextRef;
    protected static ClassLoader sClassLoader;

    protected XposedContext() {
    }

    protected static Context getContext() {
        return sContextRef.get();
    }
}
