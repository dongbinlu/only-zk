package com.only.zk.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "zk")
public class ZooKeeperProperties {

    private String zkUrl;

    private int sessionTimeout;

    private int connectionTimeout;

}
