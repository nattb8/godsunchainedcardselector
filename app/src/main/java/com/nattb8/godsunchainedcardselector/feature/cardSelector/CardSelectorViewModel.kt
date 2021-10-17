package com.nattb8.godsunchainedcardselector.feature.cardSelector

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nattb8.godsunchainedcardselector.data.model.NetworkResponse
import com.nattb8.godsunchainedcardselector.domain.manager.GodsUnchainedManager
import com.nattb8.godsunchainedcardselector.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardSelectorViewState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val selectedCardImageUrl: String? = null,
    val selectedCardName: String? = null,
    val cards: List<Card> = emptyList(),
)

sealed class CardSelectorViewEvent {
    data class Searched(val query: String) : CardSelectorViewEvent()
    data class CardTapped(val card: Card) : CardSelectorViewEvent()
    object TryAgainTapped : CardSelectorViewEvent()
}

@HiltViewModel
class CardSelectorViewModel @Inject constructor(private val manager: GodsUnchainedManager) :
    ViewModel() {
    private val _state = MutableStateFlow(CardSelectorViewState())
    val state: StateFlow<CardSelectorViewState> = _state

    @VisibleForTesting
    internal var cardsList: List<Card> = emptyList() // For storing all cards

    init {
        getCards()
    }

    private fun getCards() {
        // Get the list of cards on load
        _state.value = state.value.copy(isLoading = true, isError = false)
        viewModelScope.launch {
            _state.value = when (val response = manager.getCards()) {
                is NetworkResponse.Success -> {
                    // Saving list of cards so if user clears the search field,
                    // we can show the full list of cards again
                    cardsList = response.value
                    state.value.copy(
                        isLoading = false,
                        cards = response.value
                    )
                }
                is NetworkResponse.Error -> state.value.copy(isLoading = false, isError = true)
            }

        }
    }

    fun process(event: CardSelectorViewEvent) {
        when (event) {
            is CardSelectorViewEvent.CardTapped -> _state.value = state.value.copy(
                selectedCardImageUrl = event.card.shadowImageUrl,
                selectedCardName = event.card.name
            )
            is CardSelectorViewEvent.Searched -> _state.value = if (event.query.isEmpty()) {
                // User cleared the search field, show all cards
                state.value.copy(cards = cardsList)
            } else state.value.copy(cards = manager.search(cardsList, event.query))
            CardSelectorViewEvent.TryAgainTapped -> getCards()
        }
    }
}