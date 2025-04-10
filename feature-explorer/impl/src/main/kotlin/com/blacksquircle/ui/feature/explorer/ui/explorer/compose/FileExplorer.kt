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

package com.blacksquircle.ui.feature.explorer.ui.explorer.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastAny
import com.blacksquircle.ui.ds.R
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.emptyview.EmptyView
import com.blacksquircle.ui.ds.progress.CircularProgress
import com.blacksquircle.ui.feature.explorer.domain.model.ErrorAction
import com.blacksquircle.ui.feature.explorer.domain.model.ViewMode
import com.blacksquircle.ui.feature.explorer.ui.explorer.model.BreadcrumbState
import com.blacksquircle.ui.filesystem.base.model.FileModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val RefreshDelay = 500L

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FileExplorer(
    contentPadding: PaddingValues,
    breadcrumbState: BreadcrumbState,
    selectedFiles: List<FileModel>,
    viewMode: ViewMode,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    onFileClicked: (FileModel) -> Unit = {},
    onFileSelected: (FileModel) -> Unit = {},
    onErrorActionClicked: (ErrorAction) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    fun refresh() = refreshScope.launch {
        refreshing = true
        onRefreshClicked()
        delay(RefreshDelay)
        refreshing = false
    }

    val refreshState = rememberPullRefreshState(refreshing, ::refresh)
    val lazyListState = rememberLazyListState()

    val haptic = LocalHapticFeedback.current
    val fileList = breadcrumbState.fileList
    val isError = breadcrumbState.errorState != null
    val isEmpty = fileList.isEmpty()

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = if (isLoading) emptyList() else fileList,
                key = FileModel::fileUri,
            ) { fileModel ->
                val isSelected = selectedFiles
                    .fastAny { it.fileUri == fileModel.fileUri }
                when (viewMode) {
                    ViewMode.COMPACT_LIST -> {
                        CompactFileItem(
                            fileModel = fileModel,
                            isSelected = isSelected,
                            onClick = { onFileClicked(fileModel) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onFileSelected(fileModel)
                            },
                            modifier = Modifier.animateItem(),
                        )
                    }

                    ViewMode.DETAILED_LIST -> {
                        DetailedFileItem(
                            fileModel = fileModel,
                            isSelected = isSelected,
                            onClick = { onFileClicked(fileModel) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onFileSelected(fileModel)
                            },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }

        if (isError && !isLoading) {
            ErrorStatus(
                errorState = breadcrumbState.errorState,
                onActionClicked = onErrorActionClicked,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (isLoading) {
            CircularProgress(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (isEmpty && !isLoading && !isError) {
            EmptyView(
                iconResId = R.drawable.ic_file_find,
                title = stringResource(R.string.common_no_result),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        PullRefreshIndicator(
            state = refreshState,
            refreshing = refreshing,
            backgroundColor = SquircleTheme.colors.colorBackgroundSecondary,
            contentColor = SquircleTheme.colors.colorPrimary,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}