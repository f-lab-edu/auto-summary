package com.sjh.autosummary.core.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed interface DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>

    data class Error(val exception: Throwable) : DataState<Nothing>

    data object Loading : DataState<Nothing>

}

fun <T> Flow<T>.toDataState(): Flow<DataState<T>> =
    map<T, DataState<T>> { DataState.Success(it) }
        .onStart { emit(DataState.Loading) }
        .catch { emit(DataState.Error(it)) }
