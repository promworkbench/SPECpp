package org.processmining.specpp.orchestra;

import org.processmining.specpp.util.PrintingUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor extends ThreadPoolExecutor {

    private final String label;

    public MyThreadPoolExecutor(String label, int corePoolSize, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, corePoolSize, 60L, TimeUnit.SECONDS, workQueue, threadFactory);
        this.label = label;
    }

    @Override
    public String toString() {
        return label + "{" + PrintingUtils.stringifyThreadPoolExecutor(this) + "}";
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (Thread.interrupted()) System.out.println("interrupt was swallowed on " + Thread.currentThread().getName());
        /*
        String msg = label + " {\n";
        String thread = Thread.currentThread().getName();
        int h = r.hashCode();
        if (Thread.interrupted()) msg += "an interrupt was swallowed\n";
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> futureTask = (Future<?>) r;
                if (futureTask.isDone()) futureTask.get();
                msg += "success of " + h + " on " + thread;
            } catch (InterruptedException | CancellationException e) {
                e.fillInStackTrace();
                msg += "failure of " + h + " on " + thread + " with\n" + e;
            } catch (ExecutionException e) {
                msg += "failure of " + h + " on " + thread + " with\n" + e.getCause();
            }
        } else if (t != null) {
            t.fillInStackTrace();
            msg += "failure of " + h + " on " + thread + " with\n" + t;
        }
        System.out.println(msg + "\n" + "current status: " + PrintingUtils.stringifyThreadPoolExecutor(this) + "}");
         */
    }

    public void setNewFixedSize(int threadCount) {
        setMaximumPoolSize(threadCount);
        setCorePoolSize(threadCount);
    }
}
