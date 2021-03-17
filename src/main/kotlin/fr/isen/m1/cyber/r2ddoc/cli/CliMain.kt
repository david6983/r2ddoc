package fr.isen.m1.cyber.r2ddoc.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.decoding.decodeQRCode
import fr.isen.m1.cyber.r2ddoc.parser.Parser
import java.io.IOException

class CliMain : CliktCommand() {
    private val image: String by argument(help="Path to the 2d-doc qr code (png format)")
    private val terminal = Terminal()

    override fun run() {
        try {
            decodeQRCode(image)?.let {
                val parser = Parser()
                println("Message: $it")
                parser.parse(it)?.let { result ->
                   result.display(terminal)
                }

            }

        } catch (e: IOException) {
            println(e)
        } catch (e: NotFoundException) {
            println("not found: $e")
        }
    }
}