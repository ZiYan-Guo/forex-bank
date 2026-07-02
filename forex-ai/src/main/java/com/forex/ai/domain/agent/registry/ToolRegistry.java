package com.forex.ai.domain.agent.registry;

import com.forex.ai.domain.agent.core.AgentTool;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ToolRegistry {

    private final Map<String, AgentTool> tools = new ConcurrentHashMap<>();

    public void register(AgentTool tool) {
        tools.put(tool.name(), tool);
        System.out.println("[Agent] Tool registered: " + tool.name());
    }

    public AgentTool get(String name) {
        AgentTool tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        return tool;
    }

    public Map<String, Object> execute(String toolName, Map<String, Object> parameters) {
        AgentTool tool = get(toolName);
        return tool.execute(parameters);
    }

    public Collection<AgentTool> getAllTools() {
        return tools.values();
    }

    public Set<String> getToolNames() {
        return tools.keySet();
    }
}
