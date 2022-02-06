package com.example.workflow.delegate;

import com.example.workflow.dto.Order;
import com.example.workflow.dto.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendKitchenResponse implements JavaDelegate {

    private final RuntimeService runtimeService;
    private final OrderRepository repository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Trying to form and send order response from kitchen...");

        try {
            final String orderId = (String) delegateExecution.getVariable("orderId");
            final Boolean canBeCooked = (Boolean) delegateExecution.getVariable("canBeCooked");
            final Boolean canBeDelivered = (Boolean) delegateExecution.getVariable("canBeDelivered");
            Order order = repository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order with id " + orderId + " does not exist"));
            order.setCanBeDelivered(canBeDelivered);
            order.setCanBeCooked(canBeCooked);

            repository.save(order);

            if (canBeCooked && canBeDelivered) {
                runtimeService.createMessageCorrelation("ORDER_ADDED")
                        .processInstanceId((String) delegateExecution.getVariable("parent"))
                        .setVariable("approved", true)
                        .correlate();
            } else {
                runtimeService.createMessageCorrelation("ORDER_ADDED")
                        .processInstanceId((String) delegateExecution.getVariable("parent"))
                        .setVariable("approved", false)
                        .correlate();
            }
        } catch (Exception exception) {
            log.error("Error occurred while correlating message.", exception);
        }

        log.info("Order response sent successfully.");
    }
}
