package io.github.liukscot.sonari.audio

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.appPrefsStore by preferencesDataStore(name = "app_prefs")

object AppPrefs {
    private val FADE_ENABLED = booleanPreferencesKey("fade_enabled")
    private val DUCK_FOR_CALLS = booleanPreferencesKey("duck_for_calls")

    fun fadeEnabled(context: Context): Flow<Boolean> =
        context.appPrefsStore.data
            .catch { e -> if (e is CancellationException) throw e else emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { it[FADE_ENABLED] ?: true }

    fun duckForCalls(context: Context): Flow<Boolean> =
        context.appPrefsStore.data
            .catch { e -> if (e is CancellationException) throw e else emit(androidx.datastore.preferences.core.emptyPreferences()) }
            .map { it[DUCK_FOR_CALLS] ?: true }

    suspend fun setFadeEnabled(context: Context, value: Boolean) {
        try {
            context.appPrefsStore.edit { it[FADE_ENABLED] = value }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {}
    }

    suspend fun setDuckForCalls(context: Context, value: Boolean) {
        try {
            context.appPrefsStore.edit { it[DUCK_FOR_CALLS] = value }
        } catch (e: CancellationException) {
            throw e
        } catch (_: IOException) {}
    }
}
