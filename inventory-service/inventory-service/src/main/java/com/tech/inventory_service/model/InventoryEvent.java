package com.tech.inventory_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventId;          
    private String orderNumber;
    private String eventType;
    private String details;
    private LocalDateTime processedAt;
}
