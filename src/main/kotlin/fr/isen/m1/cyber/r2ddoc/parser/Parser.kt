package fr.isen.m1.cyber.r2ddoc.parser

import fr.isen.m1.cyber.r2ddoc.parser.domain.Data2DDoc
import fr.isen.m1.cyber.r2ddoc.parser.domain.Header2DDocC40
import fr.isen.m1.cyber.r2ddoc.parser.enums.Abbreviations
import fr.isen.m1.cyber.r2ddoc.parser.enums.DataValueIso20022
import fr.isen.m1.cyber.r2ddoc.parser.enums.Parsed2DDoc
import fr.isen.m1.cyber.r2ddoc.parser.enums.Version2DDoc
import org.apache.commons.codec.binary.Base32
import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val ASCII_GROUP_SEPARATOR = 29
const val ASCII_UNIT_SEPARATOR = 31

class Parser() {
    fun parse(decodedQrCode: String): Parsed2DDoc? {
        decodedQrCode.substring(2, 4).let { version ->
            Version2DDoc.isSupportedVersion(version)?.let { version2dDoc ->
                val header = decodedQrCode.take(version2dDoc.headerLength)
                println("header: $header")
                parseHeader(header, version)?.let { parsedHeader ->
                    val rest = decodedQrCode.drop(version2dDoc.headerLength).split(ASCII_UNIT_SEPARATOR.toChar())
                    val data = rest[0]
                    println("data: $data")
                    val parsedData = parseData(data)
                    val signature = when(rest.size) {
                        2 -> decodeSignature(rest[1])
                        else -> ""
                    }
                    println("signature: $signature")
                    return Parsed2DDoc(
                        parsedHeader,
                        parsedData,
                        signature
                    )
                }
            }
        }
        return null
    }

    fun decodeSignature(value: String): String {
        return if (value != "") {
            val decodedSignature = Base32().decode(value)
            decodedSignature.joinToString("") { String.format("%02x", it) }
        } else {
            ""
        }
    }

    fun parseHeader(header: String, version: String) : Header2DDocC40? {
        val versionSupported = Version2DDoc.isSupportedVersion(version)
        return if (header.length == versionSupported?.headerLength) {
            Header2DDocC40(
                header.take(2),
                version,
                header.substring(4, 8),
                header.substring(8, 12),
                parseDate(header.substring(12, 16)),
                parseDate(header.substring(16, 20)),
                header.substring(20, 22)
            )
        } else {
            null
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

    fun parseData(data: String): ArrayList<Data2DDoc> {
        val parsedData = arrayListOf<Data2DDoc>()
        val id = data.substring(0, 2)
        var next = ""
        DataValueIso20022.getValue(id)?.let {
            val value = when {
                it.maxSize != it.minSize -> data.removePrefix(id).substringBefore(ASCII_GROUP_SEPARATOR.toChar())
                it.minSize == it.maxSize -> data.removePrefix(id).substring(0, it.maxSize)
                else -> ""
            }
            parsedData.add(Data2DDoc(it.label, parseAbbreviation(value)))
            next = data.removePrefix(id+value).removePrefix(29.toChar().toString())
        }
        if (next != "") {
            parsedData += parseData(next)
        }
        return parsedData
    }

    fun parseDate(value: String): String {
        val numberOfDays = value.toLong(16)
        val date = LocalDate.of(2000, 1, 1).plusDays(numberOfDays)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(formatter)
    }
}