package com.example.shelvz.util

sealed class MyResult<out T> {
    data class Success<out T>(val data: T) : MyResult<T>()
    data object Loading : MyResult<Nothing>()
    data class Error(val exception: Throwable) : MyResult<Nothing>()
}