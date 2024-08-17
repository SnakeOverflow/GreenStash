/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.backup

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.starry.greenstash.database.core.GoalWithTransactions

/**
 * Converts [GoalWithTransactions] data to JSON format and vice versa.
 */
class GoalToJSONConverter {

    /**
     * Instance of [Gson] with custom type adaptor applied for serializing
     * and deserializing [Bitmap] fields.
     */
    private val gsonInstance = GsonBuilder()
        .registerTypeAdapter(Bitmap::class.java, BitmapTypeAdapter())
        .setDateFormat(ISO8601_DATE_FORMAT)
        .create()

    companion object {
        /** Backup schema version. */
        const val BACKUP_SCHEMA_VERSION = 1

        /** An ISO-8601 date format for Gson */
        private const val ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    }

    /**
     * Model for backup json data, containing current schema version
     * and timestamp when backup was created.
     *
     * @param version backup schema version.
     * @param timestamp timestamp when backup was created.
     * @param data list of [GoalWithTransactions] to be backed up.
     */
    @Keep
    data class BackupJsonModel(
        val version: Int = BACKUP_SCHEMA_VERSION,
        val timestamp: Long,
        val data: List<GoalWithTransactions>
    )

    fun convertToJson(goalWithTransactions: List<GoalWithTransactions>): String =
        gsonInstance.toJson(
            BackupJsonModel(timestamp = System.currentTimeMillis(), data = goalWithTransactions)
        )

    fun convertFromJson(json: String): BackupJsonModel =
        gsonInstance.fromJson(json, BackupJsonModel::class.java)
}