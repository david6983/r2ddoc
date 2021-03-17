package fr.isen.m1.cyber.r2ddoc.parser.enums

import fr.isen.m1.cyber.r2ddoc.parser.domain.Data2DDoc
import fr.isen.m1.cyber.r2ddoc.parser.domain.Header2DDocC40

data class Parsed2DDoc(
    val header: Header2DDocC40,
    val data: ArrayList<Data2DDoc>,
    val signature: String
) {
    fun display() {
        header.display()
        println("Data:")
        data.forEach { token ->
            token.display()
        }
        println("Signature (hex): $signature")
    }
}