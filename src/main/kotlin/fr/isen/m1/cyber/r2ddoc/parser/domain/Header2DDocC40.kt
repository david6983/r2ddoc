package fr.isen.m1.cyber.r2ddoc.parser.domain

data class Header2DDocC40(
    val identificationMarker: String,
    val version: String,
    val authorityCertificationId: String,
    val certificateId: String,
    val emissionDocumentDate: String,
    val signatureCreationDate: String,
    val documentTypeId: String // Périmètre C40 ‘01’
) {
}