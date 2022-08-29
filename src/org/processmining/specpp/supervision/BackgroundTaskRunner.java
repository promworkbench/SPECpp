package org.processmining.specpp.supervision;

import org.processmining.specpp.traits.Joinable;
import org.processmining.specpp.traits.StartStoppable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class BackgroundTaskRunner implements StartStoppable, Joinable {

    private final List<Runnable> backgroundTasks;
    private final List<Thread> backgroundThreads;

    protected BackgroundTaskRunner() {
        backgroundTasks = new LinkedList<>();
        backgroundThreads = new LinkedList<>();
    }

    public static BackgroundTaskRunner inst() {
        return new BackgroundTaskRunner();
    }

    public void register(Runnable backgroundTask) {
        backgroundTasks.add(backgroundTask);
    }

    @Override
    public void start() {
        runBackgroundTasks();
    }

    protected void runBackgroundTasks() {
        for (Runnable backgroundTask : backgroundTasks) {
            Thread thread = Executors.defaultThreadFactory().newThread(backgroundTask);
            backgroundThreads.add(thread);
            thread.start();
        }
    }

    @Override
    public void stop() {
        backgroundThreads.forEach(Thread::interrupt);
    }

    @Override
    public void join() throws InterruptedException {
        for (Thread subThread : backgroundThreads) {
            subThread.join();
        }
    }

}
