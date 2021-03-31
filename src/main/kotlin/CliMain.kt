import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.rendering.OverflowWrap
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.encoding.decodeQRCode
import fr.isen.m1.cyber.r2ddoc.parser.Parser
import fr.isen.m1.cyber.r2ddoc.parser.domain.Parsed2DDoc
import fr.isen.m1.cyber.r2ddoc.parser.domain.tsl.TrustServiceList
import fr.isen.m1.cyber.r2ddoc.parser.isXmlValid
import fr.isen.m1.cyber.r2ddoc.parser.parseXml
import fr.isen.m1.cyber.r2ddoc.parser.stringXmlToDocument
import fr.isen.m1.cyber.r2ddoc.validation.listCrl
import fr.isen.m1.cyber.r2ddoc.validation.verify2dDoc
import fr.isen.m1.cyber.r2ddoc.validation.verifyCertificate
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.ArrayList
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

//extract from XML ?
const val TSL_URL = "https://ants.gouv.fr/content/download/517/5670/version/19/file/ANTS_2D-DOc_TSL_230713_v3_signed.xml"

class CliMain : CliktCommand() {
    private val image: String by argument(help="Path to the 2d-doc qr code (png format)")
    private val terminal = Terminal()
    private val parser = Parser()
    private var tslData: TrustServiceList? = null

    override fun run() {
        performGetHttpRequest(TSL_URL).use { res ->
            val tslDoc = stringXmlToDocument(res.body!!.string())
            if (tslDoc != null) {
                val tslXml = parseXml(tslDoc)
                val tslCaCert = parser.parseX509Certificate(tslXml.caCertificate.toByteArray())
                var isTslCaRevoked = true
                getCrlFromUrl(tslCaCert).forEach { crl ->
                    isTslCaRevoked = crl.isRevoked(tslCaCert)
                }
                if (!isTslCaRevoked) {
                    val isXmlValid = isXmlValid(tslDoc, tslCaCert.publicKey)
                    if (isXmlValid) {
                        tslData = tslXml
                    } else {
                        terminal.println(TextColors.red("The TSL signature is not valid !"))
                    }
                } else {
                    terminal.println(TextColors.red("The certificate for the tsl.xml is not valid anymore !"))
                }
            } else {
                terminal.println(TextColors.red("Cannot parse TSL"))
            }
        }
        if (tslData != null) {
            try {
                decodeQRCode(image)?.let {
                    parser.parse(it)?.let { result ->
                        when (result.header.authorityCertificationId) {
                            "FR00" -> {
                                val cert = parser.parseX509Certificate(FR00_CERTIFICATE.toByteArray())
                                val isValid = verify2dDoc(cert, result)
                                if (isValid) {
                                    terminal.println(TextColors.yellow("The qr code is valid but this is a Testing code only !"))
                                    display(terminal, result)
                                } else {
                                    terminal.println(TextColors.red("The testing qr code is not valid !"))
                                }
                            }
                            else -> {
                                val participant= tslData!!.findProviderById(result.header.authorityCertificationId)
                                if (participant != null) {
                                    val trimmedUri = participant.uri.split("?")[0]
                                    performGetHttpRequest("$trimmedUri?name=${result.header.certificateId}").use { res ->
                                        when (res.code) {
                                            204 -> terminal.println(TextColors.red(("The participant \"${result.header.certificateId}\" was not found (204 No content): Unable to verify !")))
                                            200 -> {
                                                val cert = parser.parseX509Certificate(res.body!!.bytes())
                                                var isRevoked = true
                                                getCrlFromUrl(cert).forEach { crl ->
                                                    isRevoked = crl.isRevoked(cert)
                                                }
                                                if (isRevoked) {
                                                    terminal.println(TextColors.red("The certificate of the participant is revoked !"))
                                                } else {
                                                    terminal.println(TextColors.green("The certificate of the participant is not revoked."))
                                                    val caCert = parser.parseX509Certificate(participant.certificate.toByteArray())
                                                    val isCertValid = verifyCertificate(cert, caCert.publicKey)
                                                    if (isCertValid) {
                                                        val isValid = verify2dDoc(cert, result)
                                                        if (isValid) {
                                                            terminal.println(TextColors.green("The qr code is valid !"))
                                                            display(terminal, result)
                                                        } else {
                                                            terminal.println(TextColors.red("The qr code is not valid !"))
                                                        }
                                                    } else {
                                                        terminal.println(TextColors.red("cannot verify qr code because the certificate of the participant is not valid !"))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    terminal.println(TextColors.red("Cannot find the the participant in the trusted list !"))
                                }
                            }
                        }
                    } ?:run {
                        terminal.println(TextColors.red("The given qr code is not supported ! (version 03 not handled)"))
                    }
                }

            } catch (e: IOException) {
                terminal.println(TextColors.red("The input file is not valid !"))
            } catch (e: NotFoundException) {
                terminal.println(TextColors.red("2ddoc code not found !"))
            }
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

    private fun performGetHttpRequest(url: String): Response {
        val client = OkHttpClient()
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