package com.alibaba.cloud.ai.demo.common.rag.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.vectorstore.filter.Filter;

/**
 * @author Wolf
 * @date 2026/3/17 21:34 * @description RetrieverParam
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RetrieverParam {

    /**
     * 过滤条件
     */
    private Filter.Expression filter;
    /**
     * 最大返回数量
     */
    private Integer maxResults;
    /**
     * 最小命中分数
     */
    private Double minScore;

    /**
     * 如果数据库中搜索不到数据，是否强行中断该搜索，不继续往下执行（即不继续请求LLM进行回答）
     */
    private boolean isSearchMissInterrupt;
}
