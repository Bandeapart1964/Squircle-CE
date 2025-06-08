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

package com.blacksquircle.ui.feature.terminal.internal

import android.content.Context
import com.blacksquircle.ui.core.internal.CoreApiDepsProvider
import com.blacksquircle.ui.core.internal.CoreApiProvider
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApiDepsProvider
import com.blacksquircle.ui.feature.terminal.api.internal.TerminalApiProvider
import com.blacksquircle.ui.feature.terminal.ui.TerminalViewModel
import dagger.Component

@TerminalScope
@Component(
    modules = [
        TerminalModule::class,
    ],
    dependencies = [
        CoreApiDepsProvider::class,
        TerminalApiDepsProvider::class,
    ]
)
internal interface TerminalComponent {

    fun inject(factory: TerminalViewModel.ParameterizedFactory)

    @Component.Factory
    interface Factory {
        fun create(
            coreApiDepsProvider: CoreApiDepsProvider,
            terminalApiDepsProvider: TerminalApiDepsProvider,
        ): TerminalComponent
    }

    companion object {

        private var component: TerminalComponent? = null

        fun buildOrGet(context: Context): TerminalComponent {
            return component ?: DaggerTerminalComponent.factory().create(
                coreApiDepsProvider = (context.applicationContext as CoreApiProvider)
                    .provideCoreApiDepsProvider(),
                terminalApiDepsProvider = (context.applicationContext as TerminalApiProvider)
                    .provideTerminalApiDepsProvider(),
            ).also {
                component = it
            }
        }

        fun release() {
            component = null
        }
    }
}