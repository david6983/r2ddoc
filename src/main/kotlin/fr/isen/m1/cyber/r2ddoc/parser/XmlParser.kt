package fr.isen.m1.cyber.r2ddoc.parser

import fr.isen.m1.cyber.r2ddoc.parser.domain.TrustServiceProvider
import fr.isen.m1.cyber.r2ddoc.parser.domain.TslData
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.lang.Exception

import javax.xml.parsers.DocumentBuilderFactory

fun parseXml(xml: String): TslData {
    val parsedData = TslData(arrayListOf(), "", "")
    try {
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc = db.parse(InputSource(StringReader(xml)))
        val uris: NodeList = doc.getElementsByTagName("tsl:TSPInformationURI")
        val ids: NodeList = doc.getElementsByTagName("tsl:TSPTradeName")
        parsedData.caCertificate = doc.getElementsByTagName("ds:X509Certificate").item(0).textContent
        parsedData.signature = doc.getElementsByTagName("ds:SignatureValue").item(0).textContent
        for (i in 0 until uris.length) {
            parsedData.tspList.add(TrustServiceProvider(
                ids.item(i).childNodes.item(1).textContent,
                uris.item(i).childNodes.item(1).textContent
            ))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return parsedData
}