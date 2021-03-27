import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.common.HybridBinarizer
import fr.isen.m1.cyber.r2ddoc.parser.domain.Parsed2DDoc
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.DERSequenceGenerator
import org.bouncycastle.util.encoders.Hex
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.Signature
import java.security.cert.X509Certificate
import javax.imageio.ImageIO

@Throws(IOException::class, NotFoundException::class)
fun decodeQRCode(qrCodeImage: String): String? {
    val file = File(qrCodeImage)
    val bufferedImage: BufferedImage = ImageIO.read(file)
    val source: LuminanceSource = BufferedImageLuminanceSource(bufferedImage)
    val bitmap = BinaryBitmap(HybridBinarizer(source))
    val result: Result? = MultiFormatReader().decode(bitmap)
    return result?.text
}

fun encodeSignatureToDerAsn1(signature: ByteArray): ByteArray? {
    val len = signature.size / 2
    val arraySize = len + 1
    val r = ByteArray(arraySize)
    val s = ByteArray(arraySize)
    System.arraycopy(signature, 0, r, 1, len)
    System.arraycopy(signature, len, s, 1, len)
    val rBigInteger = BigInteger(r)
    val sBigInteger = BigInteger(s)
    val bos = ByteArrayOutputStream()
    try {
        val seqGen = DERSequenceGenerator(bos)
        seqGen.addObject(ASN1Integer(rBigInteger.toByteArray()))
        seqGen.addObject(ASN1Integer(sBigInteger.toByteArray()))
        seqGen.close()
        bos.close()
    } catch (e: IOException) {
        throw RuntimeException("Failed to generate ASN.1 DER signature", e)
    }
    return bos.toByteArray()
}

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