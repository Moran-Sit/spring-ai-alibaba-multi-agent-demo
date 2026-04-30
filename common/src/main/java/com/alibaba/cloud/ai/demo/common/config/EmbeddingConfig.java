package com.alibaba.cloud.ai.demo.common.config;

import com.alibaba.cloud.ai.demo.common.cosntant.AdiConstant;
import com.alibaba.cloud.ai.demo.common.properties.AdiProperties;
import com.alibaba.cloud.ai.demo.common.rag.embedding.LangChain4jEmbeddingModelAdapter;

import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallzhv15.BgeSmallZhV15EmbeddingModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class EmbeddingConfig {

    @Resource
    private AdiProperties adiProperties;

    /**
     * 初始化EmbeddingModel(单例),根据配置的embeddingModel选择不同的实现
     *
     * @return EmbeddingModel实例
     */
    @Bean
    @Primary
//    @DependsOn({"initializer","springUtil"})
    public org.springframework.ai.embedding.EmbeddingModel initEmbeddingModel() {
        if (adiProperties.getEmbeddingModel().equals(AdiConstant.EmbeddingModel.ALL_MINILM_L6)) {
            return new LangChain4jEmbeddingModelAdapter(new AllMiniLmL6V2EmbeddingModel());
        }
//        if (adiProperties.getEmbeddingModel().equalsIgnoreCase(AdiConstant.EmbeddingModel.BGE_SMALL_ZH_V15)) {
            return new LangChain4jEmbeddingModelAdapter(new BgeSmallZhV15EmbeddingModel());
//        }
//        ModelPlatformService modelPlatformService = SpringUtil.getBean(ModelPlatformService.class);
//        AiModel aiModel = AdiPropertiesUtil.getEmbeddingModelByProperty(adiProperties);
//        if (aiModel.getPlatform().equals(AdiConstant.ModelPlatform.DASHSCOPE)) {
//            return new DashScopeEmbeddingModelService(aiModel, modelPlatformService.getByName(aiModel.getPlatform())).buildModel();
//        } else if (aiModel.getPlatform().equals(AdiConstant.ModelPlatform.OPENAI)) {
//            return new OpenAiEmbeddingModelService(aiModel, modelPlatformService.getByName(aiModel.getPlatform())).buildModel();
//        } else {
//            throw new RuntimeException("Unsupported embedding model: " + adiProperties.getEmbeddingModel());
//        }
    }

}
