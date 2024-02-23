package com.gaurav.productservice.service;

import com.gaurav.productservice.entity.Product;
import com.gaurav.productservice.exception.ProductServiceException;
import com.gaurav.productservice.model.ProductRequest;
import com.gaurav.productservice.model.ProductResponse;
import com.gaurav.productservice.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{


    @Autowired
    ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("adding product");

        Product product= Product.builder().productName(productRequest.getName()).quantity(productRequest.getQuantity()).price(productRequest.getPrice()).build();
        productRepository.save(product);
        log.info("Product created");
        return product.getProductId();
    }

    @Override
    @Cacheable(value = "products", key = "#productId")
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for productId: {}",productId);
        Product product=productRepository.findById(productId).orElseThrow(()-> new ProductServiceException("Product with given id not found","PRODUCT_NOT_FOUND"));
        ProductResponse productResponse=new ProductResponse();
        copyProperties(product,productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity {} for Id: {}",quantity,productId);
        Product product=productRepository.findById(productId).orElseThrow(() ->new ProductServiceException("Product With Given Id Not Found","PRODUCT_NOT_FOUND"));

        if(product.getQuantity()< quantity){
          throw new ProductServiceException("Product does not have sufficient Quantity","INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity()-quantity);
        productRepository.save(product);
        log.info("Product Quantity updated Successfully");
    }
}
