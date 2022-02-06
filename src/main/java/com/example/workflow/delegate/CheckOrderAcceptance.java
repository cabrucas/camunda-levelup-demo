package com.example.workflow.delegate;

import com.example.workflow.dto.Order;
import com.example.workflow.dto.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckOrderAcceptance implements JavaDelegate {

    private final OrderRepository repository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Trying to check if order can be accepted...");

        try {
            final String orderId = (String) delegateExecution.getVariable("orderId");
            final Order order = repository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " does not exist."));
            if (order.getUrgently()) {
                delegateExecution.setVariable("canBeDelivered", false);
            } else {
                delegateExecution.setVariable("canBeDelivered", true);
            }
        } catch (Exception exception) {
            log.error("Error occurred while checking urgently mark on order.", exception);
            throw new BpmnError("TERMINAL_ERROR");
        }

        log.info("Acceptance checked successfully.");
    }
}
