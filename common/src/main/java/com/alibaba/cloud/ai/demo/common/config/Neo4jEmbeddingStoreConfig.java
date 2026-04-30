package com.alibaba.cloud.ai.demo.common.config;

import com.alibaba.cloud.ai.demo.common.properties.AdiProperties;
import com.alibaba.cloud.ai.demo.common.utils.AdiPropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.neo4j.Neo4jVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
@ConditionalOnProperty(value = "adi.vector-database", havingValue = "neo4j")
public class Neo4jEmbeddingStoreConfig {

    @Autowired
    private AdiProperties adiProperties;

    @Bean(destroyMethod = "close")
    public Driver neo4jDriver() {
        AdiProperties.Neo4j neo4j = adiProperties.getDatasource().getNeo4j();
        return GraphDatabase.driver("neo4j://" + neo4j.getHost() + ":" + neo4j.getPort(),
                AuthTokens.basic(neo4j.getUsername(), neo4j.getPassword()));
    }

    @Bean(name = "kbVectorStore")
    @Primary
    public VectorStore initKbVectorStore(Driver neo4jDriver, EmbeddingModel embeddingModel) {
        log.info("Initializing kbVectorStore...");
        return createVectorStore(neo4jDriver, embeddingModel, "tea_embedding", "kb_tea_embedding");
    }

    @Bean(name = "convMemoryVectorStore")
    public VectorStore initConvMemoryVectorStore(Driver neo4jDriver, EmbeddingModel embeddingModel) {
        log.info("Initializing convMemoryVectorStore...");
        return createVectorStore(neo4jDriver, embeddingModel, "conv_memory", "adi_conversation_memory_embedding");
    }

    @Bean(name = "searchVectorStore")
    public VectorStore initSearchVectorStore(Driver neo4jDriver, EmbeddingModel embeddingModel) {
        log.info("Initializing searchVectorStore...");
        return createVectorStore(neo4jDriver, embeddingModel, "aisearch", "adi_ai_search_embedding");
    }

    private VectorStore createVectorStore(Driver neo4jDriver, EmbeddingModel embeddingModel, String indexName, String label) {
        Pair<String, Integer> pair = AdiPropertiesUtil.getSuffixAndDimension(adiProperties);
        if (StringUtils.isNotBlank(pair.getLeft())) {
            indexName = indexName + "_" + pair.getLeft();
            label = label + "_" + pair.getLeft();
        }
        log.info("Creating Neo4jVectorStore with label:{},index:{},dimension:{}", label, indexName, pair.getRight());
        return Neo4jVectorStore.builder(neo4jDriver, embeddingModel)
                .databaseName(adiProperties.getDatasource().getNeo4j().getDatabase())
                .indexName(indexName)
                .label(label)
                .embeddingDimension(pair.getRight())
                .initializeSchema(true)
                .build();
    }
}
