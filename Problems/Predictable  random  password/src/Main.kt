fun generatePredictablePassword(seed: Int): String {
    var randomPassword = ""
    // write your code here, Random is already imported for you
    val random = Random(seed)
    
    for (i in 1..10) {
        randomPassword = randomPassword.plus(random.nextInt(33, 127).toChar())
    }
    
	return randomPassword
}