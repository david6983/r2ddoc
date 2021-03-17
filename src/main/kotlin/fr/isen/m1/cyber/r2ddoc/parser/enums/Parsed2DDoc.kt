package fr.isen.m1.cyber.r2ddoc.parser.enums

import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import fr.isen.m1.cyber.r2ddoc.parser.domain.Data2DDoc
import fr.isen.m1.cyber.r2ddoc.parser.domain.Header2DDocC40

data class Parsed2DDoc(
    val header: Header2DDocC40,
    val data: ArrayList<Data2DDoc>,
    val signature: String
) {
    fun display(terminal: Terminal) {
        header.display(terminal)
        terminal.println("Data:")
        terminal.println(table {
            header { row("Label", "Value") }
            body {
                data.forEach { token ->
                    row(token.label, if (token.value == "") "vide" else token.value)
                }
            }
        })
        terminal.println("Signature (hex)")
        terminal.println(table {
            body {
                row(signature)
            }
        })
    }
}