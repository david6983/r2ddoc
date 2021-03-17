package fr.isen.m1.cyber.r2ddoc.parser

enum class TypeDiplome(
    val abbr: String,
    val label: String
) {
    BR("BR", "Brevet des Collèges"),
    CA("CA", "Certificat d'Aptitude Professionnelle (CAP)"),
    BE("BE", "Brevet d'Etudes Professionnelles (BEP)"),
    BA("BA", "Baccalauréat Général"),
    BP("BP", "Baccalauréat Professionnel"),
    BS("BS", "Baccalauréat Technologique"),
    BT("BT", "Brevet Technicien Supérieur (BTS)"),
    DU("DU", "Diplôme Universitaire de Technologie (DUT)"),
    LC("LC", "Licence"),
    LP("LP", "Licence Professionnelle"),
    DE("DE", "Diplôme Européen d'Etudes Supérieures (DEES)"),
    MA("MA", "Master"),
    MB("MB", "Maîtrise en Administration des Affaires (MBA)"),
    IN("IN", "Diplôme d'Ingénieur"),
    DR("DR", "Doctorat"),
}