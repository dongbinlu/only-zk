package com.only.zk.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.only.zk.entity.OsBean;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 收集数据并上报
 */
@Component
@Slf4j
public class Agent implements ApplicationRunner {


    private static final String ROOT_PATH = "/cluster_management";

    private static final String SERVICE_PATH = ROOT_PATH + "/service";

    private String nodePath;

    @Autowired
    private ZkClient zkClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createRoot();
        createServerNode();

        Thread stateThread = new Thread(() -> {
            while (true) {
                updateServerNode();
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, "zk_state_thread");
        // 将此线程至为守护线程
        stateThread.setDaemon(true);
        stateThread.start();
    }

    private void updateServerNode() {
        zkClient.writeData(nodePath, getOsInfo());
        log.info("-----更新节点数据------");
    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(SERVICE_PATH, getOsInfo());
        log.info("----创建临时有序的子节点成功----" + nodePath);
    }

    private void createRoot() {
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
            log.info("----创建持久节点成功----");
        }
    }

    public String getOsInfo() {
        MemoryUsage memoryUsag = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        OsBean osBean = OsBean.builder()
                .lastUpdateTime(System.currentTimeMillis())
                .ip(getLocalIp())
                .cpu(RandomUtils.nextInt(3, 80))
                .usableMemorySize(memoryUsag.getUsed() / 1024 / 1024)
                .maxmemorySize(memoryUsag.getMax() / 1024 / 1024)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(osBean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return addr.getHostAddress();
    }
}
