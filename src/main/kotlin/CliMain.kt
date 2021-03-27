import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.parser.Parser
import fr.isen.m1.cyber.r2ddoc.parser.domain.Parsed2DDoc
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

const val FR00_CERTIFICATE = """
-----BEGIN CERTIFICATE-----
MIICVzCCAT8CCQCpMEvcR9M4RTANBgkqhkiG9w0BAQUFADBPMQswCQYDVQQGEwJG 
UjETMBEGA1UECgwKQUMgREUgVEVTVDEcMBoGA1UECwwTMDAwMiAwMDAwMDAwMDAw 
MDAwMDENMAsGA1UEAwwERlIwMDAeFw0xMjExMDExMzQ3NDZaFw0xNTExMDExMzQ3 
NDZaMFcxCzAJBgNVBAYTAkZSMRswGQYDVQQKDBJDRVJUSUZJQ0FUIERFIFRFU1Qx 
HDAaBgNVBAsMEzAwMDIgMDAwMDAwMDAwMDAwMDAxDTALBgNVBAMMBDAwMDEwWTAT 
BgcqhkjOPQIBBggqhkjOPQMBBwNCAASpjw18zWKAiJO+xNQ2550YNKHW4AHXDxxM 
3M2dni/iKfckBRTo3cDKmNDHRAycxJKEmg+9pz/DkvTaCuB/hMI8MA0GCSqGSIb3 
DQEBBQUAA4IBAQA6HN+w/bzIdg0ZQF+ELrocplehP7r5JuRJNBAgmoqoER7IonCv 
KSNUgUVbJ/MB4UKQ6CgzK7AOlCpiViAnBv+i6fg8Dh9evoUcHBiDvbl19+4iREaO 
oyVZ8RAlkp7VJKrC3s6dJEmI8/19obLbTvdHfY+TZfduqpVl63RSxwLG0Fjl0SAQ 
z9a+KJSKZnEvT9I0iUUgCSnqFt77RSppziQTZ+rkWcfd+BSorWr8BHqOkLtj7EiV 
amIh+g3A8JtwV7nm+NUbBlhh2UPSI0eevsRjQRghtTiEn0wflVBX7xFP9zXpViHq 
Ij+R9WiXzWGFYyKuAFK1pQ2QH8BxCbvdNdff
-----END CERTIFICATE-----
"""

//extract from XML ?
const val FR03_URL = "http://certificates.certigna.fr/search.php?name="

class CliMain : CliktCommand() {
    private val image: String by argument(help="Path to the 2d-doc qr code (png format)")
    private val terminal = Terminal()
    private val client = OkHttpClient()
    private val parser = Parser()

    override fun run() {
        try {
            decodeQRCode(image)?.let {
                parser.parse(it)?.let { result ->
                   display(terminal, result)
                    performGetHttpRequest(FR03_URL + result.header.certificateId).use { res ->
                        when (res.code) {
                            204 -> println("The certificate ${result.header.certificateId} was not found (204 No content): Unable to verify !")
                            200 -> {
                                val cert = parser.parseX509Certificate(res.body!!.bytes())
                                val isValid = verify2dDoc(cert, result)
                                println("is valid: $isValid")
                            }
                        }
                    }
                } ?:run {
                    println("The given qr code is not supported ! (version 03 not handled)")
                }

            }

        } catch (e: IOException) {
            println(e)
        } catch (e: NotFoundException) {
            println("not found: $e")
        }
    }

    // working for FR03 only so far
    private fun performGetHttpRequest(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .build()

        return client.newCall(request).execute()
    }

    private fun display(terminal: Terminal, parsedCode: Parsed2DDoc) {
        terminal.println("Header")
        terminal.println(table {
            header { row("Label", "Value") }
            body {
                row("marqueur d'identification", parsedCode.header.identificationMarker)
                row("version", parsedCode.header.version)
                row("id de l'authorité de certification", parsedCode.header.authorityCertificationId)
                row("id du certificat", parsedCode.header.certificateId)
                row("date d'emission du document", parsedCode.header.emissionDocumentDate.toString())
                row("date de création de la signature", parsedCode.header.signatureCreationDate.toString())
                row("id du type de document", parsedCode.header.documentTypeId)
                if (parsedCode.header.perimeterId != null) {
                    row("id du perimetre", parsedCode.header.perimeterId)
                }
                if (parsedCode.header.emitterCountry != null) {
                    row("pays d'emission", parsedCode.header.emitterCountry)
                }
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