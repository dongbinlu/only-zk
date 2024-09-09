package com.only.zk.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;

public class Server {


    public void openServer(int port) {

        // 构建应用
        ApplicationConfig config = new ApplicationConfig();
        config.setName("server-app");

        // 通讯协议
        ProtocolConfig protocolConfig = new ProtocolConfig("dubbo", port);
        protocolConfig.setThreads(200);

        ServiceConfig<PayService> serviceConfig = new ServiceConfig<>();

        serviceConfig.setApplication(config);
        serviceConfig.setProtocol(protocolConfig);
        serviceConfig.setRegistry(new RegistryConfig("zookeeper://10.1.20.73:2181"));
        serviceConfig.setInterface(PayService.class);
        PayServiceImpl ref = new PayServiceImpl();
        serviceConfig.setRef(ref);

        // 开始提供服务
        serviceConfig.export();
        System.out.println("服务已开启，端口为：" + serviceConfig.getExportedUrls().get(0).getPort());

        ref.setPort(serviceConfig.getExportedUrls().get(0).getPort());


    }

    public static void main(String[] args) throws IOException {
        new Server().openServer(10081);
        System.in.read();
    }


}
