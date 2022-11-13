package com.anubhav.yatak.model

import java.io.Serializable

data class Note(
    val id: String = "",
    val noteTitle: String = "",
    val noteBody: String?  = "",
    val noteIV:String = ""
) : Serializable