package fr.isen.m1.cyber.r2ddoc

data class Header2DDocC40(
    val identificationMarker: String,
    val version: String,
    val authorityCertificationId: String,
    val certificateId: String,
    val emissionDocumentDate: String, //TODO change type to date or create getter
    val signatureCreationDate: String, //TODO change type to date or create getter
    val documentTypeId: String, // Périmètre C40 ‘01’
) {

}