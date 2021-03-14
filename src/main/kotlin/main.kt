import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.LuminanceSource
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import fr.isen.m1.cyber.r2ddoc.Version2DDoc
import org.apache.commons.codec.binary.Base32
import java.io.File
import java.io.IOException
import kotlin.math.sign

@Throws(IOException::class, NotFoundException::class)
fun decodeQRCode(qrCodeImage: String): String? {
    val file = File(qrCodeImage)
    val bufferedImage: BufferedImage = ImageIO.read(file)
    val source: LuminanceSource = BufferedImageLuminanceSource(bufferedImage)
    val bitmap = BinaryBitmap(HybridBinarizer(source))
    val result: Result? = MultiFormatReader().decode(bitmap)
    return result?.text
}

//TMP function
fun parseHeader(header: String, version: String) {
    val versionSupported = Version2DDoc.isSupportedVersion(version)
    if (header.length == versionSupported?.headerLength) {
        val identificationMarker: String = header.take(2)
        val authorityCertificationId: String = header.substring(4, 8)
        val certificateId: String = header.substring(8, 12)
        val emissionDocumentDate: String = header.substring(12, 16)
        val signatureCreationDate: String = header.substring(16, 20)
        val documentTypeId: String = header.substring(20, 22)
        println("parsed header: $identificationMarker$version$authorityCertificationId$certificateId$emissionDocumentDate$signatureCreationDate$documentTypeId")
    } else {
      println("the header length is not correct")
    }
}

fun main(args: Array<String>) {
    try {
        val result = decodeQRCode("./samples/test2.png")
        result?.substring(2, 4)?.let { version ->
            Version2DDoc.isSupportedVersion(version)?.let { version2dDoc ->
                val header = result.take(version2dDoc.headerLength)
                println("message entier: $result")
                println("version: $version2dDoc")
                println("header: $header")
                parseHeader(header, version)
                val rest = result.drop(version2dDoc.headerLength).split(31.toChar()) // ASCII 31 - <US> separator
                val data = rest[0]
                println("data: $data")
                val signature = when(rest.size) {
                    2 -> rest[1]
                    else -> ""
                }
                if (signature != "") {
                    val decodedSignature = Base32().decode(signature)
                    val hexSignature = decodedSignature.joinToString("") { String.format("%02x", it) }
                    println("hex signature: $hexSignature")
                }
            }
        }
    } catch (e: IOException) {
        println(e)
    } catch (e: NotFoundException) {
        println("not found: $e")
    }
}