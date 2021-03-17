package fr.isen.m1.cyber.r2ddoc.parser

data class Header2DDocC40(
    val identificationMarker: String,
    val version: String,
    val authorityCertificationId: String,
    val certificateId: String,
    val emissionDocumentDate: String, //TODO change type to date or create getter
    val signatureCreationDate: String, //TODO change type to date or create getter
    val documentTypeId: String, // Périmètre C40 ‘01’
) {
    fun display() {
        println("Header: ")
        println("identification marker: $identificationMarker")
        println("version: $version")
        println("authority certification id: $authorityCertificationId")
        println("certificate id: $certificateId")
        println("emission document date: $emissionDocumentDate")
        println("signature creationDate: $signatureCreationDate")
        println("document type id: $documentTypeId")
    }
}