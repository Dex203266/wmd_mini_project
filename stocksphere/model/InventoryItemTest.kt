package com.stocksphere.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InventoryItemTest {

    private fun makeElectronic(qty: Int, min: Int, warrantyYears: Int = 1, critical: Boolean = false) =
        ElectronicItem(1, "Test Item", "Electronics", qty, 1000.0, null, null, null, min,
            warrantyYears = warrantyYears, isCriticalEquipment = critical)

    private fun makeOfficeSupply(qty: Int, min: Int, reorderLevel: Int = min, pastRestocks: Int = 0) =
        OfficeSupplyItem(2, "Pen", "Stationery", qty, 10.0, null, null, null, min,
            reorderLevel = reorderLevel, pastRestockCount = pastRestocks)

    // ── Stock Status ──────────────────────────────────────────────────────────

    @Test fun `quantity 0 is OUT_OF_STOCK`() {
        assertEquals(StockStatus.OUT_OF_STOCK, makeElectronic(0, 5).getStockStatus())
    }

    @Test fun `quantity equal to minimum is LOW_STOCK`() {
        assertEquals(StockStatus.LOW_STOCK, makeElectronic(5, 5).getStockStatus())
    }

    @Test fun `quantity below minimum is LOW_STOCK`() {
        assertEquals(StockStatus.LOW_STOCK, makeElectronic(3, 5).getStockStatus())
    }

    @Test fun `quantity above minimum is IN_STOCK`() {
        assertEquals(StockStatus.IN_STOCK, makeElectronic(10, 5).getStockStatus())
    }

    // ── Total Value ───────────────────────────────────────────────────────────

    @Test fun `calculateTotalValue returns qty times price`() {
        val item = makeElectronic(10, 5)
        assertEquals(10_000.0, item.calculateTotalValue())
    }

    @Test fun `calculateTotalValue is zero when quantity is zero`() {
        assertEquals(0.0, makeElectronic(0, 5).calculateTotalValue())
    }

    // ── Inventory Score — ElectronicItem ─────────────────────────────────────

    @Test fun `critical equipment gets score bonus`() {
        val critical    = makeElectronic(10, 5, warrantyYears = 2, critical = true)
        val nonCritical = makeElectronic(10, 5, warrantyYears = 2, critical = false)
        assertTrue(critical.calculateInventoryScore() > nonCritical.calculateInventoryScore())
    }

    @Test fun `score is bounded between 0 and 100`() {
        val score = makeElectronic(1000, 1, warrantyYears = 10, critical = true).calculateInventoryScore()
        assertTrue(score in 0..100)
    }

    // ── Inventory Score — OfficeSupplyItem ────────────────────────────────────

    @Test fun `past restocks improve office supply score`() {
        val frequent = makeOfficeSupply(10, 5, pastRestocks = 5)
        val fresh    = makeOfficeSupply(10, 5, pastRestocks = 0)
        assertTrue(frequent.calculateInventoryScore() > fresh.calculateInventoryScore())
    }

    // ── Validation ────────────────────────────────────────────────────────────

    @Test fun `negative quantity throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> { makeElectronic(-1, 5) }
    }

    @Test fun `negative price throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            ElectronicItem(1, "X", "Electronics", 0, -1.0, null, null, null, 0)
        }
    }

    @Test fun `blank name throws IllegalArgumentException`() {
        assertThrows<IllegalArgumentException> {
            ElectronicItem(1, "  ", "Electronics", 0, 100.0, null, null, null, 0)
        }
    }

    // ── Kotlin Collections demonstration ─────────────────────────────────────

    @Test fun `kotlin collections - filter low stock items`() {
        val items: List<InventoryItem> = listOf(
            makeElectronic(0, 5),
            makeElectronic(3, 5),
            makeElectronic(10, 5)
        )
        val lowStock = items.filter { it.getStockStatus() == StockStatus.LOW_STOCK }
        assertEquals(1, lowStock.size)
    }

    @Test fun `kotlin collections - sumOf calculates total value`() {
        val items: List<InventoryItem> = listOf(
            makeElectronic(10, 5),
            makeOfficeSupply(5, 2)
        )
        // 10*1000 + 5*10 = 10050
        assertEquals(10_050.0, items.sumOf { it.calculateTotalValue() })
    }

    @Test fun `kotlin collections - flatMap extracts unique features`() {
        val item1 = ElectronicItem(1, "A", "Electronics", 5, 100.0, null, null, null, 2,
            features = setOf("USB", "HDMI"))
        val item2 = ElectronicItem(2, "B", "Electronics", 5, 100.0, null, null, null, 2,
            features = setOf("USB", "Wireless"))
        val items: List<InventoryItem> = listOf(item1, item2)

        val unique: Set<String> = items.flatMap { it.features }.toSet()
        assertEquals(setOf("USB", "HDMI", "Wireless"), unique)
    }

    @Test fun `kotlin collections - associateBy creates lookup map`() {
        val items: List<InventoryItem> = listOf(makeElectronic(5, 2), makeOfficeSupply(10, 3))
        val lookup = items.associateBy { it.id }
        assertNotNull(lookup[1])
        assertNotNull(lookup[2])
    }

    @Test fun `polymorphism - calculateInventoryScore called on mixed list`() {
        val items: List<InventoryItem> = listOf(
            makeElectronic(10, 5, warrantyYears = 3, critical = true),
            makeOfficeSupply(20, 5, pastRestocks = 4)
        )
        // Different subclass overrides are called transparently
        val scores = items.map { it.calculateInventoryScore() }
        assertTrue(scores.all { it in 0..100 })
    }
}
