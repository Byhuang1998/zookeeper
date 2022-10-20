package com.byhuang.zk;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author mskj-huangbingyi
 * @version 1.0
 * @date 2022/10/20 16:36
 * @description TODO
 */
public class ZkClient {

    private static final String connectString = "192.168.119.131:2181";

    private static final int sessionTimeout = 20000;

    private ZooKeeper zkClient = null;

    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, (event) -> {
            System.out.println("监听后的回调函数，这里处理用户的逻辑");

            try {
                List<String> children = zkClient.getChildren("/", true);
                children.forEach(System.out::println);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void create() throws InterruptedException, KeeperException {
        zkClient.create("/ming", "ming".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void getChildren() throws InterruptedException, KeeperException {
        List<String> children = zkClient.getChildren("/", true);
        children.forEach(System.out::println);
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void isExist() throws InterruptedException, KeeperException {
        Stat stat = zkClient.exists("/ming", false);
        System.out.println(stat == null ? "not exist" : "exist");
    }

}
