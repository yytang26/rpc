package com.tyy.client.controller;

import com.tyy.api.dto.OrderDTO;
import com.tyy.api.service.OrderService;
import com.tyy.rpc.annotation.RpcConsumer;
import com.tyy.rpc.annotation.RpcProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @RpcConsumer
    private OrderService orderService;

    @RequestMapping("/getByOrderNo")
    public OrderDTO getProduct(@RequestParam(required = true) String orderNo) {
        OrderDTO orderDTO = orderService.getOrderById(orderNo);
        System.out.println(orderDTO.getTime());
        return orderDTO;
    }
}
