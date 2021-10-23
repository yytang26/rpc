package com.tyy.client.controller;

import com.tyy.api.dto.ProductDTO;
import com.tyy.api.service.ProductService;
import com.tyy.rpc.annotation.RpcConsumer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:tyy
 * @date:2021/7/10
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @RpcConsumer
    private ProductService productService;

    @RequestMapping("/getById")
    public ProductDTO getProduct(@RequestParam(required = true) Long id){
        ProductDTO productDTO = productService.getProductById(id);
        System.out.println(productDTO.getProductName());
        return productDTO;
    }
}
