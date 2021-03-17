package fr.isen.m1.cyber.r2ddoc.parser.domain

import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal

data class Header2DDocC40(
    val identificationMarker: String,
    val version: String,
    val authorityCertificationId: String,
    val certificateId: String,
    val emissionDocumentDate: String,
    val signatureCreationDate: String,
    val documentTypeId: String, // Périmètre C40 ‘01’
) {
    fun display(terminal: Terminal) {
        terminal.println("Header")
        terminal.println(table {
            header { row("Label", "Value") }
            body {
                row("identification marker", identificationMarker)
                row("version", version)
                row("authority certification id", authorityCertificationId)
                row("certificate id", certificateId)
                row("emission document date", emissionDocumentDate.toString())
                row("signature creationDate", signatureCreationDate.toString())
                row("document type id", documentTypeId)
            }
        })
    }
}