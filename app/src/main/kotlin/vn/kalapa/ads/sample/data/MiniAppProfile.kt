package vn.kalapa.ads.sample.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/** Values the host gives the SDK when it creates a session. */
internal data class MiniAppProfile(
    val fullName: String = "",
    val phoneNumber: String = "",
    val userId: String = "",
    val amount: String = "",
    val period: String = "",
) {
    fun optionalFullName(): String? = fullName.takeIf(String::isNotBlank)

    fun optionalPhoneNumber(): String? = phoneNumber.takeIf(String::isNotBlank)

    fun optionalUserId(): String? = userId.takeIf(String::isNotBlank)

    fun isAmountValid(): Boolean = amount.isBlank() || amount.toLongOrNull()?.let { it >= 0L } == true

    fun optionalPeriod(): String? = period.takeIf(String::isNotBlank)

    fun optionalAmount(): Long? = amount.takeIf(String::isNotBlank)?.toLongOrNull()?.takeIf { it >= 0L }

    override fun toString(): String =
        "MiniAppProfile(fullNamePresent=${fullName.isNotBlank()}, phoneNumberPresent=${phoneNumber.isNotBlank()}, " +
            "userIdPresent=${userId.isNotBlank()}, amountPresent=${amount.isNotBlank()}, " +
            "periodPresent=${period.isNotBlank()})"
}

/** Small adapter keeps profile persistence behavior testable without Android UI or SDK startup. */
internal class MiniAppProfileStore(private val preferences: StringPreferences) {
    fun load(): MiniAppProfile = MiniAppProfile(
        fullName = preferences.get(FULL_NAME).orEmpty(),
        phoneNumber = preferences.get(PHONE_NUMBER).orEmpty(),
        userId = preferences.get(USER_ID).orEmpty(),
        amount = preferences.get(AMOUNT).orEmpty(),
        period = preferences.get(PERIOD).orEmpty(),
    )

    fun save(profile: MiniAppProfile) {
        preferences.putAll(
            mapOf(
                FULL_NAME to profile.fullName,
                PHONE_NUMBER to profile.phoneNumber,
                USER_ID to profile.userId,
                AMOUNT to profile.amount,
                PERIOD to profile.period,
            ),
        )
    }

    fun clear() {
        preferences.clear()
    }

    private companion object {
        const val FULL_NAME = "full_name"
        const val PHONE_NUMBER = "phone_number"
        const val USER_ID = "user_id"
        const val AMOUNT = "amount"
        const val PERIOD = "period"
    }
}

internal fun miniAppProfileStore(
    context: Context,
    preferencesName: String = PREFERENCES_NAME,
): MiniAppProfileStore =
    MiniAppProfileStore(SharedPreferencesAdapter(context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)))

internal interface StringPreferences {
    fun get(key: String): String?

    fun putAll(values: Map<String, String>)

    fun clear()
}

private class SharedPreferencesAdapter(private val preferences: SharedPreferences) : StringPreferences {
    override fun get(key: String): String? = preferences.getString(key, null)

    override fun putAll(values: Map<String, String>) {
        preferences.edit { values.forEach { (key, value) -> putString(key, value) } }
    }

    override fun clear() {
        preferences.edit { clear() }
    }
}

private const val PREFERENCES_NAME = "mini_app_profile"
