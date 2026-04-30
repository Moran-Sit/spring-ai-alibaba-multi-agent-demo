/*
 * Copyright 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.demo.service;

import com.alibaba.cloud.ai.demo.common.rag.EmbeddingRag;
import com.alibaba.cloud.ai.demo.common.rag.vo.RetrieverParam;
import com.alibaba.cloud.ai.demo.entity.Product;
import com.alibaba.cloud.ai.demo.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 咨询知识库服务类
 * 提供奶茶店产品和店铺信息的检索服务
 */
@Service
public class DefaultConsultService implements IConsultService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConsultService.class);

    @Autowired
    private EmbeddingRag knowledgeBaseRag;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void ingestKnowledge() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

            Resource[] resources = resolver.getResources("classpath*:knowledge/**");
            Arrays.stream(resources).forEach(resource -> {
                try {
                    String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                    Map<String, Object> metadata = new HashMap<>();
                    metadata.put("filename", resource.getFilename());
                    metadata.put("timestamp", System.currentTimeMillis());
                    Document document = new Document(content, metadata);
                    knowledgeBaseRag.ingest(document, 100, null);
                } catch (IOException e) {
                    logger.error("Failed to read resource: {}", resource.getFilename(), e);
                }
            });
        } catch (Exception e) {
            logger.error("Failed to ingest knowledge", e);
        }
    }

    /**
     * 根据查询内容检索知识库
     */
    public String searchKnowledge(String query) {
        logger.info("=== ConsultService.searchKnowledge 入口 ===");
        logger.info("请求参数 - query: {}", query);

        try {
            List<Document> documents = knowledgeBaseRag.createRetriever(buildRetrieverParam()).retrieve(new Query(query));
            logger.info("检索到文档数量: {}", documents.size());

            if (documents.isEmpty()) {
                String result = "未找到相关资料，查询内容：" + query;
                logger.info("=== ConsultService.searchKnowledge 出口 ===");
                logger.info("返回结果: {}", result);
                return result;
            }

            String finalResult = knowledgeBaseRag.buildContent(documents);
            logger.info("=== ConsultService.searchKnowledge 出口 ===");
            logger.info("返回结果长度: {} 字符", finalResult.length());
            logger.info("返回结果预览: {}", finalResult.length() > 500 ? finalResult.substring(0, 500) + "..." : finalResult);

            return finalResult;
        } catch (Exception e) {
            logger.error("知识库检索异常", e);
            String errorResult = "知识库检索失败: " + e.getMessage() + "，查询内容：" + query;
            logger.info("=== ConsultService.searchKnowledge 出口 ===");
            logger.info("返回错误结果: {}", errorResult);
            return errorResult;
        }
    }

    private RetrieverParam buildRetrieverParam() {
        return this.buildRetrieverParam(null, false);
    }

    private RetrieverParam buildRetrieverParam(Filter.Expression filter, boolean isInterrupt) {
        RetrieverParam.RetrieverParamBuilder paramBuilder = RetrieverParam.builder();
        paramBuilder.isSearchMissInterrupt(isInterrupt)
                .filter(filter);
        return paramBuilder.build();
    }

    /**
     * 获取所有可用产品列表
     */
    public List<Product> getAllProducts() {
        logger.info("=== ConsultService.getAllProducts 入口 ===");

        try {
            List<Product> products = productMapper.selectAllAvailable();

            logger.info("=== ConsultService.getAllProducts 出口 ===");
            logger.info("返回结果 - 产品总数: {}", products.size());

            return products;
        } catch (Exception e) {
            logger.error("获取产品列表异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据产品名称获取产品详情
     */
    public Product getProductByName(String productName) {
        logger.info("=== ConsultService.getProductByName 入口 ===");
        logger.info("请求参数 - productName: {}", productName);

        try {
            Product product = productMapper.selectByNameAndStatus(productName, 1);

            logger.info("=== ConsultService.getProductByName 出口 ===");
            logger.info("返回结果 - product: {}", product != null ? product.getName() : "null");

            return product;
        } catch (Exception e) {
            logger.error("获取产品详情异常", e);
            return null;
        }
    }

    /**
     * 根据产品名称模糊搜索产品列表
     */
    public List<Product> searchProductsByName(String productName) {
        logger.info("=== ConsultService.searchProductsByName 入口 ===");
        logger.info("请求参数 - productName: {}", productName);

        try {
            List<Product> products = productMapper.selectByNameLike(productName);

            logger.info("=== ConsultService.searchProductsByName 出口 ===");
            logger.info("返回结果 - 产品总数: {}", products.size());

            return products;
        } catch (Exception e) {
            logger.error("搜索产品异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 验证产品是否存在且可用
     */
    public boolean validateProduct(String productName) {
        logger.info("=== ConsultService.validateProduct 入口 ===");
        logger.info("请求参数 - productName: {}", productName);

        try {
            boolean exists = productMapper.existsByNameAndStatusTrue(productName) > 0;

            logger.info("=== ConsultService.validateProduct 出口 ===");
            logger.info("返回结果 - exists: {}", exists);

            return exists;
        } catch (Exception e) {
            logger.error("验证产品异常", e);
            return false;
        }
    }
}
