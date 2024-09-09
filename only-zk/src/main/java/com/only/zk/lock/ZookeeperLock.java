package com.only.zk.lock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ZookeeperLock implements InitializingBean {

    private static final String ROOT_PATH = "/lock";

    @Autowired
    private ZkClient zkClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        createRoot();
    }

    private void createRoot() {
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
        }
    }

    // 创建临时有序节点
    public Lock createLockNode(String lockId) {
        String nodePath = zkClient.createEphemeralSequential(ROOT_PATH + "/" + lockId, "w");
        return new Lock(lockId, nodePath);
    }

    // 获取锁
    public Lock lock(String lockId, long timeout) {
        Lock lockNode = createLockNode(lockId);

        // 尝试激活锁
        lockNode = tryActiveLock(lockNode);

        // 获取锁失败
        if (!lockNode.isActive()) {
            try {
                synchronized (lockNode) {
                    lockNode.wait(timeout);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!lockNode.isActive()) {
            zkClient.delete(lockNode.getPath());
            throw new RuntimeException("获取锁超时");
        }
        return lockNode;
    }

    private Lock tryActiveLock(Lock lockNode) {

        List<String> childrenList = zkClient.getChildren(ROOT_PATH).stream()
                .sorted()
                .map(p -> ROOT_PATH + "/" + p)
                .collect(Collectors.toList());

        String firstNodePath = childrenList.get(0);
        if (StringUtils.equals(firstNodePath, lockNode.getPath())) {
            lockNode.setActive(true);
        } else {
            String upNodePath = childrenList.get(childrenList.indexOf(lockNode.getPath()) - 1);
            zkClient.subscribeDataChanges(upNodePath, new IZkDataListener() {
                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    System.out.println("------上个节点已删除-------" + dataPath);
                    Lock lock = tryActiveLock(lockNode);
                    synchronized (lockNode) {
                        if (lock.isActive()) {
                            lockNode.notify();
                        }
                    }
                    zkClient.subscribeDataChanges(upNodePath, this);
                }

                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {
                    // do nothing
                }
            });
        }

        return lockNode;
    }

    public void unLock(Lock lock) {
        if (null != lock) {
            if (lock.isActive()) {
                System.out.println("释放锁" + lock.getPath() + "成功");
                zkClient.delete(lock.getPath());
            }
        }
    }

}
