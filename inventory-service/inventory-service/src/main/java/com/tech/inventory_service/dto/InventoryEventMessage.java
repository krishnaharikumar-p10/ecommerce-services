package com.tech.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEventMessage {
    private String eventId;
    private String orderNumber;
    private String eventType; 
}
