package com.only.zk.job;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MasterResolve implements ApplicationRunner {

    @Autowired
    private ZkClient zkClient;

    private static final String ROOT_PATH = "/job_master";

    private static final String SERVICE_PATH = ROOT_PATH + "/service";

    private String nodePath;

    private volatile boolean master = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRoot();
        createServerNode();
    }

    public boolean isMaster() {
        return master;
    }

    private void createRoot() {

        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
            System.out.println("------创建父节点成功------");
        }

    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(SERVICE_PATH, "slave");
        System.out.println("-----创建子节点成功-----" + nodePath);

        // 初始化master节点
        initMaster();

        // 初始化后进行监听
        initListener();
    }

    private void initListener() {

        zkClient.subscribeChildChanges(ROOT_PATH, ((parentPath, currentChilds) -> {
            // 执行选举
            doElection();
        }));

    }

    private void initMaster() {

        boolean exist = zkClient.getChildren(ROOT_PATH).stream()
                .map(p -> ROOT_PATH + "/" + p)
                .map(p -> zkClient.readData(p))
                .anyMatch(d -> StringUtils.equals("master", (CharSequence) d));

        if (!exist) {
            // 选举节点最小的为master节点
            doElection();
        }

    }

    private void doElection() {

        Map<String, Object> childData = zkClient.getChildren(ROOT_PATH).stream()
                .map(p -> ROOT_PATH + "/" + p)
                .collect(Collectors.toMap(p -> p, p -> zkClient.readData(p)));

        if (childData.containsValue("master")) {
            return;
        }

        // 进行排序并判断是否存在
        childData.keySet().stream()
                .sorted()
                .findFirst()
                .ifPresent(p -> {
                    // 如果最小的节点是当前节点，选举为master
                    if (StringUtils.equals(p, nodePath)) {
                        zkClient.writeData(nodePath, "master");
                        master = true;
                        System.out.println("当前master:" + nodePath);
                    }
                });
    }
}
