package com.example.fareplansa

import org.junit.Assert.*
import org.junit.Test

class BudgetAlertHelperTest {

    private fun tripWith(total: Double, remaining: Double) = Trip(
        totalBudget = total,
        remainingBudget = remaining
    )

    @Test
    fun spentPercent_zeroSpent_returnsZero() {
        val trip = tripWith(1000.0, 1000.0)
        assertEquals(0, BudgetAlertHelper.spentPercent(trip))
    }

    @Test
    fun spentPercent_halfSpent_returns50() {
        val trip = tripWith(1000.0, 500.0)
        assertEquals(50, BudgetAlertHelper.spentPercent(trip))
    }

    @Test
    fun spentPercent_fullySpent_returns100() {
        val trip = tripWith(1000.0, 0.0)
        assertEquals(100, BudgetAlertHelper.spentPercent(trip))
    }

    @Test
    fun spentPercent_overSpent_capsAt100() {
        val trip = tripWith(1000.0, -200.0)
        assertEquals(100, BudgetAlertHelper.spentPercent(trip))
    }

    @Test
    fun progressColor_under70_returnsGreen() {
        val trip = tripWith(1000.0, 400.0) // 60% spent
        assertEquals(0xFF2ECC71.toInt(), BudgetAlertHelper.progressColor(trip))
    }

    @Test
    fun progressColor_70to89_returnsAmber() {
        val trip = tripWith(1000.0, 200.0) // 80% spent
        assertEquals(0xFFF39C12.toInt(), BudgetAlertHelper.progressColor(trip))
    }

    @Test
    fun progressColor_90plus_returnsRed() {
        val trip = tripWith(1000.0, 50.0) // 95% spent
        assertEquals(0xFFE74C3C.toInt(), BudgetAlertHelper.progressColor(trip))
    }
}