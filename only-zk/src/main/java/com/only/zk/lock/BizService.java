package com.only.zk.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lock")
public class BizService {

    @Autowired
    private ZookeeperLock zookeeperLock;

    @GetMapping
    public String lock() {

        Lock lock = null;
        try {
            lock = zookeeperLock.lock("boy", 10 * 1000);
            if (lock.isActive()) {
                System.out.println("获取锁" + lock.getPath() + "成功。。。执行任务");
            }
        } finally {
            zookeeperLock.unLock(lock);

        }
        return "end";
    }


}
