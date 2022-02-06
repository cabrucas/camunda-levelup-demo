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
public class CheckIngredients implements JavaDelegate {

    private final OrderRepository repository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Trying to check ingredients...");
        try {
            String orderId = (String) delegateExecution.getVariable("orderId");
            Order order = repository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " does not exist"));
            if (order.getPizza().equalsIgnoreCase("pepperoni")) {
                delegateExecution.setVariable("canBeCooked", true);
            } else {
                delegateExecution.setVariable("canBeCooked", false);
            }

        } catch (Exception exception) {
            log.error("Error occurred while checking if pizza can be cooked.", exception);
            throw new BpmnError("TERMINAL_ERROR");
        }

        log.info("Ingredients checked successfully.");
    }
}
