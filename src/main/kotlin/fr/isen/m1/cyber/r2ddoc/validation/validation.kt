package fr.isen.m1.cyber.r2ddoc.validation

import fr.isen.m1.cyber.r2ddoc.encoding.encodeSignatureToDerAsn1
import fr.isen.m1.cyber.r2ddoc.parser.domain.Parsed2DDoc
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.DERIA5String
import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.util.encoders.Hex
import java.io.ByteArrayInputStream
import java.security.*
import java.security.cert.CRL
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

fun verify2dDoc(cert: X509Certificate, parsed2DDoc: Parsed2DDoc): Boolean {
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

fun verifyCertificate(cert: X509Certificate, caPublicKey: PublicKey): Boolean {
    try {
        cert.verify(caPublicKey)
        return true
    } catch(e: Exception) {
        when(e) {
            is CertificateException -> {
                println("The certificate is invalid: $e")
            }
            is NoSuchAlgorithmException -> {
                println("The certificate is invalid: the algorithm doesn't exist")
            }
            is InvalidKeyException -> {
                println("The certificate is invalid: the key is not supported")
            }
            is NoSuchProviderException -> {
                println("The certificate is invalid: The provider doesn't exist")
            }
            is SignatureException -> {
                println("The certificate of the participant is invalid: The key is not supported")
            }
        }
    }
    return false
}

fun listCrl(certificate: X509Certificate): List<String> {
    val crlDistributionPointDerEncodedArray: ByteArray =
        certificate.getExtensionValue(Extension.cRLDistributionPoints.id)

    val oAsnInStream = ASN1InputStream(ByteArrayInputStream(crlDistributionPointDerEncodedArray))
    val derObjCrlDP = oAsnInStream.readObject()
    val dosCrlDP = derObjCrlDP as DEROctetString

    oAsnInStream.close()

    val crldpExtOctets = dosCrlDP.octets
    val oAsnInStream2 = ASN1InputStream(ByteArrayInputStream(crldpExtOctets))
    val derObj2 = oAsnInStream2.readObject()
    val distPoint = CRLDistPoint.getInstance(derObj2)

    oAsnInStream2.close()

    val crlUrls: MutableList<String> = java.util.ArrayList()
    for (dp in distPoint.distributionPoints) {
        val dpn: DistributionPointName = dp.distributionPoint
        // Look for URIs in fullName
        if (dpn.type == DistributionPointName.FULL_NAME) {
            val genNames: Array<GeneralName> = GeneralNames.getInstance(dpn.name).names
            // Look for an URI
            for (j in genNames.indices) {
                if (genNames[j].tagNo == GeneralName.uniformResourceIdentifier) {
                    val url = DERIA5String.getInstance(genNames[j].name).string
                    crlUrls.add(url)
                }
            }
        }
    }

    return crlUrls
}
