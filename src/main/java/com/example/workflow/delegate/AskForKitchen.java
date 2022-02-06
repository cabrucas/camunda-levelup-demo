package com.example.workflow.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AskForKitchen implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Trying to ask kitchen...");

        try {
            runtimeService.createMessageCorrelation("ADD_ORDER")
                    .setVariable("parent", delegateExecution.getProcessInstanceId())
                    .setVariable("orderId", delegateExecution.getVariable("orderId"))
                    .correlate();
        } catch (Exception exception) {
            log.error("Error occurred while correlating message.", exception);
        }

        log.info("Kitchen asked successfully.");
    }
}
