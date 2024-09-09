package com.only.zk.dubbo;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;

public class Client {

    PayService payService;

    // url远程服务的调用地址
    public PayService buildService(String url) {

        ApplicationConfig config = new ApplicationConfig();
        config.setName("client-app");

        // 构建一个引用对象
        ReferenceConfig<PayService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(config);

        referenceConfig.setInterface(PayService.class);
        referenceConfig.setUrl(url);
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://10.1.20.73:2181"));
        referenceConfig.setTimeout(5000);
        //透明化
        this.payService = referenceConfig.get();
        return payService;


    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.buildService("");
        String cmd;
        while (!(cmd = read()).equals("exit")) {
            int count = client.payService.caclulate(Integer.parseInt(cmd), Integer.parseInt(cmd));
            System.out.println(count);
        }

    }

    private static String read() throws Exception {
        byte[] b = new byte[1024];
        int size = System.in.read(b);
        return new String(b, 0, size).trim();

    }


}
