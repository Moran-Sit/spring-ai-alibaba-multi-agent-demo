package com.alibaba.cloud.ai.demo.service;

import com.alibaba.cloud.ai.demo.ConsultSubAgentApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ConsultSubAgentApplication.class)
public class DefaultConsultServiceTest {

    @Autowired
    private DefaultConsultService defaultConsultService;

    @Test
    public void testIngestKnowledge() {
        defaultConsultService.ingestKnowledge();
    }
}
