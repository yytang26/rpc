package com.tyy.api.service;

import com.tyy.api.dto.OrderDTO;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public interface OrderService {

    OrderDTO getOrderById(String orderNo);
}
