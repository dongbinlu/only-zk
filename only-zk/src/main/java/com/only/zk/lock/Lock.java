package com.only.zk.lock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lock {

    // 建立资源的ID
    private String lockId;

    // 路径
    private String path;

    // 是否激活
    private boolean active;

    public Lock(String lockId, String path) {
        this.lockId = lockId;
        this.path = path;
    }

}
