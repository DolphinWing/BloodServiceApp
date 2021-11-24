package dolphin.android.apps.BloodServiceApp.provider

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Use DataStore Preferences.
 * See [https://developer.android.com/topic/libraries/architecture/datastore]
 */
class PrefsDataStore
/**
 * Create [DataStore].
 *
 * @param dataStore Android Context
 */
constructor(private val dataStore: DataStore<Preferences>) {
    /**
     * Use [Context] to create [DataStore].
     *
     * @param context Android Context
     */
    constructor(context: Context) : this(context.dataStore)

    private val keyCenterId = intPreferencesKey("near_by_center")

    /**
     * Nearby blood center id
     */
    val centerId: Flow<Int> = dataStore.data.map { preferences ->
        // No type safety.
        preferences[keyCenterId] ?: 0
    }

    /**
     * Change to a new nearby blood center
     *
     * @param id blood center id
     */
    suspend fun changeCenter(id: Int) {
        dataStore.edit { settings ->
            settings[keyCenterId] = id
        }
    }

    private val keyPolicyCode = longPreferencesKey("private_policy")

    /**
     * App privacy policy code. We use this to check if we have updated a new policy.
     */
    val policyCode: Flow<Long> = dataStore.data.map { preferences ->
        preferences[keyPolicyCode] ?: 0
    }

    /**
     * Update new privacy policy code.
     *
     * @param code new policy code
     */
    suspend fun updatePolicyCode(code: Long) {
        dataStore.edit { settings ->
            settings[keyPolicyCode] = code
        }
    }

    private val keyMobileAds = booleanPreferencesKey("enable_adview")

    /**
     * A flag to show/hide mobile ads in app.
     */
    val mobileAds: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[keyMobileAds] ?: true
    }

    /**
     * Enable or disable mobile ads in app.
     *
     * @param enabled true if user allows mobile ads in app
     */
    suspend fun toggleAds(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[keyMobileAds] = enabled
        }
    }
}
