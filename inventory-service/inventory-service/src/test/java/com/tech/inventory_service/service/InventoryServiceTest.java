package com.tech.inventory_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tech.inventory_service.model.Inventory;
import com.tech.inventory_service.repository.InventoryRepository;
import com.tech.inventory_service.exceptions.SKUNotFoundException;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void testReserveStock_Success() throws SKUNotFoundException {
        String sku = "SKU123";
        Inventory inventory = new Inventory();
        inventory.setSkuCode(sku);
        inventory.setAvailableQuantity(10);
        inventory.setReservedQuantity(5);

        when(inventoryRepository.findByskuCode(sku)).thenReturn(Optional.of(inventory));

        inventoryService.reserveStock(sku, 3);

        assertEquals(7, inventory.getAvailableQuantity());
        assertEquals(8, inventory.getReservedQuantity());
        verify(inventoryRepository).save(inventory);
    }

    @Test
    void testReserveStock_SKUNotFound() {
        String sku = "SKU999";
        when(inventoryRepository.findByskuCode(sku)).thenReturn(Optional.empty());

        assertThrows(SKUNotFoundException.class, () -> inventoryService.reserveStock(sku, 2));
    }
    
    @Test
    void testReduceReservedStock_Success() {
        String sku = "SKU123";
        Inventory inventory = new Inventory();
        inventory.setSkuCode(sku);
        inventory.setTotalQuantity(20);
        inventory.setReservedQuantity(5);

        when(inventoryRepository.findByskuCode(sku)).thenReturn(Optional.of(inventory));

        inventoryService.reduceReservedStock(sku, 3);

        assertEquals(17, inventory.getTotalQuantity());
        assertEquals(2, inventory.getReservedQuantity());
        verify(inventoryRepository).save(inventory);
    }
}