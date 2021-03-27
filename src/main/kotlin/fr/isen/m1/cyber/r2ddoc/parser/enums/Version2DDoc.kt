package fr.isen.m1.cyber.r2ddoc.parser.enums

enum class Version2DDoc(val headerLength: Int, val version: String) {
    V01(22, "01"),
    V02(22, "02"),
    V04(26, "04");

    companion object {
        private val supportedVersions: List<Version2DDoc>
            get() = values().toList()

        fun isSupportedVersion(version: String): Version2DDoc? {
            supportedVersions.forEach {
                if (it.version == version) {
                    return it
                }
            }
            return null
        }
    }
}