import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.LuminanceSource
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import fr.isen.m1.cyber.r2ddoc.parser.*
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

fun main(args: Array<String>) {
    try {
        decodeQRCode("./samples/edf.png")?.let {
            val parser = Parser()
            parser.parse(it)?.let { result ->
                result.display()
            }

        }

    } catch (e: IOException) {
        println(e)
    } catch (e: NotFoundException) {
        println("not found: $e")
    }
}