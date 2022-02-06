package com.example.workflow.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Data
@Builder
@Document
public class Order {

    @Id
    private String id;

    private String pizza;

    private Boolean urgently;

    private Boolean canBeCooked;

    private Boolean canBeDelivered;
}
