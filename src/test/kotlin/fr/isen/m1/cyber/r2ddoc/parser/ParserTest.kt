package fr.isen.m1.cyber.r2ddoc.parser

import fr.isen.m1.cyber.r2ddoc.parser.enums.Parsed2DDoc
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ParserTest {
    private val parser = Parser()
    private val sample1 = "DC02FR03EDF11BFD1BFD0001977985C0F7A54F72BFE24EEE52DEDB8B\u001D10M/GONCALVES ROCHA/RENAN\u001D20\u001D21\u001D229 RUE MAYET\u001D23\u001D247500625PARIS\u001D26FR\u001F2X5TKR5P2VFSVTPKM7TJQMJF4Q4L6K7JZNYA7BHNPHYUS3P2HVZRS7YGC27JP5YBJAXOSUU2VHWVUAWCFWHCEHIVFCST3QUO53KLIWQ"
    private val sample2 = "DC02FR000001125E125B0126FR247500010MME/SPECIMEN/NATACHA\u001D22145 AVENUE DES SPECIMENS\u001D\u001F54LDD5F7JD4JEFPR6WZYVZVB2JZXPZB73SP7WUTN5N44P3GESXW75JZUZD5FM3G4URAJ6IKDSSUB66Y3OWQIEH22G46QOAGWH7YHJWQ"

    private val headerSample1 = "DC02FR03EDF11BFD1BFD00"
    private val headerSample2 = "DC02FR000001125E125B01"
    private val dataSample1 = "01977985C0F7A54F72BFE24EEE52DEDB8B\u001D10M/GONCALVES ROCHA/RENAN\u001D20\u001D21\u001D229 RUE MAYET\u001D23\u001D247500625PARIS\u001D26FR"
    private val dataSample2 = "26FR247500010MME/SPECIMEN/NATACHA\u001D22145 AVENUE DES SPECIMENS\u001D"
    private val signatureSample1 = "2X5TKR5P2VFSVTPKM7TJQMJF4Q4L6K7JZNYA7BHNPHYUS3P2HVZRS7YGC27JP5YBJAXOSUU2VHWVUAWCFWHCEHIVFCST3QUO53KLIWQ"
    private val signatureSample2 = "54LDD5F7JD4JEFPR6WZYVZVB2JZXPZB73SP7WUTN5N44P3GESXW75JZUZD5FM3G4URAJ6IKDSSUB66Y3OWQIEH22G46QOAGWH7YHJWQ"

    @Test
    fun `parseDate() should parse a date from hexadecimal (XXXX) to human format`() {
        Assertions.assertEquals("31/12/2011", parser.parseDate("111E"))
        Assertions.assertEquals("14/08/2019", parser.parseDate("1BFD"))
    }

    @Test
    fun `parseHeader() should return a valid Header2DDocC40 object`() {
        val header = parser.parseHeader(headerSample1, "02")
        Assertions.assertNotNull(header)
        Assertions.assertEquals("DC", header?.identificationMarker)
        Assertions.assertEquals("02", header?.version)
        Assertions.assertEquals("FR03", header?.authorityCertificationId)
        Assertions.assertEquals("EDF1", header?.certificateId)
        Assertions.assertEquals("14/08/2019", header?.emissionDocumentDate)
        Assertions.assertEquals("14/08/2019", header?.signatureCreationDate)
        Assertions.assertEquals("00", header?.documentTypeId)

        val header2 = parser.parseHeader(headerSample2, "02")
        Assertions.assertNotNull(header2)
        Assertions.assertEquals("DC", header2?.identificationMarker)
        Assertions.assertEquals("02", header2?.version)
        Assertions.assertEquals("FR00", header2?.authorityCertificationId)
        Assertions.assertEquals("0001", header2?.certificateId)
        Assertions.assertEquals("15/11/2012", header2?.emissionDocumentDate)
        Assertions.assertEquals("12/11/2012", header2?.signatureCreationDate)
        Assertions.assertEquals("01", header2?.documentTypeId)

        val notSupportedVersion = parser.parseHeader("DC03FR03EDF11BFD1BFD00", "03")
        Assertions.assertNull(notSupportedVersion)

        val tooLongHeaderV2 = parser.parseHeader("DC02FR03EDF11BFD1BFD000", "02")
        Assertions.assertNull(tooLongHeaderV2)
    }

    @Test
    fun `parseAbbreviation() should return a human readable string of each abbreviations`() {
        Assertions.assertEquals("Université Sapins verts ", parser.parseAbbreviation("UNIV/Sapins verts"))
        Assertions.assertEquals("Lol", parser.parseAbbreviation("Lol"))
    }

    @Test
    fun `parseData() should separate each tokens correctly`() {
        val parsedData = parser.parseData(dataSample1)
        Assertions.assertEquals(9, parsedData.size)
        Assertions.assertEquals("Identifiant unique du document", parsedData[0].label)
        Assertions.assertEquals("977985C0F7A54F72BFE24EEE52DEDB8B", parsedData[0].value)
        Assertions.assertEquals("Ligne 1 de la norme adresse postale", parsedData[1].label)
        Assertions.assertEquals("Monsieur GONCALVES ROCHA RENAN ", parsedData[1].value)
        Assertions.assertEquals("Ligne 2 de la norme adresse postale du point de service des prestations", parsedData[2].label)
        Assertions.assertEquals("", parsedData[2].value)
        Assertions.assertEquals("Ligne 3 de la norme adresse postale du point de service des prestations", parsedData[3].label)
        Assertions.assertEquals("", parsedData[3].value)
        Assertions.assertEquals("Ligne 4 de la norme adresse postale du point de service des prestations", parsedData[4].label)
        Assertions.assertEquals("9 RUE MAYET", parsedData[4].value)
        Assertions.assertEquals("Ligne 5 de la norme adresse postale du point de service des prestations", parsedData[5].label)
        Assertions.assertEquals("", parsedData[5].value)
        Assertions.assertEquals("Code postal ou code cedex du point de service des prestations", parsedData[6].label)
        Assertions.assertEquals("75006", parsedData[6].value)
        Assertions.assertEquals("Localité de destination ou libellé cedex du point de service des prestations ", parsedData[7].label)
        Assertions.assertEquals("PARIS", parsedData[7].value)
        Assertions.assertEquals("Pays de service des prestations", parsedData[8].label)
        Assertions.assertEquals("FR", parsedData[8].value)

        val parsedData2 = parser.parseData(dataSample2)
        Assertions.assertEquals(4, parsedData2.size)
        Assertions.assertEquals("Pays de service des prestations", parsedData2[0].label)
        Assertions.assertEquals("FR", parsedData2[0].value)
        Assertions.assertEquals("Code postal ou code cedex du point de service des prestations", parsedData2[1].label)
        Assertions.assertEquals("75000", parsedData2[1].value)
        Assertions.assertEquals("Ligne 1 de la norme adresse postale", parsedData2[2].label)
        Assertions.assertEquals("Madame SPECIMEN NATACHA ", parsedData2[2].value)
        Assertions.assertEquals("Ligne 4 de la norme adresse postale du point de service des prestations", parsedData2[3].label)
        Assertions.assertEquals("145 AVENUE DES SPECIMENS", parsedData2[3].value)
    }

    @Test
    fun `decodeSignature() should return the decoded Base32 signature in hexadecimal`() {
        Assertions.assertEquals("d5fb3547afd54b2acdea67e6983125e438bf2be9cb700f84ed79f1496dfa3d73197f0616be97f701482ee9529aa9ed5a02c22d8e221d1528a53dc28eeed4b45a", parser.decodeSignature(signatureSample1))
        Assertions.assertEquals("ef1631f4bf48f89215f1f5b38ae6a1d27377e43fdc9ffb526deb79c7ecc495edfea734c8fa566cdca4409f214394a81f7b1b75a0821f5a373d0700d63ff074da", parser.decodeSignature(signatureSample2))
    }

    @Test
    fun `parse() should decode the entire 2d-doc string`() {
        val parsedCode = parser.parse(sample1)
        Assertions.assertNotNull(parsedCode)
        Assertions.assertEquals(9, parsedCode?.data?.size)

        val parsedCode2 = parser.parse(sample2)
        Assertions.assertNotNull(parsedCode2)
        Assertions.assertEquals(4, parsedCode2?.data?.size)
    }
}