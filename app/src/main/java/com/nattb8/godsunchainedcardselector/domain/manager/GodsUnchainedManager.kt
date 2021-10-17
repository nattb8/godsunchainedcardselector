package com.nattb8.godsunchainedcardselector.domain.manager

import androidx.annotation.VisibleForTesting
import com.nattb8.godsunchainedcardselector.data.model.NetworkResponse
import com.nattb8.godsunchainedcardselector.data.model.Record
import com.nattb8.godsunchainedcardselector.data.repository.GodsUnchainedRepository
import com.nattb8.godsunchainedcardselector.domain.model.Card
import com.nattb8.godsunchainedcardselector.domain.model.CosmeticQuality
import javax.inject.Inject
import javax.inject.Singleton

@VisibleForTesting
internal const val CARD_IMAGE_URL = "https://card.godsunchained.com/?id=%d&q=%d"

@Singleton
class GodsUnchainedManager @Inject constructor(private val repository: GodsUnchainedRepository) {

    suspend fun getCards(): NetworkResponse<List<Card>> =
        when (val response = repository.getProtos()) {
            // Convert API proto record model to card model
            is NetworkResponse.Success -> NetworkResponse.Success(response.value.records.toCards())
            is NetworkResponse.Error -> NetworkResponse.Error(response.throwable)
        }

    /**
     * Converts the list of records to cards
     */
    private fun List<Record>.toCards() = map {
        Card(
            id = it.id,
            name = it.name,
            effect = it.effect,
            plainImageUrl = String.format(CARD_IMAGE_URL, it.id, CosmeticQuality.PLAIN.id),
            shadowImageUrl = String.format(
                CARD_IMAGE_URL,
                it.id,
                CosmeticQuality.SHADOW.id
            )
        )
    }

    /**
     * Filters the given list of [cards] by name and effect based on the given [query] (ignoring the case).
     */
    fun search(cards: List<Card>, query: String) =
        cards.filter {
            it.name.contains(query, ignoreCase = true) || it.effect.contains(
                query,
                ignoreCase = true
            )
        }
}