package com.only.zk.dubbo;

import lombok.Data;

@Data
public class PayServiceImpl implements PayService {

    private int port;

    @Override
    public int caclulate(int x, int y) {
        return x + y;
    }
}
