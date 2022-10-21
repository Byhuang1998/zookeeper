package com.byhuang.distributedlock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author mskj-huangbingyi
 * @version 1.0
 * @date 2022/10/20 21:05
 * @description TODO
 */
public class DistributedLock {

    private static final String URL = "192.168.119.131:2181";

    private static final String PARENT_NODE = "/locks";

    private static final String CHILD_NODE = "/lock";

    private ZooKeeper zkClient;

    private String currentNode;
    private String watchPath;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        zkClient = new ZooKeeper(URL, 200000, (watchedEvent -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectLatch.countDown();
                System.out.println("connect to zookeeper...");
            }
            if (watchedEvent.getType() == Watcher.Event.EventType.NodeDeleted && watchedEvent.getPath().equals(watchPath)) {
                waitLatch.countDown();
            }
        }));

        // Waiting for connecting...
        connectLatch.await();

        Stat exists = zkClient.exists(PARENT_NODE, false);

        if (exists == null) {
            System.out.println("PARENT_NODE not exists");
            zkClient.create(PARENT_NODE, PARENT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("creating parent node");
        }
    }

    public void zkLock() throws InterruptedException, KeeperException {
        String path = PARENT_NODE + CHILD_NODE;
        currentNode = zkClient.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        List<String> childrenSeq1 = zkClient.getChildren(PARENT_NODE, false);
        List<String> childrenSeq = zkClient.getChildren(PARENT_NODE, false)
                .stream().map(s -> s.substring(CHILD_NODE.length() - 1)).sorted().collect(Collectors.toList());
        if (childrenSeq.size() <= 0) {
            System.out.println("error!");
        } else {
            // The smallest seq gets the lock
            String lockSeq = childrenSeq.get(0);
            String currentSeq = currentNode.substring((PARENT_NODE + CHILD_NODE).length());
            if (currentSeq.equals(lockSeq)) {
                return;
            } else {
                // Watch the former seq
                int index = childrenSeq.indexOf(currentSeq);
                watchPath = PARENT_NODE + CHILD_NODE + childrenSeq.get(index - 1);
                zkClient.getData(watchPath, true, new Stat());
                waitLatch.await();
            }
        }
        System.out.println(childrenSeq);
    }

    public void zkUnlock() throws InterruptedException, KeeperException {
        zkClient.delete(currentNode, -1);
    }

}
