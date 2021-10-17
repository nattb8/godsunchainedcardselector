package com.nattb8.godsunchainedcardselector.data.model

data class ProtosResponse(val records: List<Record>)

data class Record(
    val id: Long,
    val name: String,
    val effect: String
)