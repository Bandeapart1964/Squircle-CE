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

package com.blacksquircle.ui.application

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.blacksquircle.ui.core.extensions.fullscreenMode
import com.blacksquircle.ui.core.extensions.viewModels
import com.blacksquircle.ui.ds.SquircleTheme
import com.blacksquircle.ui.ds.animation.NavigationTransition
import com.blacksquircle.ui.ds.extensions.LocalNavController
import com.blacksquircle.ui.feature.changelog.ui.changelogGraph
import com.blacksquircle.ui.feature.editor.api.navigation.EditorScreen
import com.blacksquircle.ui.feature.editor.ui.editorGraph
import com.blacksquircle.ui.feature.explorer.ui.explorerGraph
import com.blacksquircle.ui.feature.fonts.ui.fontsGraph
import com.blacksquircle.ui.feature.servers.ui.serversGraph
import com.blacksquircle.ui.feature.settings.ui.settingsGraph
import com.blacksquircle.ui.feature.shortcuts.ui.shortcutsGraph
import com.blacksquircle.ui.feature.themes.ui.themesGraph
import com.blacksquircle.ui.internal.di.AppComponent
import com.blacksquircle.ui.utils.InAppUpdate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

internal class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var inAppUpdate: InAppUpdate

    @Inject
    lateinit var viewModelProvider: Provider<MainViewModel>

    private val viewModel by viewModels<MainViewModel> { viewModelProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        AppComponent.buildOrGet(this).inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SquircleTheme {
                Surface(color = SquircleTheme.colors.colorBackgroundPrimary) {
                    val navController = rememberNavController()
                    CompositionLocalProvider(
                        LocalNavController provides navController,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = EditorScreen,
                            enterTransition = { NavigationTransition.EnterTransition },
                            exitTransition = { NavigationTransition.ExitTransition },
                            popEnterTransition = { NavigationTransition.PopEnterTransition },
                            popExitTransition = { NavigationTransition.PopExitTransition },
                        ) {
                            changelogGraph(navController)
                            editorGraph(navController)
                            explorerGraph(navController)
                            fontsGraph(navController)
                            serversGraph(navController)
                            settingsGraph(navController)
                            shortcutsGraph(navController)
                            themesGraph(navController)
                            dialog<UpdateDialog> {
                                UpdateScreen(navController, inAppUpdate)
                            }
                        }
                    }
                    LaunchedEffect(Unit) {
                        inAppUpdate.checkForUpdates(this@MainActivity) {
                            navController.navigate(UpdateDialog)
                        }
                    }
                }
            }
        }

        viewModel.viewState.flowWithLifecycle(lifecycle)
            .onEach { state ->
                window.fullscreenMode(state.fullscreenMode)
            }
            .launchIn(lifecycleScope)

        if (savedInstanceState == null) {
            viewModel.handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.handleIntent(intent)
    }
}