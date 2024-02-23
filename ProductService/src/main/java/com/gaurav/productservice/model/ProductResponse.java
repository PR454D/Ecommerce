package com.gaurav.productservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductResponse implements Serializable {
    private String productName;
    private long productId;
    private long quantity;
    private long price;

}
