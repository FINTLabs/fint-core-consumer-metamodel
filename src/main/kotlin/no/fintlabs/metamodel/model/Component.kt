package no.fintlabs.metamodel.model

data class Component(
    val domainName: String,
    val packageName: String,
) {
    private var _resources: List<Resource> = emptyList()
    val resources: List<Resource> get() = _resources

    internal fun setResources(list: List<Resource>) {
        if (_resources.isNotEmpty()) throw IllegalStateException("Resources already set!")
        _resources = list
    }

    fun nameEquals(domainName: String, packageName: String) =
        this.domainName.equals(domainName, ignoreCase = true) &&
                this.packageName.equals(packageName, ignoreCase = true)


}

/**
 * Meant to be used on package names
 */
fun String.createComponent(): Component =
    this.split(".")
        .takeLast(2)
        .let { (domainName, packageName) -> Component(domainName, packageName) }
