val advice = try {
    pepTalk(name)
} catch (e : Exception) {
    "Don't lose the towel, nameless one."
} finally {
    println("Good luck!")
}

println(advice)