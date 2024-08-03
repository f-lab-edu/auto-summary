package com.sjh.autosummary.core.common

sealed interface LoadState<out T> {
    data class Succeeded<T>(val data: T) : LoadState<T>

    data class Failed(val exception: Throwable) : LoadState<Nothing>

    data object InProgress : LoadState<Nothing>
}
