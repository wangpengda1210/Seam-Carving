try {
    throwException(data)
} catch (e: Exception) {
    handleException(data)
} finally {
    print("Will be executed in any case")
}