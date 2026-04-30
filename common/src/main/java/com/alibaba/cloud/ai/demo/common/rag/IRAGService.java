package com.alibaba.cloud.ai.demo.common.rag;

import com.alibaba.cloud.ai.demo.common.rag.vo.ResolveResult;
import com.alibaba.cloud.ai.demo.common.rag.vo.RetrieverParam;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant.*;

public interface IRAGService {


    void ingest(Document document, int overlap, String tokenizer);

    ContentRetriever createRetriever(RetrieverParam param);


    /**
     * 根据模型的contentWindow计算使用该模型最多召回的文档数量
     * <br/>以分块时的最大文本段对应的token数量{maxSegmentSizeInTokens}为计算因子
     *
     * @param userQuestion   用户的问题
     * @param maxInputTokens AI模型所能容纳的窗口大小
     * @return
     */
    default int getRetrieveMaxResults(String userQuestion, int maxInputTokens) {
        if (maxInputTokens == 0) {
            return RAG_RETRIEVE_NUMBER_MAX;
        }
        Integer questionTokenCount = isValidAndGetTokenCount(userQuestion, maxInputTokens);
        int maxRetrieveDocLength = maxInputTokens - questionTokenCount;
        if (maxRetrieveDocLength > RAG_RETRIEVE_NUMBER_MAX * RAG_MAX_SEGMENT_SIZE_IN_TOKENS) {
            return RAG_RETRIEVE_NUMBER_MAX;
        } else {
            return maxRetrieveDocLength / RAG_MAX_SEGMENT_SIZE_IN_TOKENS;
        }

    }

    default Integer isValidAndGetTokenCount(String userQuestion, int maxInputTokens) {
        return isValidAndGetTokenCount(userQuestion, maxInputTokens, TokenEstimatorFactory.create(TokenEstimatorThreadLocal.getTokenEstimator()));
    }

    default Integer isValidAndGetTokenCount(String userQuestion, int maxInputTokens, TokenCountEstimator tokenizer) {
        int questionTokenCount = tokenizer.estimateTokenCountInText(userQuestion);
        if (questionTokenCount > maxInputTokens) {
            throw new IllegalArgumentException("用户问题过长,已超过最大输入Token" + maxInputTokens);
        }
        return questionTokenCount;
    }

    default Filter buildFilter(ResolveResult resolveResult) {
        if (CollectionUtils.isNotEmpty(resolveResult.getDocIds())) {
            return new IsIn(DOC_KEY, resolveResult.getDocIds());
        } else if (StringUtils.isNotBlank(resolveResult.getKbId())) {
            return new IsEqualTo(KB_KEY, resolveResult.getKbId());
        }
        throw new RuntimeException("无法构建Filter，缺乏必要的知识库ID或文档ID");
    }

    default String buildContent(List<Content> contents) {
        if (contents == null || contents.isEmpty()) {
            return NONE;
        }
        return contents.stream()
                .map(c -> c.textSegment().text().trim())
                .filter(s -> !s.isEmpty())
                .distinct()
                .limit(DEFAULT_TOP_N)
                .map(text -> LIST + text)
                .collect(Collectors.joining(NEW_LINE)) + NEW_LINE;
    }
}
