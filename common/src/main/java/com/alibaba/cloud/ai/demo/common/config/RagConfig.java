package com.alibaba.cloud.ai.demo.common.config;

import com.alibaba.cloud.ai.demo.common.rag.EmbeddingRag;
import com.alibaba.cloud.ai.demo.common.rag.enums.RagType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.vectorstore.VectorStore;

@Slf4j
@Configuration
public class RagConfig {

    @Bean
    public EmbeddingRag knowledgeBaseRag(VectorStore kbVectorStore) {
        return new EmbeddingRag(RagType.KNOWLEDGE_BASE, kbVectorStore);
    }
}
