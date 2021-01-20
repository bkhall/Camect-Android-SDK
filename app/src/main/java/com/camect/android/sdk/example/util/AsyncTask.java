package com.camect.android.sdk.example.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AsyncTask<INPUT, PROGRESS, RESULT> implements Runnable {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public static ThreadPoolExecutor newCachedThreadPool() {
        return new ThreadPoolExecutor(32, Short.MAX_VALUE,
                30L, TimeUnit.SECONDS,
                new SynchronousQueue<>());
    }

    public static ThreadPoolExecutor newSingleThreadExecutor() {
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    private final AtomicBoolean mIsCancelled = new AtomicBoolean();
    private final AtomicBoolean mIsDone      = new AtomicBoolean();
    private final AtomicBoolean mIsRunning   = new AtomicBoolean();

    private ThreadPoolExecutor mExecutor;
    private Future<?>          mFuture;
    private INPUT[]            mInputs;
    private RESULT             mResult;
    private Thread             mThread;

    public AsyncTask() {
    }

    public AsyncTask(@NonNull ThreadPoolExecutor executor) {
        mExecutor = executor;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mExecutor == null) {
            throw new RuntimeException("Executor not specified. You must specify the executor in " +
                    "the constructor or specify it in the execute method call.");
        }

        if (isCancelled()) {
            return true;
        }

        boolean cancelled = mExecutor.isTerminating() || mExecutor.isTerminated() ||
                mExecutor.isShutdown();

        if (mFuture != null) {
            cancelled |= mFuture.cancel(mayInterruptIfRunning);
        } else if (mThread != null && mayInterruptIfRunning) {
            mThread.interrupt();
            cancelled = true;
        }

        cancelled |= mExecutor.remove(this);

        mExecutor.purge();

        mIsCancelled.set(true);
        mIsDone.set(true);
        mIsRunning.set(false);

        sHandler.post(() -> {
            onCancelled();
            onCancelled(mResult);
        });

        return cancelled;
    }

    @WorkerThread
    protected abstract RESULT doInBackground(INPUT... inputs);

    public void execute() {
        execute(false);
    }

    public void execute(ThreadPoolExecutor executor) {
        mExecutor = executor;

        execute(false);
    }

    private void execute(boolean important) {
        if (mExecutor == null) {
            throw new RuntimeException("Executor not specified. You must specify the executor in " +
                    "the constructor or specify it in the execute method call.");
        }

        if (mExecutor.isTerminating() || isCancelled() || isDone() || isRunning()) {
            return;
        }

        // reclaim memory by removing cancelled tasks
        mExecutor.purge();

        if (important) {
            mExecutor.execute(this);
        } else {
            mFuture = mExecutor.submit(this);
        }
    }

    public void execute(INPUT... inputs) {
        mInputs = inputs;

        execute(false);
    }

    public void execute(ThreadPoolExecutor executor, INPUT... inputs) {
        mExecutor = executor;

        mInputs = inputs;

        execute(false);
    }

    public void executeNow() {
        execute(true);
    }

    public void executeNow(ThreadPoolExecutor executor) {
        mExecutor = executor;

        execute(true);
    }

    public void executeNow(ThreadPoolExecutor executor, INPUT... inputs) {
        mExecutor = executor;

        mInputs = inputs;

        execute(true);
    }

    public void executeNow(INPUT... inputs) {
        mInputs = inputs;

        execute(true);
    }

    public final boolean isCancelled() {
        return mIsCancelled.get();
    }

    public final boolean isDone() {
        return mIsDone.get();
    }

    public boolean isRunning() {
        return mIsRunning.get();
    }

    @MainThread
    protected void onCancelled() {
    }

    @MainThread
    protected void onCancelled(RESULT result) {
    }

    @MainThread
    protected void onPostExecute(RESULT result) {
    }

    @MainThread
    protected void onPreExecute() {
    }

    @MainThread
    protected void onProgressUpdate(PROGRESS... progress) {
    }

    @SafeVarargs
    @WorkerThread
    protected final void publishProgress(final PROGRESS... progress) {
        if (mExecutor.isTerminating() || isCancelled()) {
            return;
        }

        sHandler.post(() -> onProgressUpdate(progress));
    }

    /*
        Reset this instance's run flags so it can be reused.
        This can only be called when this instance is not running.
     */
    public void reset() {
        if (isRunning()) {
            return;
        }

        mIsCancelled.set(false);
        mIsDone.set(false);
        mIsRunning.set(false);
    }

    @Override
    public void run() {
        if (mExecutor.isTerminating() || isCancelled() || isDone() || isRunning()) {
            return;
        }

        mThread = Thread.currentThread();

        mIsRunning.set(true);

        // post to main thread
        sHandler.post(this::onPreExecute);

        if (mExecutor.isTerminating() || isCancelled()) {
            return;
        }

        // perform long running background steps
        mResult = doInBackground(mInputs);

        if (mExecutor.isTerminating() || isCancelled()) {
            return;
        }

        // post to main thread
        sHandler.post(() -> onPostExecute(mResult));

        mIsDone.set(true);
        mIsRunning.set(false);
    }
}
