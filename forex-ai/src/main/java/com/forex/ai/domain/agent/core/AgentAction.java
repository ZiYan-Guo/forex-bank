package com.forex.ai.domain.agent.core;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AgentAction {
    private String thought;
    private String tool;
    private Map<String, Object> parameters;
    private String observation;
    private String finalAnswer;

    public boolean isFinalAnswer() {
        return finalAnswer != null && !finalAnswer.isBlank();
    }

    public boolean requiresTool() {
        return tool != null && !tool.isBlank();
    }
}
