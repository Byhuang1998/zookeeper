package com.byhuang.zkcase1;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;

/**
 * @author mskj-huangbingyi
 * @version 1.0
 * @date 2022/10/20 19:16
 * @description TODO
 */
public class DistributedClient {

    private static final String URL = "192.168.119.131:2181";

    private static final String PARENT_NODE = "/servers";


    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        DistributedClient client = new DistributedClient();

        client.connect();

        client.watch();

        client.business();
    }

    // Simulate to deal with business
    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    // Connect to zookeeper
    public void connect() throws IOException {
        zkClient = new ZooKeeper(URL, 20000, (watchedEvent -> {
            try {
                watch();
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }));
    }

    // Listen
    public void watch() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren(PARENT_NODE, true);
        children.forEach(System.out::print);
        System.out.println();
    }

}
