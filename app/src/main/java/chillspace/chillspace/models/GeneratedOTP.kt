package chillspace.chillspace.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GeneratedOTP(var uid : String? = null, var isRunning : Boolean? = null)