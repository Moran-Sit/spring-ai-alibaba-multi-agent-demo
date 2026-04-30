package com.alibaba.cloud.ai.demo.common.rag;



import com.alibaba.cloud.ai.demo.common.rag.enums.RagType;
import com.alibaba.cloud.ai.demo.common.rag.vo.RetrieverParam;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant.*;


@Slf4j
public class EmbeddingRag implements IRAGService {

    /**
     * RAG名称，用于区分不同的实例
     */
    @Getter
    private final RagType type;

    private final EmbeddingModel embeddingModel;

    private final EmbeddingStore<TextSegment> embeddingStore;

    public EmbeddingRag(RagType type, EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.type = type;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    /**
     * 对文档切块、向量化并存储到数据库
     *
     * @param document 知识库文档
     * @param overlap  重叠token数
     */
    @Override
    public void ingest(Document document, int overlap, String tokenEstimator) {
        log.info("EmbeddingRag ingest,TokenCountEstimator:{}", tokenEstimator);
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(RAG_MAX_SEGMENT_SIZE_IN_TOKENS, overlap, TokenEstimatorFactory.create(tokenEstimator));
        EmbeddingStoreIngestor embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        embeddingStoreIngestor.ingest(document);
    }

    /**
     * 创建召回器
     *
     * @param param 条件
     * @return ContentRetriever
     */
    @Override
    public DefaultEmbeddingStoreContentRetriever createRetriever(RetrieverParam param) {
        return DefaultEmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(param.getMaxResults() == null || param.getMaxResults() <= 0 ? 3 : param.getMaxResults())
                .minScore(param.getMinScore() == null || param.getMinScore() <= 0 ? RAG_MIN_SCORE : param.getMinScore())
                .filter(param.getFilter())
                .breakIfSearchMissed(param.isSearchMissInterrupt())
                .build();
    }
}
