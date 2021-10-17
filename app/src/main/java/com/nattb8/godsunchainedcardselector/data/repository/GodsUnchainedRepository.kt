package com.nattb8.godsunchainedcardselector.data.repository

import com.nattb8.godsunchainedcardselector.data.api.GodsUnchainedApi
import com.nattb8.godsunchainedcardselector.data.model.apiCall
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GodsUnchainedRepository @Inject constructor(retrofit: Retrofit) {
    private val api: GodsUnchainedApi by lazy { retrofit.create(GodsUnchainedApi::class.java) }

    suspend fun getProtos() = apiCall { api.getProtos() }
}