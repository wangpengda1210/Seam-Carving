fun solution(elements: MutableSet<Int>, element: Int): MutableSet<Int> =
        if (elements.contains(element)) emptySet<Int>().toMutableSet() else elements