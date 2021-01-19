try {
    suspiciousFunction(data)
} catch (e : IOException) {
    println("The IOException occurred")
} catch (e : Exception) {
    println(e.message)
} finally {
    println("Handling completed successfully!")
}