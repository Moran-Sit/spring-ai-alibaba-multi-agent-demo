package com.alibaba.cloud.ai.demo.common.rag.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author Wolf
 * @description ResolveResult
 * @date 2026/3/19 10:28
 */
@Builder
@Data
public class ResolveResult {

    private String kbId;

    private List<String> docIds;

}
