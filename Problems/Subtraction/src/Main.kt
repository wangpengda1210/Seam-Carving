fun solution(elements: Set<String>, element: String): MutableSet<String> {
    // put your code here
    val set = elements.toMutableSet()
    set.remove(element)
    return set
}