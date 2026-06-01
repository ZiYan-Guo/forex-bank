package com.forex.clearing.domain.event;

import com.forex.common.base.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class InstructionSentEvent extends BaseDomainEvent {

    private final Long instructionId;
    private final String instructionNo;
    private final String clearingChannel;

    public InstructionSentEvent(Long instructionId, String instructionNo, String clearingChannel) {
        super();
        this.instructionId = instructionId;
        this.instructionNo = instructionNo;
        this.clearingChannel = clearingChannel;
    }

    @Override
    public String eventName() {
        return "ClearingInstructionSent";
    }
}
