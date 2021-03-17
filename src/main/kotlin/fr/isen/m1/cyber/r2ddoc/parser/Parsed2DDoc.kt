package fr.isen.m1.cyber.r2ddoc.parser

data class Parsed2DDoc(
    val header: Header2DDocC40,
    val data: ArrayList<Data2DDoc>,
    val signature: String
) {
    fun display() {
        header.display()
        println("Data:")
        data.forEach { token ->
            token.display()
        }
        println("Signature (hex): $signature")
    }
}