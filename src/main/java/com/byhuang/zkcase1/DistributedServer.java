package com.byhuang.zkcase1;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * @author mskj-huangbingyi
 * @version 1.0
 * @date 2022/10/20 18:55
 * @description TODO
 */
public class DistributedServer {

    private static final String URL = "192.168.119.131:2181";

    private static final String PARENT_NODE = "/servers/";

    private static final String SERVER_NAME = "server2";

    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        DistributedServer server = new DistributedServer();

        server.connect();

        server.register();

        server.business();

    }

    private void business() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
    }

    private void register() throws InterruptedException, KeeperException {
        zkClient.create(PARENT_NODE + SERVER_NAME, SERVER_NAME.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    // connect to zookeeper cluster
    public void connect() throws IOException {
        zkClient = new ZooKeeper(URL, 20000, (event) -> {

        });
    }
}
