import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.BinaryBitmap
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.LuminanceSource
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import com.google.zxing.NotFoundException
import com.google.zxing.Result
import fr.isen.m1.cyber.r2ddoc.Abbreviations
import fr.isen.m1.cyber.r2ddoc.DataValueIso20022
import fr.isen.m1.cyber.r2ddoc.Header2DDocC40
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
// https://github.com/ajalt/mordant
//TMP function
fun parseHeader(header: String, version: String) {
    val versionSupported = Version2DDoc.isSupportedVersion(version)
    if (header.length == versionSupported?.headerLength) {
        val header2Ddoc = Header2DDocC40(
            header.take(2),
            version,
            header.substring(4, 8),
            header.substring(8, 12),
            header.substring(12, 16),
            header.substring(16, 20),
            header.substring(20, 22)
        )
        println("parsed header: $header2Ddoc")
    } else {
      println("the header length is not correct")
    }
}

fun parseAbbreviation(value: String): String {
    return if (value.contains('/')) {
        var final = ""
        val split = value.split('/')
        split.forEach { token ->
            val abbr = Abbreviations.getAbbreviation(token)
            final += if (abbr != null) {
                "${abbr.word} "
            } else {
                "$token "
            }
        }
        final
    } else {
        value
    }
}

fun parseData(data: String): ArrayList<Pair<String, String>> {
    val parsedData = arrayListOf<Pair<String, String>>()
    val id = data.substring(0, 2)
    var next = ""
    DataValueIso20022.getValue(id)?.let {
        val value = when {
            it.maxSize != it.minSize -> data.removePrefix(id).substringBefore(29.toChar()) // ASCII 29 <GS>
            it.minSize == it.maxSize -> data.removePrefix(id).substring(0, it.maxSize)
            else -> ""
        }
        parsedData.add(Pair(it.label, parseAbbreviation(value)))
        next = data.removePrefix(id+value).removePrefix(29.toChar().toString())
    }
    if (next != "") {
        parsedData += parseData(next)
    }
    return parsedData
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
                val rest = result.drop(version2dDoc.headerLength).split(31.toChar()) // ASCII 31 - <US> Unit Separator
                val data = rest[0]
                println("data: $data")
                val parsedData = parseData(data)
                println("parsed data:")
                parsedData.forEach { token ->
                    val (label, value) = token
                    if (value == "") {
                        println("$label: vide")
                    } else {
                        println("$label: $value")
                    }
                }
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