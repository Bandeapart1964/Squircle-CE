/*
 * Copyright 2025 Squircle CE contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blacksquircle.ui.feature.git.ui.git

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.core.mvi.ViewEvent
import com.blacksquircle.ui.feature.git.api.navigation.FetchDialog
import com.blacksquircle.ui.feature.git.domain.repository.GitRepository
import com.blacksquircle.ui.feature.git.api.navigation.PullDialog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GitViewModel @AssistedInject constructor(
    private val gitRepository: GitRepository,
    @Assisted private val repository: String,
) : ViewModel() {

    private val _viewState = MutableStateFlow(GitViewState())
    val viewState: StateFlow<GitViewState> = _viewState.asStateFlow()

    private val _viewEvent = Channel<ViewEvent>(Channel.BUFFERED)
    val viewEvent: Flow<ViewEvent> = _viewEvent.receiveAsFlow()

    fun onBackClicked() {
        viewModelScope.launch {
            _viewEvent.send(ViewEvent.PopBackStack)
        }
    }

    fun onFetchClicked() {
        viewModelScope.launch {
            val screen = FetchDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onPullClicked() {
        viewModelScope.launch {
            val screen = PullDialog(repository)
            _viewEvent.send(ViewEvent.PopBackStack)
            _viewEvent.send(ViewEvent.Navigation(screen))
        }
    }

    fun onCommitClicked() {
        viewModelScope.launch {
            try {
                // todo: commit dialog with text field (value = commit text)
                gitRepository.commit(repository, "new commit")
            } catch (e: Exception) {
                _viewEvent.send(ViewEvent.Toast("Git error: ${e.message}"))
            }
        }
    }

    fun onPushClicked() {
        viewModelScope.launch {
            try {
                gitRepository.push(repository)
            } catch (e: Exception) {
                _viewEvent.send(ViewEvent.Toast("Git error: ${e.message}"))
            }
        }
    }

    fun onCheckoutClicked() {
        viewModelScope.launch {
            try {
                // todo: checkout dialog with text field (value = branch)
                gitRepository.checkout(repository, "test")
            } catch (e: Exception) {
                _viewEvent.send(ViewEvent.Toast("Git error: ${e.message}"))
            }
        }
    }

    class ParameterizedFactory(private val repository: String) : ViewModelProvider.Factory {

        @Inject
        lateinit var viewModelFactory: Factory

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return viewModelFactory.create(repository) as T
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted repositoryPath: String): GitViewModel
    }
}