package com.stocksphere.service

import com.stocksphere.dto.StockMovementRequest
import com.stocksphere.entity.InventoryEntity
import com.stocksphere.entity.StockMovementEntity
import com.stocksphere.exception.InsufficientStockException
import com.stocksphere.repository.InventoryRepository
import com.stocksphere.repository.StockMovementRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.Optional

class StockMovementServiceTest {

    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var stockMovementRepository: StockMovementRepository
    private lateinit var inventoryService: InventoryService
    private lateinit var stockMovementService: StockMovementService

    private fun sampleEntity(qty: Int = 10) = InventoryEntity(
        id = 1L, name = "Laptop", category = "Electronics",
        quantity = qty, unitPrice = BigDecimal("50000.00"),
        minimumStockLevel = 2
    )

    @BeforeEach
    fun setUp() {
        inventoryRepository = mock()
        stockMovementRepository = mock()
        inventoryService = InventoryService(inventoryRepository, stockMovementRepository)
        stockMovementService = StockMovementService(inventoryRepository, stockMovementRepository, inventoryService)
    }

    @Test
    fun `stock IN increases quantity`() {
        val entity = sampleEntity(qty = 10)
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))
        whenever(inventoryRepository.save(any())).thenReturn(entity)
        whenever(stockMovementRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<StockMovementEntity>(0)
        }

        val result = stockMovementService.recordMovement(1L, StockMovementRequest("IN", 5, "Restock"))
        assertEquals("IN", result.movementType)
        assertEquals(10, result.previousQuantity)
        assertEquals(15, result.newQuantity)
        assertEquals(5, result.quantity)
    }

    @Test
    fun `stock OUT decreases quantity`() {
        val entity = sampleEntity(qty = 10)
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))
        whenever(inventoryRepository.save(any())).thenReturn(entity)
        whenever(stockMovementRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<StockMovementEntity>(0)
        }

        val result = stockMovementService.recordMovement(1L, StockMovementRequest("OUT", 4, "Issued"))
        assertEquals("OUT", result.movementType)
        assertEquals(10, result.previousQuantity)
        assertEquals(6, result.newQuantity)
    }

    @Test
    fun `stock OUT throws InsufficientStockException when quantity exceeds available`() {
        val entity = sampleEntity(qty = 3)
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))

        assertThrows<InsufficientStockException> {
            stockMovementService.recordMovement(1L, StockMovementRequest("OUT", 10, null))
        }
    }

    @Test
    fun `stock OUT of exact available quantity brings stock to zero`() {
        val entity = sampleEntity(qty = 5)
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))
        whenever(inventoryRepository.save(any())).thenReturn(entity)
        whenever(stockMovementRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<StockMovementEntity>(0)
        }

        val result = stockMovementService.recordMovement(1L, StockMovementRequest("OUT", 5, null))
        assertEquals(0, result.newQuantity)
    }
}
