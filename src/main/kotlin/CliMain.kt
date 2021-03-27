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
import java.io.IOException
import org.bouncycastle.util.encoders.Hex
import java.security.Signature
import org.bouncycastle.asn1.ASN1Integer

import org.bouncycastle.asn1.DERSequenceGenerator

import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.cert.X509Certificate

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
                    val cert = downloadCertificate(FR03_URL + result.header.certificateId)
                    val isValid = verify2dDoc(cert, result)
                    println("is valid: $isValid")
                }

            }

        } catch (e: IOException) {
            println(e)
        } catch (e: NotFoundException) {
            println("not found: $e")
        }
    }

    // working for FR03 only so far
    private fun downloadCertificate(url: String): X509Certificate {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val body = response.body!!.bytes()
            return parser.parseX509Certificate(body)
        }
    }

    private fun verify2dDoc(cert: X509Certificate, parsed2DDoc: Parsed2DDoc): Boolean {
        val signatureAlgorithm = if (cert.publicKey.algorithm == "EC") {
            "SHA256withECDSA"
        } else {
            "SHA256withRSA"
        }
        val signature: Signature = Signature.getInstance(signatureAlgorithm)
        signature.initVerify(cert.publicKey)
        val payload = parsed2DDoc.rawHeader + parsed2DDoc.rawData
        signature.update(payload.toByteArray())
        val derSignature = encodeSignatureToDerAsn1(Hex.decode(parsed2DDoc.signature))
        return signature.verify(derSignature)
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