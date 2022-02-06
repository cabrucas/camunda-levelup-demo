package com.example.workflow.delegate;

import com.example.workflow.dto.Order;
import com.example.workflow.dto.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveOrder implements JavaDelegate {

    private final OrderRepository repository;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        log.info("Trying to save order to database...");

        try {
            final Order order = repository.save(Order.builder()
                    .pizza((String) delegateExecution.getVariable("pizza"))
                    .urgently((Boolean) delegateExecution.getVariable("urgently"))
                    .build());

            delegateExecution.setVariable("orderId", order.getId());
        } catch (Exception exception) {
            log.error("Error occurred while saving order to database.", exception);
        }

        log.info("Order saved successfully.");
    }
}
