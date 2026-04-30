package com.alibaba.cloud.ai.demo.common.rag.embedding;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;

import java.util.ArrayList;
import java.util.List;

public class LangChain4jEmbeddingModelAdapter implements EmbeddingModel {

    private final dev.langchain4j.model.embedding.EmbeddingModel delegate;

    public LangChain4jEmbeddingModelAdapter(dev.langchain4j.model.embedding.EmbeddingModel delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        List<Embedding> embeddings = new ArrayList<>();
        List<String> instructions = request.getInstructions();
        for (int i = 0; i < instructions.size(); i++) {
            float[] vector = delegate.embed(instructions.get(i)).content().vector();
            embeddings.add(new Embedding(vector, i));
        }
        return new EmbeddingResponse(embeddings);
    }

    @Override
    public float[] embed(Document document) {
        return delegate.embed(document.getText()).content().vector();
    }
}
