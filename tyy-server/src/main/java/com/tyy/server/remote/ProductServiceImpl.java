package com.tyy.server.remote;

import com.tyy.api.dto.ProductDTO;
import com.tyy.api.service.ProductService;
import com.tyy.rpc.annotation.RpcProvider;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@RpcProvider(name="con.tyy.api.service.ProductService",version="1.0.0")
public class ProductServiceImpl implements ProductService {

    @Override
    public ProductDTO getProductById(Long id) {
        ProductDTO dto = new ProductDTO();
        dto.setCategory("A类");
        dto.setProductName("产品名称");
        return dto;
    }
}
