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

package com.blacksquircle.ui.feature.terminal

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.blacksquircle.ui.feature.terminal.api.navigation.TerminalScreen
import com.blacksquircle.ui.feature.terminal.ui.TerminalScreen

fun NavGraphBuilder.terminalGraph(navController: NavHostController) {
    composable<TerminalScreen> { backStackEntry ->
        val navArgs = backStackEntry.toRoute<TerminalScreen>()
        TerminalScreen(navArgs, navController)
    }
}