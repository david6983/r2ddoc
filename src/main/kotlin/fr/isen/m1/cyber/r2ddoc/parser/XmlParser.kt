package fr.isen.m1.cyber.r2ddoc.parser

import fr.isen.m1.cyber.r2ddoc.parser.domain.tsl.TrustServiceList
import fr.isen.m1.cyber.r2ddoc.parser.domain.tsl.TrustServiceProvider
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.lang.Exception
import javax.xml.crypto.dsig.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.crypto.dsig.dom.DOMValidateContext
import javax.xml.crypto.dsig.XMLSignatureFactory
import javax.xml.xpath.XPathConstants
import org.w3c.dom.Element
import java.security.PublicKey
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathFactory

fun stringXmlToDocument(xml: String): Document? {
    try {
        val dbf = DocumentBuilderFactory.newInstance()
        dbf.isNamespaceAware = true
        val db = dbf.newDocumentBuilder()
        return db.parse(InputSource(StringReader(xml)))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun isXmlValid(xmlDoc: Document, pubKey: PublicKey): Boolean {
    val xpf: XPathFactory = XPathFactory.newInstance()
    val xp: XPath = xpf.newXPath()
    val signature: Element = xmlDoc.getElementsByTagName("ds:Signature").item(0) as Element
    val ctx = DOMValidateContext(pubKey, signature)
    val idAttributes: NodeList = xp.evaluate("//*[@Id]", xmlDoc, XPathConstants.NODESET) as NodeList
    for (i in 0 until idAttributes.length) {
        ctx.setIdAttributeNS(idAttributes.item(i) as Element, null, "Id")
    }
    val sigF = XMLSignatureFactory.getInstance("DOM")
    val xmlSignature: XMLSignature = sigF.unmarshalXMLSignature(ctx)
    return xmlSignature.validate(ctx)
}

fun parseXml(xmlDoc: Document): TrustServiceList {
    val parsedData = TrustServiceList(arrayListOf(), "")
    val uris: NodeList = xmlDoc.getElementsByTagName("tsl:TSPInformationURI")
    val ids: NodeList = xmlDoc.getElementsByTagName("tsl:TSPTradeName")
    for (i in 0 until uris.length) {
        parsedData.tspList.add(
            TrustServiceProvider(
            ids.item(i).childNodes.item(1).textContent,
            uris.item(i).childNodes.item(1).textContent
        )
        )
    }
    return parsedData
}