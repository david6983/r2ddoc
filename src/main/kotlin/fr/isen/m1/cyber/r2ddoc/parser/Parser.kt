package fr.isen.m1.cyber.r2ddoc.parser

import org.apache.commons.codec.binary.Base32

class Parser() {
    fun parse(decodedQrCode: String): Parsed2DDoc? {
        decodedQrCode.substring(2, 4).let { version ->
            Version2DDoc.isSupportedVersion(version)?.let { version2dDoc ->
                val header = decodedQrCode.take(version2dDoc.headerLength)
                parseHeader(header, version)?.let { parsedHeader ->
                    val rest = decodedQrCode.drop(version2dDoc.headerLength).split(31.toChar()) // ASCII 31 - <US> Unit Separator
                    val data = rest[0]
                    val parsedData = parseData(data)
                    var hexSignature = ""
                    val signature = when(rest.size) {
                        2 -> rest[1]
                        else -> ""
                    }
                    if (signature != "") {
                        val decodedSignature = Base32().decode(signature)
                        hexSignature = decodedSignature.joinToString("") { String.format("%02x", it) }
                    }
                    return Parsed2DDoc(
                        parsedHeader,
                        parsedData,
                        hexSignature
                    )
                }
            }
        }
        return null
    }

    private fun parseHeader(header: String, version: String) : Header2DDocC40? {
        val versionSupported = Version2DDoc.isSupportedVersion(version)
        return if (header.length == versionSupported?.headerLength) {
            Header2DDocC40(
                header.take(2),
                version,
                header.substring(4, 8),
                header.substring(8, 12),
                header.substring(12, 16),
                header.substring(16, 20),
                header.substring(20, 22)
            )
        } else {
            null
        }
    }

    private fun parseAbbreviation(value: String): String {
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

    private fun parseData(data: String): ArrayList<Data2DDoc> {
        val parsedData = arrayListOf<Data2DDoc>()
        val id = data.substring(0, 2)
        var next = ""
        DataValueIso20022.getValue(id)?.let {
            val value = when {
                it.maxSize != it.minSize -> data.removePrefix(id).substringBefore(29.toChar()) // ASCII 29 <GS>
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
}