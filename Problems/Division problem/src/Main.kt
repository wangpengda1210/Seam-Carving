fun solution(first: Set<Int>, second: Set<Int>): Set<Int> {
    // put your code here
    return if (second.isEmpty()) {
        emptySet()
    } else {
        first.filter { i ->
            i % second.size == 0
        }.toSet()
    }
}