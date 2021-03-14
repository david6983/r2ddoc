package fr.isen.m1.cyber.r2ddoc

// -1 for max size means none"),
enum class DataValueIso20022(
    val id: String,
    val label: String,
) {
    ID_0A("0A", "Editeur du 2D-Doc"),
    ID_0B("0B", "Intégrateur du 2D-Doc"),
    ID_0C("0C", "URL du document"),
    ID_01("01", "Identifiant unique du document"),
    ID_02("02", "Catégorie de document"),
    ID_03("03", "Sous-catégorie de document"),
    ID_04("04", "Application de composition"),
    ID_05("05", "Version de l’application de composition"),
    ID_06("06", "Date de l’association entre le document et le code 2D-Doc. Cette date est indiquée par le nombre de jours en hexadécimal depuis le 1er janvier 2000."),
    ID_07("07", "Heure de l’association entre le document et le code 2D-Doc."),
    ID_08("08", "Date d’expiration du document"),
    ID_10("10", "Ligne 1 de la norme adresse postale : Qualité – Nom – Prénom – éventuellement titre ou profession du bénéficiaire de la prestation"),
    ID_11("11", "Qualité de la personne bénéficiaire de la prestation"),
    ID_12("12", "Prénom de la personne bénéficiaire de la prestation"),
    ID_13("13", "Nom de la personne bénéficiaire de la prestation"),
    ID_14("14", "Ligne 1 de la norme adresse postale Qualité – Nom – Prénom– éventuellement titre ou profession du destinataire de la facture (Ligne 1 de la norme adresse postale)"),
    ID_15("15", "Qualité de la personne destinataire de la facture"),
    ID_16("16", "Prénom de la personne destinataire de la facture"),
    ID_17("17", "Nom de la personne destinataire de la facture"),
    ID_18("18", "Numéro de facture"),
    ID_19("19", "Numéro de client"),
    ID_1A("1A", "Numéro du contrat"),
    ID_1B("1B", "Identifiant du souscripteur du contrat"),
    ID_1C("1C", "Date d’effet du contrat"),
    ID_1D("1D", "Montant de la facture"),
    ID_1E("1E", "Numéro de téléphone du bénéficiaire de la prestation"),
    ID_1F("1F", "Numéro de téléphone du destinataire de la facture"),
    ID_1G("1G", "Présence d’un co-bénéficiaire de la prestation non mentionné dans le code"),
    ID_1H("1H", "Présence d’un co-destinataire de la facture non mentionné dans le code"),
    ID_1I("1I", "Ligne 1 de la norme adresse postale du co-bénéficiaire de la prestation."),
    ID_1J("1J", "Qualité et/ou titre du co-bénéficiaire de la prestation."),
    ID_1K("1K", "Prénom du co-bénéficiaire de la prestation."),
    ID_1L("1L", "Nom du co-bénéficiaire de la prestation."),
    ID_1M("1M", "Ligne 1 de la norme adresse postale du co-destinataire de la facture."),
    ID_1N("1N", "Qualité et/ou titre du co-destinataire de la facture."),
    ID_1O("1O", "Prénom du co-destinataire de la facture."),
    ID_1P("1P", "Nom du co-destinataire de la facture."),
    ID_20("20", "Ligne 2 de la norme adresse postale du point de service des prestations :"),
    ID_21("21", "Ligne 3 de la norme adresse postale du point de service des prestations :"),
    ID_22("22", "Ligne 4 de la norme adresse postale du point de service des prestations :"),
    ID_23("23", "Ligne 5 de la norme adresse postale du point de service des prestations : Mention de distribution (BP) suivie du libellé de la localité de destination dans le cas où celle-ci serait différente du libellé cedex lieu-dit ou hameau"),
    ID_24("24", "Code postal ou code cedex du point de service des prestations (compris dans la ligne 6 de la norme adresse postale)"),
    ID_25("25", "Localité de destination ou libellé cedex du point de service des prestations (compris dans la ligne 6 de la norme adresse postale)"),
    ID_26("26", "Pays de service des prestations"),
    ID_27("27", "Ligne 2 de la norme adresse postale du destinataire de la facture"),
    ID_28("28", "Ligne 3 de la norme adresse postale du destinataire de la facture"),
    ID_29("29", "Numéro dans la voie + type et nom de la voie (Ligne 4 de la norme adresse postale) du destinataire de la facture"),
    ID_2A("2A", "Mention de distribution (BP) suivie du libellé de la localité de destination dans le cas où celle-ci serait différente du libellé cedex lieu- dit ou hameau (Ligne 5 de la norme adresse postale) du destinataire de la facture"),
    ID_2B("2B", "Code postal ou code cedex du destinataire de la facture (compris dans la ligne 6 de la norme adresse postale)"),
    ID_2C("2C", "Localité de destination ou libellé cedex du destinataire de la facture (compris dans la ligne 6 de la norme adresse postale)"),
    ID_2D("2D", "Pays du destinataire de la facture"),
    ID_30("30", "Qualité Nom et Prénom"),
    ID_31("31", "Code IBAN"),
    ID_33("33", "Code BBAN"),
    ID_34("34", "Pays de localisation du compte"),
    ID_32("32", "Code BIC"),
    ID_35("35", "Identifiant SEPAmail (QXBAN)"),
    ID_36("36", "Date de début de période"),
    ID_37("37", "Date de fin de période"),
    ID_38("38", "Solde compte courant début de période"),
    ID_39("39", "Solde compte courant fin de période"),
    ID_40("40", "Numéro fiscal"),
    ID_41("41", "Revenu fiscal de référence"),
    ID_42("42", "Situation du foyer"),
    ID_43("43", "Nombre de parts"),
    ID_44("44", "Référence d’avis d’impôt"),
    ID_50("50", "SIRET de l’employeur"),
    ID_51("51", "Nombre d’heures travaillées"),
    ID_52("52", "Cumul du nombre d’heures travaillées"),
    ID_53("53", "Début de période"),
    ID_54("54", "Fin de période"),
    ID_55("55", "Date de début de contrat"),
    ID_56("56", "Date de fin de contrat"),
    ID_57("57", "Date de signature du contrat"),
    ID_58("58", "Salaire net imposable"),
    ID_59("59", "Cumul du salaire net imposable"),
    ID_5A("5A", "Salaire brut du mois"),
    ID_5B("5B", "Cumul du salaire brut"),
    ID_5C("5C", "Salaire net"),
    ID_5D("5D", "Ligne 2 de la norme adresse postale de l’employeur"),
    ID_5E("5E", "Ligne 3 de la norme adresse postale de l’employeur"),
    ID_5F("5F", "Numéro dans la voie + type et nom de la voie (Ligne 4 de la norme adresse postale) de l’employeur"),
    ID_5G("5G", "Mention de distribution (BP) suivie du libellé de la localité de destination dans le cas où celle-ci serait différente du libellé cedex lieu- dit ou hameau (Ligne 5 de la norme adresse postale) de l’employeur"),
    ID_5H("5H", "Code postal ou code cedex de l’employeur (compris dans la ligne 6 de la norme adresse postale)"),
    ID_5I("5I", "Localité de destination ou libellé cedex de l’employeur (compris dans la ligne 6 de la norme adresse postale)"),
    ID_5J("5J", "Pays de l’employeur"),
    ID_5K("5K", "Identifiant Cotisant Prestations Sociales"),
    ID_60("60", "Liste des prénoms"),
    ID_61("61", "Prénom"),
    ID_62("62", "Nom patronymique"),
    ID_63("63", "Nom d’usage"),
    ID_64("64", "Nom d’épouse/époux"),
    ID_65("65", "Type de pièce d’identité"),
    ID_66("66", "Numéro de la pièce d’identité"),
    ID_67("67", "Nationalité"),
    ID_68("68", "Genre"),
    ID_69("69", "Date de naissance"),
    ID_6A("6A", "Lieu de naissance"),
    ID_6B("6B", "Département du bureau émetteur"),
    ID_6C("6C", "Pays de naissance"),
    ID_6D("6D", "Nom et prénom du père. L’utilisation du séparateur ‘/’ est possible pour séparer le nom du prénom."),
    ID_6E("6E", "Nom et prénom de la mère. L’utilisation du séparateur ‘/’ est possible pour séparer le nom du prénom."),
    ID_6F("6F", "Machine Readable Zone (Zone de Lecture Automatique, ZLA)"),
    ID_6G("6G", "Nom"),
    ID_6H("6H", "Civilité"),
    ID_6I("6I", "Pays émetteur"),
    ID_6J("6J", "Type de document étranger"),
    ID_6K("6K", "Numéro de la demande de document étranger"),
    ID_6L("6L", "Date de dépôt de la demande"),
    ID_6M("6M", "Catégorie du titre"),
    ID_6N("6N", "Date de début de validité"),
    ID_6O("6O", "Date de fin de validité"),
    ID_6P("6P", "Autorisation"),
    ID_6Q("6Q", "Numéro d’étranger"),
    ID_6R("6R", "Numéro de visa"),
    ID_6S("6S", "Ligne 2 de l’adresse postale du domicile"),
    ID_6T("6T", "Ligne 3 de l’adresse postale du domicile"),
    ID_6U("6U", "Ligne 4 de l’adresse postale du domicile"),
    ID_6V("6V", "Ligne 5 de l’adresse postale du domicile"),
    ID_6W("6W", "Code postal ou code cedex de l’adresse postale du domicile"),
    ID_6X("6X", "Commune de l’adresse postale du domicile"),
    ID_6Y("6Y", "Code pays de l’adresse postale du domicile"),
    ID_70("70", "Date et heure du décès"),
    ID_71("71", "Date et heure du constat de décès"),
    ID_72("72", "Nom du défunt"),
    ID_73("73", "Prénoms du défunt"),
    ID_74("74", "Nom de jeune fille du défunt"),
    ID_75("75", "Date de naissance du défunt"),
    ID_76("76", "Genre du défunt"),
    ID_77("77", "Commune de décès"),
    ID_78("78", "Code postal de la commune de décès"),
    ID_79("79", "Adresse du domicile du défunt"),
    ID_7A("7A", "Code postal du domicile du défunt"),
    ID_7B("7B", "Commune du domicile du défunt"),
    ID_7C("7C", "Obstacle médico-légal"),
    ID_7D("7D", "Mise en bière"),
    ID_7E("7E", "Obstacle aux soins de conservation"),
    ID_7F("7F", "Obstacle aux dons du corps"),
    ID_7G("7G", "Recherche de la cause du décès"),
    ID_7H("7H", "Délai de transport du corps"),
    ID_7I("7I", "Prothèse avec pile"),
    ID_7J("7J", "Retrait de la pile de prothèse"),
    ID_7K("7K", "Code NNC"),
    ID_7L("7L", "Code Finess de l'organisme agréé"),
    ID_7M("7M", "Identification du médecin"),
    ID_7N("7N", "Lieu de validation du certificat de décès"),
    ID_7O("7O", "Certificat de décès supplémentaire"),
    ID_80("80", "Nom"),
    ID_81("81", "Prénoms"),
    ID_82("82", "Numéro de carte"),
    ID_83("83", "Organisme de tutelle"),
    ID_84("84", "Profession"),
    ID_90("90", "Identité de l'huissier de justice"),
    ID_91("91", "Identité ou raison sociale du demandeur"),
    ID_92("92", "Identité ou raison sociale du destinataire"),
    ID_93("93", "Identité ou raison sociale de tiers concerné"),
    ID_94("94", "Intitulé de l'acte"),
    ID_95("95", "Numéro de l'acte"),
    ID_96("96", "Date de signature de l'acte"),
    ID_A0("A0", "Pays ayant émis l’immatriculation du véhicule."),
    ID_A1("A1", "Immatriculation du véhicule"),
    ID_A2("A2", "Marque du véhicule."),
    ID_A3("A3", "Nom commercial du véhicule."),
    ID_A4("A4", "Numéro de série du véhicule (VIN)."),
    ID_A5("A5", "Catégorie du véhicule."),
    ID_A6("A6", "Carburant."),
    ID_A7("A7", "Taux d’émission de CO2 du véhicule (en g/km)."),
    ID_A8("A8", "Indication de la classe environnementale de réception CE."),
    ID_A9("A9", "Classe d’émission polluante."),
    ID_AA("AA", "Date de première immatriculation du véhicule."),
    ID_AB("AB", "Type de lettre"),
    ID_AC("AC", "N° Dossier"),
    ID_AD("AD", "Date Infraction"),
    ID_AE("AE", "Heure de l’infraction"),
    ID_AF("AF", "Nombre de points retirés lors de l’infraction"),
    ID_AG("AG", "Solde de points"),
    ID_AH("AH", "Numéro de la carte"),
    ID_AI("AI", "Date d’expiration initiale"),
    ID_AJ("AJ", "Numéro EVTC"),
    ID_AK("AK", "Numéro de macaron"),
    ID_AL("AL", "Numéro de la carte"),
    ID_AM("AM", "Motif de sur-classement"),
    ID_AN("AN", "Kilométrage"),
    ID_B0("B0", "Liste des prénoms"),
    ID_B1("B1", "Prénom"),
    ID_B2("B2", "Nom patronymique"),
    ID_B3("B3", "Nom d'usage"),
    ID_B4("B4", "Nom d'épouse/époux"),
    ID_B5("B5", "Nationalité Country"),
    ID_B6("B6", "Genre"),
    ID_B7("B7", "Date de naissance"),
    ID_B8("B8", "Lieu de naissance"),
    ID_B9("B9", "Pays de naissance"),
    ID_BA("BA", "Mention obtenue"),
    ID_BB("BB", "Numéro ou code d'identification de l'étudiant"),
    ID_BC("BC", "Numéro du diplôme"),
    ID_BD("BD", "Niveau du diplôme selon la classification CEC"),
    ID_BE("BE", "Crédits ECTS obtenus"),
    ID_BF("BF", "Année universitaire"),
    ID_BG("BG", "Type de diplôme"),
    ID_BH("BH", "Domaine"),
    ID_BI("BI", "Mention"),
    ID_BJ("BJ", "Spécialité"),
    ID_BK("BK", "Numéro de l'Attestation de versement de la CVE"),
    ID_C0("C0", "Genre du vendeur"),
    ID_C1("C1", "Nom patronymique du vendeur"),
    ID_C2("C2", "Prénom du vendeur"),
    ID_C3("C3", "Date et heure de la cession"),
    ID_C4("C4", "Date de la signature du vendeur"),
    ID_C5("C5", "Genre de l’acheteur"),
    ID_C6("C6", "Nom patronymique de l’acheteur"),
    ID_C7("C7", "Prénom de l’acheteur"),
    ID_C8("C8", "Ligne 4 de la norme adresse postale du domicile de l’acheteur"),
    ID_C9("C9", "Code postal ou code cedex du domicile de l’acheteur"),
    ID_CA("CA", "Commune du domicile de l’acheteur"),
    ID_CB("CB", "N° d’enregistrement"),
    ID_CC("CC", "Date et heure d'enregistrement dans le SIV");

    companion object {
        private val supportedValues: List<DataValueIso20022>
            get() = values().toList()

        fun getAbbreviation(id: String): DataValueIso20022? {
            supportedValues.forEach {
                if (it.id == id) {
                    return it
                }
            }
            return null
        }

        fun getIds(): List<String> {
            return supportedValues.map { it.id }
        }

        fun getLabels(): List<String> {
            return supportedValues.map { it.label }
        }
    }
}
