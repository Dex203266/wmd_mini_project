package com.stocksphere.service

import com.stocksphere.dto.InventoryRequest
import com.stocksphere.entity.InventoryEntity
import com.stocksphere.exception.InvalidOperationException
import com.stocksphere.exception.ResourceNotFoundException
import com.stocksphere.repository.InventoryRepository
import com.stocksphere.repository.StockMovementRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.util.Optional

class InventoryServiceTest {

    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var stockMovementRepository: StockMovementRepository
    private lateinit var inventoryService: InventoryService

    private fun sampleEntity(id: Long = 1L, qty: Int = 10, min: Int = 3) = InventoryEntity(
        id = id, name = "Test Item", category = "Electronics",
        quantity = qty, unitPrice = BigDecimal("1000.00"),
        minimumStockLevel = min
    )

    private fun sampleRequest(qty: Int = 10, price: Double = 1000.0) = InventoryRequest(
        name = "Test Item", category = "Electronics",
        quantity = qty, unitPrice = price,
        minimumStockLevel = 3
    )

    @BeforeEach
    fun setUp() {
        inventoryRepository = mock()
        stockMovementRepository = mock()
        inventoryService = InventoryService(inventoryRepository, stockMovementRepository)
    }

    @Test
    fun `getAll returns mapped responses`() {
        whenever(inventoryRepository.findAll()).thenReturn(listOf(sampleEntity()))
        val result = inventoryService.getAll()
        assertEquals(1, result.size)
        assertEquals("Test Item", result[0].name)
    }

    @Test
    fun `getById returns item when found`() {
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleEntity()))
        val result = inventoryService.getById(1L)
        assertEquals(1L, result.id)
    }

    @Test
    fun `getById throws ResourceNotFoundException when not found`() {
        whenever(inventoryRepository.findById(99L)).thenReturn(Optional.empty())
        assertThrows<ResourceNotFoundException> { inventoryService.getById(99L) }
    }

    @Test
    fun `create saves and returns new item`() {
        val entity = sampleEntity()
        whenever(inventoryRepository.save(any())).thenReturn(entity)
        val result = inventoryService.create(sampleRequest())
        assertEquals("Test Item", result.name)
        verify(inventoryRepository).save(any())
    }

    @Test
    fun `update modifies existing item`() {
        val entity = sampleEntity()
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))
        whenever(inventoryRepository.save(any())).thenReturn(entity)
        val result = inventoryService.update(1L, sampleRequest(qty = 20))
        assertNotNull(result)
        verify(inventoryRepository).save(any())
    }

    @Test
    fun `delete succeeds when no movements exist`() {
        val entity = sampleEntity()
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(entity))
        whenever(stockMovementRepository.countByInventoryItemId(1L)).thenReturn(0L)
        inventoryService.delete(1L)
        verify(inventoryRepository).delete(entity)
    }

    @Test
    fun `delete throws InvalidOperationException when movement history exists`() {
        whenever(inventoryRepository.findById(1L)).thenReturn(Optional.of(sampleEntity()))
        whenever(stockMovementRepository.countByInventoryItemId(1L)).thenReturn(3L)
        assertThrows<InvalidOperationException> { inventoryService.delete(1L) }
    }

    @Test
    fun `getStats calculates low stock and out of stock correctly`() {
        val items = listOf(
            sampleEntity(1L, qty = 0,  min = 5),   // OUT_OF_STOCK
            sampleEntity(2L, qty = 2,  min = 5),   // LOW_STOCK
            sampleEntity(3L, qty = 10, min = 3)    // IN_STOCK
        )
        whenever(inventoryRepository.findAll()).thenReturn(items)
        val stats = inventoryService.getStats()
        assertEquals(3L, stats.totalProducts)
        assertEquals(1L, stats.outOfStockCount)
        assertEquals(1L, stats.lowStockCount)
    }

    @Test
    fun `getStats calculates inventory value correctly`() {
        val items = listOf(
            sampleEntity(1L, qty = 5).apply {  },  // 5 * 1000 = 5000
            sampleEntity(2L, qty = 3)               // 3 * 1000 = 3000
        )
        whenever(inventoryRepository.findAll()).thenReturn(items)
        val stats = inventoryService.getStats()
        assertEquals(BigDecimal("8000.00"), stats.inventoryValue)
    }
}
