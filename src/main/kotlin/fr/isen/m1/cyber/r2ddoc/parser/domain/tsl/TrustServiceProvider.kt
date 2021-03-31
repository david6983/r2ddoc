package fr.isen.m1.cyber.r2ddoc.parser.domain.tsl

data class TrustServiceProvider(
    val name: String,
    val uri: String,
    val certificate: String
)