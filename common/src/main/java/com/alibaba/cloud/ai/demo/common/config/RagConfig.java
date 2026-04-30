package com.alibaba.cloud.ai.demo.common.config;

import com.alibaba.cloud.ai.demo.common.rag.EmbeddingRag;
import com.alibaba.cloud.ai.demo.common.rag.enums.RagType;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RagConfig {

    @Bean
    public EmbeddingRag knowledgeBaseRag(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> kbEmbeddingStore) {
        return new EmbeddingRag(RagType.KNOWLEDGE_BASE, embeddingModel, kbEmbeddingStore);
    }
}
