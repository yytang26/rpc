package com.tyy.server.remote;


import com.tyy.api.dto.OrderDTO;
import com.tyy.api.service.OrderService;
import com.tyy.rpc.annotation.RpcProvider;

import java.util.Date;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@RpcProvider(name="con.tyy.api.service.OrderService",version="1.0.0")
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderDTO getOrderById(String orderNo) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo(orderNo);
        dto.setTime(new Date().toString());
        return dto;
    }
}
