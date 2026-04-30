package com.alibaba.cloud.ai.demo.service;

import com.alibaba.cloud.ai.demo.entity.Product;

import java.util.List;

/**
 * @description IConsultService 
 * @author Wolf
 * @date 2026/4/27 13:38
 */
public interface IConsultService {

    default void initRetriever() {}

    default void ingestKnowledge() {}

    String searchKnowledge(String query);

    List<Product> getAllProducts();

    List<Product> searchProductsByName(String productName);

    Product getProductByName(String productName);

    boolean validateProduct(String productName);



}
