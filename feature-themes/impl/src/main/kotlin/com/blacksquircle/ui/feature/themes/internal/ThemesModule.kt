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

package com.blacksquircle.ui.feature.themes.internal

import android.content.Context
import com.blacksquircle.ui.core.database.AppDatabase
import com.blacksquircle.ui.core.database.dao.theme.ThemeDao
import com.blacksquircle.ui.core.provider.coroutine.DispatcherProvider
import com.blacksquircle.ui.core.settings.SettingsManager
import com.blacksquircle.ui.feature.themes.api.interactor.ThemesInteractor
import com.blacksquircle.ui.feature.themes.data.interactor.ThemesInteractorImpl
import com.blacksquircle.ui.feature.themes.data.repository.ThemesRepositoryImpl
import com.blacksquircle.ui.feature.themes.domain.repository.ThemesRepository
import dagger.Module
import dagger.Provides

@Module
internal object ThemesModule {

    @Provides
    @ThemesScope
    fun provideThemesInteractor(
        dispatcherProvider: DispatcherProvider,
        context: Context,
    ): ThemesInteractor {
        return ThemesInteractorImpl(
            dispatcherProvider = dispatcherProvider,
            context = context,
        )
    }

    @Provides
    @ThemesScope
    fun provideThemesRepository(
        dispatcherProvider: DispatcherProvider,
        themesInteractor: ThemesInteractor,
        settingsManager: SettingsManager,
        themeDao: ThemeDao,
        context: Context,
    ): ThemesRepository {
        return ThemesRepositoryImpl(
            dispatcherProvider = dispatcherProvider,
            themesInteractor = themesInteractor,
            settingsManager = settingsManager,
            themeDao = themeDao,
            context = context,
        )
    }

    @Provides
    fun provideThemeDao(appDatabase: AppDatabase): ThemeDao {
        return appDatabase.themeDao()
    }
}