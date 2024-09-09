package com.only.zk.agent.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.only.zk.entity.OsBean;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agent")
@Slf4j
public class AgentListener implements InitializingBean {


    private static final String ROOT_PATH = "/cluster_management";

    @Autowired
    private ZkClient zkClient;

    private Map<String, OsBean> map = Maps.newHashMap();

    @GetMapping
    public List<OsBean> getCurrentOsBeans() {
        List<OsBean> osBeans = zkClient.getChildren(ROOT_PATH).stream().map(p -> ROOT_PATH + "/" + p)
                .map(p -> convert(zkClient.readData(p))).collect(Collectors.toList());
        return osBeans;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        initSubscribeListener();
    }

    /**
     * 初始化订阅事件
     */
    private void initSubscribeListener() {
        // 订退
        zkClient.unsubscribeAll();

        // 获取子节点
        zkClient.getChildren(ROOT_PATH).stream()
                .map(p -> ROOT_PATH + "/" + p)
                .forEach(p -> {
                    // 订阅数据变化事件
                    zkClient.subscribeDataChanges(p, new DataChanges());
                });

        // 订阅子节点变化事件
        zkClient.subscribeChildChanges(ROOT_PATH,
                (parentPath, currentChilds) -> initSubscribeListener());

    }

    /**
     * 订阅数据变化事件
     */
    private class DataChanges implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object obj) throws Exception {
            OsBean osBean = convert((String) obj);
            map.put(dataPath, osBean);
            doFilter(osBean);
        }

        @Override
        public void handleDataDeleted(String dataPath) throws Exception {
            if (map.containsKey(dataPath)) {
                OsBean osBean = map.get(dataPath);
                log.info("------服务已下线-----" + osBean);
                map.remove(dataPath);
            }
        }
    }

    // 警告过滤
    private void doFilter(OsBean osBean) {
        // cpu 超过10%报警
        if (osBean.getCpu() > 10) {
            log.info("------cpu报警--------" + osBean.getCpu());
        }

    }

    public OsBean convert(String json) {
        try {
            return new ObjectMapper().readValue(json, OsBean.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
