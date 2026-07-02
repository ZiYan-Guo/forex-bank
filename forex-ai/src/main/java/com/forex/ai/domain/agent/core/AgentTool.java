package com.forex.ai.domain.agent.core;

import java.util.Map;

public interface AgentTool {

    String name();

    String description();

    Map<String, Object> execute(Map<String, Object> parameters);

    default String parameterSchema() {
        return "{}";
    }
}
