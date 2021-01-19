fun createDiceGameRandomizer(n: Int): Int {
    // write your code here, Random is already imported for you
    var i = 0
    while (true) {
        val random = Random(i)
        
        var friendSum = 0
        for (j in 1..n) {
            friendSum += random.nextInt(1, 7)
        }
        
        var mySum = 0
        for (j in 1..n) {
            mySum += random.nextInt(1, 7)
        }
        
        if (mySum > friendSum) {
            return i
        }
        
        i++
    }
}