package com.nattb8.godsunchainedcardselector.domain.manager

import com.nattb8.godsunchainedcardselector.*
import com.nattb8.godsunchainedcardselector.data.model.NetworkResponse
import com.nattb8.godsunchainedcardselector.data.model.ProtosResponse
import com.nattb8.godsunchainedcardselector.data.model.Record
import com.nattb8.godsunchainedcardselector.data.repository.GodsUnchainedRepository
import com.nattb8.godsunchainedcardselector.domain.model.Card
import com.nattb8.godsunchainedcardselector.domain.model.CosmeticQuality
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.spyk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GodsUnchainedManagerTest {

    @MockK
    lateinit var repository: GodsUnchainedRepository

    private val record1 = Record(RECORD_1_ID, RECORD_1_NAME, RECORD_1_EFFECT)
    private val record2 = Record(RECORD_2_ID, RECORD_2_NAME, RECORD_2_EFFECT)
    private val record3 = Record(RECORD_3_ID, RECORD_3_NAME, RECORD_3_EFFECT)
    private val card1 = Card(RECORD_1_ID, RECORD_1_NAME, RECORD_1_EFFECT, "", "")
    private val card2 = Card(RECORD_2_ID, RECORD_2_NAME, RECORD_2_EFFECT, "", "")
    private val card3 = Card(RECORD_3_ID, RECORD_3_NAME, RECORD_3_EFFECT, "", "")

    private lateinit var manager: GodsUnchainedManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        manager = spyk(GodsUnchainedManager(repository))
    }

    @Test
    fun testManager_getCards_success() {
        coEvery { repository.getProtos() } returns NetworkResponse.Success(
            ProtosResponse(arrayListOf(record1, record2, record3))
        )

        runBlocking {
            val response = manager.getCards()

            assert(response is NetworkResponse.Success)
            coVerify { repository.getProtos() }

            val cards = (response as NetworkResponse.Success).value
            cards[0].apply {
                assertEquals(RECORD_1_ID, id)
                assertEquals(RECORD_1_NAME, name)
                assertEquals(RECORD_1_EFFECT, effect)
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_1_ID, CosmeticQuality.PLAIN.id),
                    plainImageUrl
                )
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_1_ID, CosmeticQuality.SHADOW.id),
                    shadowImageUrl
                )
            }
            cards[1].apply {
                assertEquals(RECORD_2_ID, id)
                assertEquals(RECORD_2_NAME, name)
                assertEquals(RECORD_2_EFFECT, effect)
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_2_ID, CosmeticQuality.PLAIN.id),
                    plainImageUrl
                )
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_2_ID, CosmeticQuality.SHADOW.id),
                    shadowImageUrl
                )
            }
            cards[2].apply {
                assertEquals(RECORD_3_ID, id)
                assertEquals(RECORD_3_NAME, name)
                assertEquals(RECORD_3_EFFECT, effect)
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_3_ID, CosmeticQuality.PLAIN.id),
                    plainImageUrl
                )
                assertEquals(
                    String.format(CARD_IMAGE_URL, RECORD_3_ID, CosmeticQuality.SHADOW.id),
                    shadowImageUrl
                )
            }
        }
    }

    @Test
    fun testManager_getCards_error() {
        coEvery { repository.getProtos() } returns NetworkResponse.Error(RuntimeException())

        runBlocking {
            val response = manager.getCards()

            assert(response is NetworkResponse.Error)
            coVerify { repository.getProtos() }

            assert((response as NetworkResponse.Error).throwable is RuntimeException)
        }
    }

    @Test
    fun testManager_search() {
        val cards = listOf(card1, card2, card3)

        // Search name
        manager.search(cards, query = "armin").apply {
            assertEquals(1, size)
            assertEquals(RECORD_1_NAME, first().name)
        }

        // Search effect
        manager.search(cards, query = "nail").apply {
            assertEquals(1, size)
            assertEquals(RECORD_3_EFFECT, first().effect)
        }

        // Search name and effect
        manager.search(cards, query = "The").apply {
            assertEquals(3, size)
            assertTrue(find { it.name == RECORD_1_NAME } != null)
            assertTrue(find { it.name == RECORD_2_NAME } != null)
            assertTrue(find { it.name == RECORD_3_NAME } != null)
        }

        // Search something that does not exist
        manager.search(cards, query = "bingsu").apply {
            assertEquals(0, size)
        }
    }

}