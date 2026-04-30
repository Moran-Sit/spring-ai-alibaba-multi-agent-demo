package com.alibaba.cloud.ai.demo.common.rag;


import com.alibaba.cloud.ai.demo.common.rag.enums.RagType;
import com.alibaba.cloud.ai.demo.common.rag.vo.RetrieverParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;

import java.util.List;

import static com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant.RAG_MAX_SEGMENT_SIZE_IN_TOKENS;
import static com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant.RAG_MIN_SCORE;


@Slf4j
public class EmbeddingRag implements IRAGService {

    /**
     * RAG名称，用于区分不同的实例
     */
    @Getter
    private final RagType type;

    private final VectorStore vectorStore;

    public EmbeddingRag(RagType type, VectorStore vectorStore) {
        this.type = type;
        this.vectorStore = vectorStore;
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
        DocumentTransformer splitter = new TokenTextSplitter(RAG_MAX_SEGMENT_SIZE_IN_TOKENS, overlap, 5, 10000, true);
        List<Document> splitDocuments = splitter.apply(List.of(document));
        vectorStore.add(splitDocuments);
    }

    /**
     * 创建召回器
     *
     * @param param 条件
     * @return DocumentRetriever
     */
    @Override
    public DocumentRetriever createRetriever(RetrieverParam param) {
        RetrieverParam retrieverParam = param == null ? new RetrieverParam() : param;
        VectorStoreDocumentRetriever.Builder builder = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .topK(retrieverParam.getMaxResults() == null || retrieverParam.getMaxResults() <= 0 ? 3 : retrieverParam.getMaxResults())
                .similarityThreshold(retrieverParam.getMinScore() == null || retrieverParam.getMinScore() <= 0 ? RAG_MIN_SCORE : retrieverParam.getMinScore());

        Filter.Expression filter = retrieverParam.getFilter();
        if (filter != null) {
            builder.filterExpression(filter);
        }
        return builder
                .build();
    }
}
