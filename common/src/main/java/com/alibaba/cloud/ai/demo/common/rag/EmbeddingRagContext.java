package com.alibaba.cloud.ai.demo.common.rag;

import com.alibaba.cloud.ai.demo.common.rag.enums.RagType;

import java.util.HashMap;
import java.util.Map;

public class EmbeddingRagContext {

    protected static final Map<RagType, EmbeddingRag> TYPE_TO_RAG = new HashMap<>();

    private EmbeddingRagContext() {
    }

    public static EmbeddingRag get(RagType type) {
        return TYPE_TO_RAG.get(type);
    }

    public static void add(EmbeddingRag rag) {
        TYPE_TO_RAG.put(rag.getType(), rag);
    }
}
