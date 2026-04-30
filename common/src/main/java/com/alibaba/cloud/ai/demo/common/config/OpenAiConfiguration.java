package com.alibaba.cloud.ai.demo.common.config;


import jakarta.annotation.Resource;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties;
import org.springframework.ai.model.openai.autoconfigure.OpenAiConnectionProperties;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Configuration
public class OpenAiConfiguration {

    @Resource
    private OpenAiChatProperties chatProperties;

    @Resource
    private OpenAiConnectionProperties connectionProperties;

    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl(connectionProperties.getBaseUrl())
                .apiKey(connectionProperties.getApiKey())
                .restClientBuilder(buildRestClient())
                .build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel(OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(chatProperties.getOptions())
                .build();
    }


    private RestClient.Builder buildRestClient(){
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    System.out.println("=== OpenAI Request ===");
                    System.out.println("URI: " + request.getURI());
                    System.out.println("Method: " + request.getMethod());
                    System.out.println("Headers: " + request.getHeaders());
                    System.out.println("Body: " + new String(body, StandardCharsets.UTF_8));

                    var response = execution.execute(request, body);

                    System.out.println("=== OpenAI Response ===");
                    System.out.println("Status: " + response.getStatusCode());

                    return response;
                });
    }

}
