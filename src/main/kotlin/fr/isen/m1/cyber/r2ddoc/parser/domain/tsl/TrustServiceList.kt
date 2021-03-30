package fr.isen.m1.cyber.r2ddoc.parser.domain.tsl

data class TrustServiceList(
    var tspList: ArrayList<TrustServiceProvider>,
    var caCertificate: String,
) {
    fun findProviderById(id: String): TrustServiceProvider? {
        return tspList.find { it.name == id }
    }
}