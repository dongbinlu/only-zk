package com.only.zk.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobTask {

    @Autowired
    private MasterResolve masterResolve;

    @Scheduled(cron = "0/5 * * * * ?")
    public void doJodTask() {
        boolean flag = masterResolve.isMaster();

        if (flag) {
            System.out.println("开始执行定时任务");
        } else {
            System.out.println(flag);
        }
    }


}
