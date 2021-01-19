fun generateTemperature(seed: Int): String{
    // write your code here, Random is already imported for you
    val random = Random(seed)
    
    val result = StringBuilder()
    for (i in 1..7) {
        result.append(random.nextInt(20, 31)).append(" ")
    }
    
    return result.trim().toString()
}