package com.company;

public class Main {
    public static void main(String[] args) {
        Slave mySlave = new Slave();
        Task task = () -> {
            long sum = 0;
            for (int i = 0; i < 10000; i++)
                sum += i;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
            System.out.println(Thread.currentThread().getName() + "-Calculated: " + sum);
        };
        Task findPrime = () -> {
            boolean flag = true;
            int i = 125454530;
            while(flag) {
                flag = false;
                i++;
                for (int j = 2; j < i/2; j++)
                    if (i % j == 0) {
                        flag = true;
                        break;
                    }
            }
            System.out.println(Thread.currentThread().getName() + "-Calculated Prime: " + i);
        };
        mySlave.start();
        mySlave.setTask(task);

        mySlave.setTask(findPrime);
        mySlave.setTask(task);
        mySlave.setTask(task);
        mySlave.setTask(findPrime);
        mySlave.setTask(findPrime);
        mySlave.setTask(findPrime);
        mySlave.setTask(findPrime);
        //while (!mySlave.terminate())
        mySlave.terminate();
        try {mySlave.join();} catch (InterruptedException e) {e.printStackTrace();}
        System.out.println("Finish");
    }
}

