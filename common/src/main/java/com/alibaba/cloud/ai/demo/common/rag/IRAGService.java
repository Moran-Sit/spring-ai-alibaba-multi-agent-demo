package com.alibaba.cloud.ai.demo.common.rag;

import com.alibaba.cloud.ai.demo.common.rag.vo.ResolveResult;
import com.alibaba.cloud.ai.demo.common.rag.vo.RetrieverParam;
import dev.langchain4j.model.TokenCountEstimator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant.*;

public interface IRAGService {

    void ingest(Document document, int overlap, String tokenizer);

    DocumentRetriever createRetriever(RetrieverParam param);

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

    default Filter.Expression buildFilter(ResolveResult resolveResult) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        if (CollectionUtils.isNotEmpty(resolveResult.getDocIds())) {
            return builder.in(DOC_KEY, resolveResult.getDocIds()).build();
        } else if (StringUtils.isNotBlank(resolveResult.getKbId())) {
            return builder.eq(KB_KEY, resolveResult.getKbId()).build();
        }
        throw new RuntimeException("无法构建Filter，缺少必要的知识库ID或文档ID");
    }

    default String buildContent(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return NONE;
        }
        return documents.stream()
                .map(Document::getText)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .limit(DEFAULT_TOP_N)
                .map(text -> LIST + text)
                .collect(Collectors.joining(NEW_LINE)) + NEW_LINE;
    }
}
