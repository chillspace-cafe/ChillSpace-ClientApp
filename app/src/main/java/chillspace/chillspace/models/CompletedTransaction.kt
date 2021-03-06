package chillspace.chillspace.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CompletedTransaction(var uid : String? = null,
                                val startTime : Long? = null,
                                val elapsedTimeInMinutes : Int? = null,
                                val cost : Int? = null)