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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.blacksquircle.ui.ds.navigationitem.NavigationItem
import com.blacksquircle.ui.feature.explorer.R
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceModel
import com.blacksquircle.ui.feature.explorer.domain.model.WorkspaceType
import com.blacksquircle.ui.ds.R as UiR

@Composable
internal fun Workspaces(
    workspaces: List<WorkspaceModel>,
    selectedWorkspace: WorkspaceModel?,
    onWorkspaceClicked: (WorkspaceModel) -> Unit,
    onAddWorkspaceClicked: () -> Unit,
    onDeleteWorkspaceClicked: (WorkspaceModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    Column(
        modifier = modifier
            .width(64.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .systemBarsPadding()
    ) {
        workspaces.fastForEach { workspace ->
            WorkspaceItem(
                workspace = workspace,
                selected = workspace == selectedWorkspace,
                onClick = { onWorkspaceClicked(workspace) },
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDeleteWorkspaceClicked(workspace)
                },
            )
        }
        NavigationItem(
            iconResId = UiR.drawable.ic_plus,
            label = stringResource(R.string.explorer_workspace_button_add),
            onClick = onAddWorkspaceClicked,
        )
    }
}

@Composable
@NonRestartableComposable
private fun WorkspaceItem(
    workspace: WorkspaceModel,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationItem(
        iconResId = when (workspace.type) {
            WorkspaceType.LOCAL -> UiR.drawable.ic_folder
            WorkspaceType.ROOT -> UiR.drawable.ic_folder_pound
            WorkspaceType.CUSTOM -> UiR.drawable.ic_folder
            WorkspaceType.SERVER -> UiR.drawable.ic_server_network
        },
        label = when (workspace.type) {
            WorkspaceType.LOCAL -> stringResource(R.string.explorer_workspace_button_files)
            WorkspaceType.ROOT -> stringResource(R.string.explorer_workspace_button_root)
            WorkspaceType.CUSTOM,
            WorkspaceType.SERVER -> workspace.name
        },
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
    )
}