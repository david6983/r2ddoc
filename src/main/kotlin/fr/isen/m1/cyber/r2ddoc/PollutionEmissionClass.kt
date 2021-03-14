package fr.isen.m1.cyber.r2ddoc

enum class PollutionEmissionClass(
    val encoding: String,
    val meaning: String
) {
    CLASS_E("E", "Véhicule électrique"),
    CLASS_1("1", "Classe 1"),
    CLASS_2("2", "Classe 2"),
    CLASS_3("3", "Classe 3"),
    CLASS_4("4", "Classe 4"),
    CLASS_5("5", "Classe 5"),
    CLASS_6("6", "Classe 6"),
}