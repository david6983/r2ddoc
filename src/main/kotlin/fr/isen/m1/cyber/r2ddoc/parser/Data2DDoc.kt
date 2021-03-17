package fr.isen.m1.cyber.r2ddoc.parser

data class Data2DDoc(val label: String, val value: String) {
    fun display() {
        if (value == "") {
            println("$label: vide")
        } else {
            println("$label: $value")
        }
    }
}