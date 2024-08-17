package com.sjh.autosummary.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjh.autosummary.core.common.LoadState
import com.sjh.autosummary.core.data.repository.HistoryRepository
import com.sjh.autosummary.feature.history.contract.sideeffect.HistoryScreenSideEffect
import com.sjh.autosummary.feature.history.contract.state.HistoryScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.annotation.OrbitInternal
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@OptIn(OrbitInternal::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel(), ContainerHost<HistoryScreenState, HistoryScreenSideEffect> {
    override val container: Container<HistoryScreenState, HistoryScreenSideEffect> =
        container(initialState = HistoryScreenState())

    init {
        fetchAllChatHistroies()
    }

    private fun fetchAllChatHistroies() {
        viewModelScope.launch {
            container.orbit {
                if (state.chatHistoriesState is LoadState.Succeeded) return@orbit

                val result = historyRepository.retrieveAllChatHistories()

                result.fold(
                    onSuccess = { foundChatHistories ->
                        reduce {
                            state.copy(chatHistoriesState = LoadState.Succeeded(data = foundChatHistories))
                        }
                    },
                    onFailure = {
                        /* Todo : 데이터 불러오기 실패 토스트 띄우기 */
                    }
                )
            }
        }
    }
}
