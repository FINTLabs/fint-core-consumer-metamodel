package no.fintlabs.metamodel.metadata.model

data class Component(
    val domainName: String,
    val packageName: String
) {
    val name: String = "${domainName.lowercase()}-${packageName.lowercase()}"

    private fun normalized(): String = name

    private fun normalizeInput(input: String): String =
        input.lowercase()
            .replace(".", "-")
            .replace("_", "-")

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Component -> this.normalized() == other.normalized()
            is String -> this.normalized() == normalizeInput(other)
            else -> false
        }
    }

    override fun hashCode(): Int = normalized().hashCode()

    override fun toString(): String = name
}
