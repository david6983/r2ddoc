package fr.isen.m1.cyber.r2ddoc.parser.domain

import java.io.Serializable
import java.util.*

data class Parsed2DDoc(
    val header: Header2DDocC40,
    val data: ArrayList<Data2DDoc>,
    val signature: String,
    val rawHeader: String,
    val rawData: String,
    val rawSignature: String
) : Serializable