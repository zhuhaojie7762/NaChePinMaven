package com.aichebaba.locks;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest extends TestCase {

    public static Map<Integer, Stock> stockMap = new HashMap<>();

    public static void main(String[] args) {
        final Stock s1 = new Stock(1, 2);
        stockMap.put(s1.getId(), s1);
        final Stock s2 = new Stock(2, 3);
        stockMap.put(s2.getId(), s2);

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                int num = new Random().nextFloat() > 1 ? -1 : 1;
                s1.chgNum(num);
                s2.chgNum(num);
            }).start();
        }
    }

}

class Stock {

    public Stock(int id, int num) {
        this.id = id;
        this.num = num;
    }

    int id;
    int num;
    Lock lock = new ReentrantLock();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void chgNum(int num) {
        lock.lock();
        try {
            if (num > this.num) {
                Thread.currentThread().sleep(100);
            }
            this.num = this.num + num;
            System.out.println(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
