package com.github.chengpohi.script

data class EDQLRunResult(
    val response: Result<List<String>>,
    val context: EDQLRunContext? = null
) {
    val isSuccess: Boolean
        get() = response.isSuccess

    val isFail: Boolean
        get() = response.isFailure

    val failed: Throwable
        get() = response.exceptionOrNull() ?: throw IllegalStateException("No exception available")

    val success: List<String>
        get() = response.getOrThrow()
}