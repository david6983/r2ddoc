package fr.isen.m1.cyber.r2ddoc.parser.domain

data class TrustServiceProvider(
    val name: String,
    val uri: String
)

data class TslData(
    var tspList: ArrayList<TrustServiceProvider>,
    var signature: String,
    var caCertificate: String,
)