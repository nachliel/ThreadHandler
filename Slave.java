package com.company;

import java.util.LinkedList;
import java.util.Queue;


class Slave extends Thread {
    private Object lock = new Object();
    private Object lockTask = new Object();
    private int taskNumber = 0;

    private volatile boolean holdMe = true;
    private Queue<Task> taskQueue = new LinkedList<>();

    public Slave() {
        setName("Slave");
    }
    @Override
    public void run() {
        synchronized (lock) {
            while (holdMe) {
                if (taskQueue.isEmpty())
                    try {lock.wait();} catch (InterruptedException e) { e.printStackTrace();}
                else {
                    synchronized (lockTask) {
                        System.out.println("Task: " + ++taskNumber + "/" + countTasks());
                        taskQueue.remove().execute();
                        lockTask.notify();
                    }
                }
            }
        }
    }

    public boolean setTask(Task newTask) {
        if (taskQueue.isEmpty()) {
            synchronized (lock) {
                taskQueue.add(newTask);
                lock.notify();
                return true;
            }
        }
        else {
            synchronized (lockTask) {
                try { lockTask.wait();} catch (InterruptedException e) { e.printStackTrace();}
                if (setTask(newTask))
                    return true;
            }
        }
        return false;
    }

    public boolean addTask(Task newTask) {

        synchronized (lock) {
            taskQueue.add(newTask);
            lock.notify();
            return true;
        }
    }

    public boolean terminate() {
        if (taskQueue.isEmpty()) {
            synchronized (lock) {
                holdMe = false;
                lock.notify();
            }
            return true;
        } else {
            synchronized (lockTask) {
                try {
                    lockTask.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return terminate();
            }
        }
    }
    public int countTasks() {
        return taskQueue.size();
    }
}