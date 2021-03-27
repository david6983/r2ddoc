import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.encoding.decodeQRCode
import fr.isen.m1.cyber.r2ddoc.parser.Parser
import fr.isen.m1.cyber.r2ddoc.parser.domain.Parsed2DDoc
import fr.isen.m1.cyber.r2ddoc.validation.listCrl
import fr.isen.m1.cyber.r2ddoc.validation.verify2dDoc
import fr.isen.m1.cyber.r2ddoc.validation.verifyCertificate
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import org.bouncycastle.asn1.DERIA5String

import java.util.ArrayList

import org.bouncycastle.asn1.ASN1Primitive

import java.io.ByteArrayInputStream

import org.bouncycastle.asn1.ASN1InputStream

import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.isismtt.ocsp.RequestedCertificate

import org.bouncycastle.asn1.isismtt.ocsp.RequestedCertificate.certificate
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.asn1.x509.Extension

import java.net.MalformedURLException
import java.security.cert.*


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

const val FR03_CA_CERTIFICATE = """
-----BEGIN CERTIFICATE-----
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
-----END CERTIFICATE-----
"""

//extract from XML ?
const val FR03_URL = "http://certificates.certigna.fr/search.php?name="

class CliMain : CliktCommand() {
    private val image: String by argument(help="Path to the 2d-doc qr code (png format)")
    private val terminal = Terminal()
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
                                var isRevoked = true
                                getCrlFromUrl(cert).forEach { crl ->
                                    isRevoked = crl.isRevoked(cert)
                                }
                                println("is revoked: $isRevoked")

                                val caCert = parser.parseX509Certificate(FR03_CA_CERTIFICATE.toByteArray())

                                val isCertValid = verifyCertificate(cert, caCert.publicKey)
                                if (isCertValid) {
                                    val isValid = verify2dDoc(cert, result)
                                    println("is valid: $isValid")
                                } else {
                                    println("cannot verify qr code because the certificate of the participant is not valid !")
                                }
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

    private fun getCrlFromUrl(cert: X509Certificate): ArrayList<CRL> {
        val cf = CertificateFactory.getInstance("X.509")
        val crlList = arrayListOf<CRL>()
        listCrl(cert).forEach { crlUrl ->
            performGetHttpRequest(crlUrl).use { crlRes ->
                when (crlRes.code) {
                    200 -> {
                        crlList.add(cf.generateCRL(crlRes.body!!.byteStream()))
                    }
                }
            }
        }
        return crlList
    }

    // working for FR03 only so far
    private fun performGetHttpRequest(url: String): Response {
        val client = OkHttpClient() //.Builder()
            //.certificatePinner(
            //    CertificatePinner.Builder()
            //        .add("certificates.certigna.fr", "sha256/$FR03_CA_CERTIFICATE")
            //        .build())
            //.build()
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