package fr.isen.m1.cyber.r2ddoc.parser.domain

import java.io.Serializable

data class Header2DDocC40(
    val identificationMarker: String,
    val version: String,
    val authorityCertificationId: String,
    val certificateId: String,
    val emissionDocumentDate: String,
    val signatureCreationDate: String,
    val documentTypeId: String,
    val perimeterId: String?, // V04
    val emitterCountry: String?, // V04

) : Serializable