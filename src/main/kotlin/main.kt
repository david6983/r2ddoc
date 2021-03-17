import com.google.zxing.NotFoundException
import fr.isen.m1.cyber.r2ddoc.decoding.decodeQRCode
import fr.isen.m1.cyber.r2ddoc.parser.*
import java.io.IOException

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