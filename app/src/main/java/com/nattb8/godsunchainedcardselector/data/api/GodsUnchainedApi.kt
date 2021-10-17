package com.nattb8.godsunchainedcardselector.data.api

import com.nattb8.godsunchainedcardselector.data.model.ProtosResponse
import retrofit2.http.GET

interface GodsUnchainedApi {

    @GET("proto")
    suspend fun getProtos(): ProtosResponse

}