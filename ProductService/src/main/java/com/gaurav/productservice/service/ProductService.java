package com.gaurav.productservice.service;

import com.gaurav.productservice.model.ProductRequest;
import com.gaurav.productservice.model.ProductResponse;
import org.springframework.stereotype.Service;


public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);


}
