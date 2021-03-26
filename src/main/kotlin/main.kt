import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler
import java.lang.Exception
import javax.xml.parsers.SAXParserFactory

fun main(args: Array<String>) {
    /* URL url = new URL("https://ants.gouv.fr/content/download/517/5670/version/18/file/ANTS_2D-Doc_TSL_v12_sign.xml");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");*/


  try {
        val spfactory = SAXParserFactory.newInstance()
        val saxParser = spfactory.newSAXParser()
        val handler: DefaultHandler = object : DefaultHandler() {
            var bId = false
            var bURL = false

            /* '<' */
            @Throws(SAXException::class)
            override fun startElement(
                uri: String, localName: String,
                qName: String, attributes: Attributes
            ) {
                if (qName.equals("tsl:X509Certificate", ignoreCase = true)) {
                    bId = true
                }
                if (qName.equals("tsl:URI", ignoreCase = true)) {
                    bURL = true
                }
            }

            /* '>' */
            @Throws(SAXException::class)
            override fun endElement(
                uri: String, localName: String,
                qName: String
            ) {
                if (qName.equals("tsl:X509Certificate", ignoreCase = true)) {
                    bId = false
                }
                if (qName.equals("tsl:URI", ignoreCase = true)) {
                    bURL = false
                }
            }

            /*between '<' et '>' */
            @Throws(SAXException::class)
            override fun characters(
                ch: CharArray, start: Int,
                length: Int
            ) {
                if (bId) {
                    println(
                        "ID : " +
                                String(ch, start, length)
                    )
                    val ID = String(ch, start, length)
                    bId = false
                }
                if (bURL) {
                    println(
                        "Url : " +
                                String(ch, start, length)
                    )
                    bURL = false
                }
            }
        }
        saxParser.parse(
            "https://ants.gouv.fr/content/download/517/5670/version/18/file/ANTS_2D-Doc_TSL_v12_sign.xml",
            handler
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
