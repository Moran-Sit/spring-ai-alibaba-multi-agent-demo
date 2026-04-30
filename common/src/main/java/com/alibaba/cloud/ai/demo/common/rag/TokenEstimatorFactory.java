package com.alibaba.cloud.ai.demo.common.rag;


import com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant;
import dev.langchain4j.model.TokenCountEstimator;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenCountEstimator;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class TokenEstimatorFactory {

    public static TokenCountEstimator create(String tokenEstimator) {
        if (StringUtils.isBlank(tokenEstimator)) {
            return new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_3_5_TURBO);
        }
        if (AdiConstant.TokenEstimator.OPENAI.equals(tokenEstimator)) {
            return new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_3_5_TURBO);
        } else if (AdiConstant.TokenEstimator.HUGGING_FACE.equals(tokenEstimator)) {
            return new HuggingFaceTokenCountEstimator();
        } /*else if (AdiConstant.TokenEstimator.QWEN.equals(tokenEstimator)) {
            AbstractLLMService llmService = LLMContext.getAllServices()
                    .stream()
                    .filter(item -> {
                        AiModel aiModel = item.getAiModel();
                        return aiModel.getPlatform().equals(AdiConstant.ModelPlatform.DASHSCOPE) && aiModel.getType().equals(AdiConstant.ModelType.TEXT);
                    })
                    .findFirst().orElse(null);
            if (null != llmService) {
                return llmService.getTokenEstimator();
            } else {
                log.warn("没有找到Qwen模型的tokenizer，使用默认的OpenAiTokenizer");
                return new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_3_5_TURBO);
            }
        }*/
        return new OpenAiTokenCountEstimator(OpenAiChatModelName.GPT_3_5_TURBO);
    }

}
