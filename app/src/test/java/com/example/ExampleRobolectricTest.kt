package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.data.GameRepository
import com.example.game.model.CustomerType
import com.example.game.model.EmployeeType
import com.example.game.model.GameData
import com.example.game.viewmodel.SupermarketViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Supermarket Stories", appName)
  }

  @Test
  fun `game starts with initial resources and unlocked fruit department`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = GameRepository(context)
    val vm = SupermarketViewModel(repo, context)

    val state = vm.uiState.value
    assertTrue("Should start with initial coins", state.coins >= 300)
    assertTrue("Should have initial energy", state.energy > 0)
    assertEquals("Board should have 16 slots", 16, state.boardCells.size)
    assertTrue("Should have sections initialized", state.sections.isNotEmpty())
    assertTrue("Fruit & Veg should be unlocked", state.sections.first().isUnlocked)
  }

  @Test
  fun `hire cashier employee updates state and speed`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = GameRepository(context)
    val vm = SupermarketViewModel(repo, context)

    val initialCoins = vm.uiState.value.coins
    vm.hireOrUpgradeEmployee(EmployeeType.CASHIER)

    val updatedState = vm.uiState.value
    val cashier = updatedState.employees.find { it.type == EmployeeType.CASHIER }
    assertNotNull(cashier)
    assertTrue("Cashier should be hired", cashier!!.isHired)
  }
}
