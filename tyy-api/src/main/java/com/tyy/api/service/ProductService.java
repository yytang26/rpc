package com.tyy.api.service;

import com.tyy.api.dto.ProductDTO;

/**
 * @author:tyy
 * @date:2021/7/10
 */
public interface ProductService {

    ProductDTO getProductById(Long id);
}
