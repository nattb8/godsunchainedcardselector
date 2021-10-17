package com.nattb8.godsunchainedcardselector.feature.cardSelector

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nattb8.godsunchainedcardselector.*
import com.nattb8.godsunchainedcardselector.data.model.NetworkResponse
import com.nattb8.godsunchainedcardselector.domain.manager.GodsUnchainedManager
import com.nattb8.godsunchainedcardselector.domain.model.Card
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val SHADOW_IMAGE_URL = "Shadow url"

@ExperimentalCoroutinesApi
class CardSelectorViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val dispatcher = TestCoroutineDispatcher()

    @MockK
    lateinit var manager: GodsUnchainedManager

    private val card1 = Card(RECORD_1_ID, RECORD_1_NAME, RECORD_1_EFFECT, "", "")
    private val card2 = Card(RECORD_2_ID, RECORD_2_NAME, RECORD_2_EFFECT, "", "")
    private val card3 = Card(RECORD_3_ID, RECORD_3_NAME, RECORD_3_EFFECT, "", SHADOW_IMAGE_URL)

    private lateinit var viewModel: CardSelectorViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = spyk(CardSelectorViewModel(manager))
    }

    @Test
    fun testViewModel_getCardsOnLoad_success() {
        coEvery { manager.getCards() } returns NetworkResponse.Success(
            listOf(card1, card2, card3)
        )
        createViewModel()

        coVerify { manager.getCards() }
        assertEquals(3, viewModel.cardsList.size)
        assertTrue(viewModel.cardsList.contains(card1))
        assertTrue(viewModel.cardsList.contains(card2))
        assertTrue(viewModel.cardsList.contains(card3))
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(3, viewModel.state.value.cards.size)
        assertTrue(viewModel.state.value.cards.contains(card1))
        assertTrue(viewModel.state.value.cards.contains(card2))
        assertTrue(viewModel.state.value.cards.contains(card3))
        assertTrue(viewModel.state.value.cards == viewModel.cardsList)
    }

    @Test
    fun testViewModel_getCardsOnLoad_error() {
        coEvery { manager.getCards() } returns NetworkResponse.Error(IllegalStateException())
        createViewModel()

        coVerify { manager.getCards() }
        assertEquals(0, viewModel.cardsList.size)
        assertFalse(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.isError)
        assertEquals(0, viewModel.state.value.cards.size)
    }

    @Test
    fun testViewModel_process_cardTapped() {
        coEvery { manager.getCards() } returns NetworkResponse.Success(
            listOf(card1, card2, card3)
        )
        createViewModel()

        viewModel.process(CardSelectorViewEvent.CardTapped(card3))
        assertEquals(RECORD_3_NAME, viewModel.state.value.selectedCardName)
        assertEquals(SHADOW_IMAGE_URL, viewModel.state.value.selectedCardImageUrl)
    }

    @Test
    fun testViewModel_process_searched() {
        val fullList = listOf(card1, card2, card3)
        coEvery { manager.getCards() } returns NetworkResponse.Success(fullList)
        every { manager.search(any(), any()) } returns listOf(card1, card2)
        createViewModel()

        // Search
        viewModel.process(CardSelectorViewEvent.Searched("yea yea yea"))
        assertEquals(2, viewModel.state.value.cards.size)
        assertTrue(viewModel.state.value.cards.contains(card1))
        assertTrue(viewModel.state.value.cards.contains(card2))
        assertFalse(viewModel.state.value.cards.contains(card3))

        // Clear search
        viewModel.process(CardSelectorViewEvent.Searched(""))
        assertEquals(fullList.size, viewModel.state.value.cards.size)
        assertTrue(viewModel.state.value.cards.contains(card1))
        assertTrue(viewModel.state.value.cards.contains(card2))
        assertTrue(viewModel.state.value.cards.contains(card3))
    }

    @Test
    fun testViewModel_process_tryAgainTapped() {
        coEvery { manager.getCards() } returns NetworkResponse.Success(
            listOf(card1, card2, card3)
        )
        createViewModel()

        viewModel.process(CardSelectorViewEvent.TryAgainTapped)
        coVerify { manager.getCards() }
        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isError)
    }
}