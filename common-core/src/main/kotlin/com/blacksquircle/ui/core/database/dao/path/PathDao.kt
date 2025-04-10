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

package com.blacksquircle.ui.core.database.dao.path

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blacksquircle.ui.core.database.entity.path.PathEntity
import com.blacksquircle.ui.core.database.utils.Tables

@Dao
interface PathDao {

    @Query("SELECT * FROM `${Tables.PATHS}` WHERE `filesystem_uuid` = :uuid")
    suspend fun load(uuid: String): PathEntity?

    @Query("DELETE FROM `${Tables.PATHS}` WHERE `filesystem_uuid` = :uuid")
    suspend fun delete(uuid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pathEntity: PathEntity): Long
}