package com.only.zk.config;

import com.only.zk.properties.ZooKeeperProperties;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableConfigurationProperties(ZooKeeperProperties.class)
@ComponentScan(basePackages = {"com.only.zk"})
@EnableScheduling
@EnableAsync
public class MainConfig {

    @Autowired
    private ZooKeeperProperties zooKeeperProperties;

    @Bean
    public ZkClient zkClient() {
        ZkClient zkClient = new ZkClient(zooKeeperProperties.getZkUrl(),
                zooKeeperProperties.getSessionTimeout(),
                zooKeeperProperties.getConnectionTimeout());
        return zkClient;
    }
}
