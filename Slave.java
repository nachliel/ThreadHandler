package com.company;

import java.util.Queue;

class Slave extends Thread {
    private Task task;
    private Object lock = new Object();
    private Object lockTask = new Object();
    private volatile boolean holdMe = true;


    public Slave() {
        setName("Slave");
    }
    @Override
    public void run() {
        synchronized (lock) {
            while (holdMe) {
                if (task == null)
                    try {lock.wait();} catch (InterruptedException e) { e.printStackTrace();}
                else {
                    synchronized (lockTask) {
                        task.execute();
                        task = null;
                        lockTask.notify();
                    }
                }
            }
        }
    }

    public boolean setTask(Task newTask) {
        if (task==null) {
            synchronized (lock) {
                task = newTask;
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

    public boolean terminate() {
        do {
            if (task == null) {
                synchronized (lock) {
                    holdMe = false;
                    lock.notify();
                }
                return true;
            } else
                try {sleep(500); } catch (InterruptedException e) {e.printStackTrace();}
        }
        while(true);
    }
}