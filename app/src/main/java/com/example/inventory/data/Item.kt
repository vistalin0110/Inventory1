/*
 * Copyright (C) 2021 The Android Open Source Project.
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
package com.example.inventory.data

import android.content.ContentValues
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import com.example.inventory.MainActivity.Companion.ITEM_FAV
import com.example.inventory.MainActivity.Companion.ITEM_CHN
import com.example.inventory.MainActivity.Companion.ITEM_ID
import com.example.inventory.MainActivity.Companion.ITEM_ENG

/**
 * Entity data class represents a single row in the database.
 */
@Entity
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "english")
    var english: String="",
    @ColumnInfo(name = "chinese")
    var chinese: String="",
    @ColumnInfo(name = "isFavorite")
    var isFavorite: Int=0,

    ) {

    companion object {
        fun fromContentValues(contentValues: ContentValues): Item {
            var item = Item()
            if (contentValues.containsKey(ITEM_ID)) {
                item.id = contentValues.getAsInteger(ITEM_ID)
            } else if (contentValues.containsKey(ITEM_ENG)) {
                item.english = contentValues.getAsString(ITEM_ENG)
            } else if (contentValues.containsKey(ITEM_CHN)) {
                item.chinese = contentValues.getAsString(ITEM_CHN)
            } else if (contentValues.containsKey(ITEM_FAV)) {
                item.isFavorite = contentValues.getAsInteger(ITEM_FAV)
            }
            return item
        }
    }
}
/**
 * Returns the passed in price in currency format.
 */
