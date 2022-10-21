package com.byhuang.distributedlock.test;

import com.byhuang.distributedlock.DistributedLock;
import org.apache.zookeeper.KeeperException;
import org.junit.Test;

import java.io.IOException;

/**
 * @author mskj-huangbingyi
 * @version 1.0
 * @date 2022/10/21 14:38
 * @description TODO
 */
public class TestZkLock {

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        DistributedLock zkLock = new DistributedLock();
        DistributedLock zkLock1 = new DistributedLock();
        new Thread(() -> {
            try {
                zkLock.zkLock();

                System.out.println(Thread.currentThread().getName() + "get the lock...");
                // Simulate to deal with business
                Thread.sleep(5000);
                zkLock.zkUnlock();
                System.out.println(Thread.currentThread().getName() + "release the lock");
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }, "thread 1 ").start();


        new Thread(() -> {
            try {
                zkLock1.zkLock();

                System.out.println(Thread.currentThread().getName() + "get the lock...");
                // Simulate to deal with business
                Thread.sleep(5000);
                zkLock1.zkUnlock();
                System.out.println(Thread.currentThread().getName() + "release the lock");
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }, "thread 2 ").start();
    }
}
