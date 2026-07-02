package com.forex.ai.domain.agent.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AgentResult {
    private String answer;
    private List<AgentAction> actionHistory;
    private int iterations;
    private boolean success;
    private String downloadUrl;
    private String reportFormat;
}
