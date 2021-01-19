fun solution(setSource: Set<String>, arraySource: Array<String>): MutableSet<String> {
    // put your code here
    val newSet = setSource.toMutableSet()
    newSet.addAll(arraySource)
    return newSet
}