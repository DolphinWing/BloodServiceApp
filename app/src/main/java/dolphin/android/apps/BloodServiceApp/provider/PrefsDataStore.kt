package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * https://developer.android.com/topic/libraries/architecture/datastore
 */
class PrefsDataStore(private val context: Context) {
    // At the top level of your kotlin file:
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val keyCenterId = intPreferencesKey("near_by_center")
    val centerId: Flow<Int> = context.dataStore.data.map { preferences ->
        // No type safety.
        preferences[keyCenterId] ?: 0
    }

    suspend fun changeCenter(id: Int) {
        context.dataStore.edit { settings ->
            settings[keyCenterId] = id
        }
    }

    private val keyPolicyCode = longPreferencesKey("private_policy")
    val policyCode: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[keyPolicyCode] ?: 0
    }

    suspend fun updatePolicyCode(code: Long) {
        context.dataStore.edit { settings ->
            settings[keyPolicyCode] = code
        }
    }
}
