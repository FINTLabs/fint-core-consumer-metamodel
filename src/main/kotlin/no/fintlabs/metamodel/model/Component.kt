package no.fintlabs.metamodel.model

class Component(
    component: String,
    format: String,
    val resources: List<Resource>
) {
    val domainName: String = component.split("-").first()
    val packageName: String = component.split("-").last()
    val name: String = "${domainName.lowercase()}${format}${packageName.lowercase()}"

    fun nameEquals(domainName: String, packageName: String) =
        this.domainName.equals(domainName, ignoreCase = true) &&
                this.packageName.equals(packageName, ignoreCase = true)


}
