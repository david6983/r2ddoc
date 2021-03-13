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
import java.io.File
import java.io.IOException

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
        val result = decodeQRCode("./samples/edf.png")
        result?.substring(2, 4)?.let { version ->
            Version2DDoc.isSupportedVersion(version)?.let {
                val header = result.take(it.headerLength)
                println("message entier: $result")
                println("version: $version")
                println("header: $header")
                parseHeader(header, version)
                println("reste: ${result.drop(it.headerLength)}")
                // zone message <US> signature en base32
            }
        }
    } catch (e: IOException) {
        println(e)
    } catch (e: NotFoundException) {
        println("not found: $e")
    }
}