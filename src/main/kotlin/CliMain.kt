import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.parser.Parser
import fr.isen.m1.cyber.r2ddoc.parser.enums.Parsed2DDoc
import java.io.IOException

class CliMain : CliktCommand() {
    private val image: String by argument(help="Path to the 2d-doc qr code (png format)")
    private val terminal = Terminal()

    override fun run() {
        try {
            decodeQRCode(image)?.let {
                val parser = Parser()
                parser.parse(it)?.let { result ->
                   display(terminal, result)
                }

            }

        } catch (e: IOException) {
            println(e)
        } catch (e: NotFoundException) {
            println("not found: $e")
        }
    }

    private fun display(terminal: Terminal, parsedCode: Parsed2DDoc) {
        terminal.println("Header")
        terminal.println(table {
            header { row("Label", "Value") }
            body {
                row("identification marker", parsedCode.header.identificationMarker)
                row("version", parsedCode.header.version)
                row("authority certification id", parsedCode.header.authorityCertificationId)
                row("certificate id", parsedCode.header.certificateId)
                row("emission document date", parsedCode.header.emissionDocumentDate.toString())
                row("signature creationDate", parsedCode.header.signatureCreationDate.toString())
                row("document type id", parsedCode.header.documentTypeId)
            }
        })
        terminal.println("Data")
        terminal.println(table {
            header { row("Label", "Value") }
            body {
                parsedCode.data.forEach { token ->
                    row {
                        cell(token.label)
                        cell(if (token.value == "") "vide" else token.value) { overflowWrap = OverflowWrap.BREAK_WORD }
                    }
                }
            }
        })
        terminal.println("Signature (hex)")
        terminal.println(table {
            body {
                row { cell(parsedCode.signature) { overflowWrap = OverflowWrap.BREAK_WORD } }
            }
        })
    }
}