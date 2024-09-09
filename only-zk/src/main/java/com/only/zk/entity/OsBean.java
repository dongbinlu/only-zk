package com.only.zk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OsBean {


    private long lastUpdateTime;

    private String ip;

    private int cpu;

    private long usableMemorySize;

    private long maxmemorySize;

}
