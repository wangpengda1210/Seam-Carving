fun evenFilter(numbers: Set<Int>): Set<Int> {
    // put your code here
    return numbers.filter { it % 2 == 0 }.toSet()
}