package vn.kalapa.ads.sample.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class MiniAppProfileStoreTest {
    @Test
    fun profileSurvivesStoreRecreationAndKeepsIdentifiersAsText() {
        val preferences = InMemoryStringPreferences()
        val profile = MiniAppProfile("Nguyễn Ánh", "0900123456", "001234567890", "2500000", "12")

        MiniAppProfileStore(preferences).save(profile)

        val loaded = MiniAppProfileStore(preferences).load()
        assertEquals(profile, loaded)
        assertEquals("0900123456", loaded.optionalPhoneNumber())
        assertEquals("001234567890", loaded.optionalUserId())
        assertEquals(2_500_000L, loaded.optionalAmount())
        assertEquals("12", loaded.optionalPeriod())
        assertEquals(
            "MiniAppProfile(fullNamePresent=true, phoneNumberPresent=true, userIdPresent=true, amountPresent=true, " +
                "periodPresent=true)",
            loaded.toString(),
        )
        assertFalse(loaded.toString().contains("0900123456"))
        assertFalse(loaded.toString().contains("001234567890"))
    }

    @Test
    fun clearRemovesSavedProfileAndBlankOrInvalidValuesAreNotConfigured() {
        val preferences = InMemoryStringPreferences()
        val store = MiniAppProfileStore(preferences)
        store.save(MiniAppProfile("Name", "0900", "0001", "not-a-number"))

        store.clear()

        assertEquals(MiniAppProfile(), MiniAppProfileStore(preferences).load())
        val invalid = MiniAppProfile(fullName = "  ", phoneNumber = "  ", userId = "", amount = "bad", period = " ")
        assertNull(invalid.optionalFullName())
        assertNull(invalid.optionalPhoneNumber())
        assertNull(invalid.optionalUserId())
        assertNull(invalid.optionalAmount())
        assertNull(invalid.optionalPeriod())
        assertNull(MiniAppProfile(amount = "-1").optionalAmount())
    }

    @Test
    fun amountAcceptsZeroAndMaximumLongButRejectsNegativeMalformedAndOverflow() {
        assertEquals(0L, MiniAppProfile(amount = "0").optionalAmount())
        assertEquals(Long.MAX_VALUE, MiniAppProfile(amount = Long.MAX_VALUE.toString()).optionalAmount())
        listOf("-1", "12.5", "12x", "9223372036854775808").forEach { invalidAmount ->
            val profile = MiniAppProfile(amount = invalidAmount)
            assertFalse(profile.isAmountValid())
            assertNull(profile.optionalAmount())
        }
    }
}

private class InMemoryStringPreferences : StringPreferences {
    private val values = mutableMapOf<String, String>()

    override fun get(key: String): String? = values[key]

    override fun putAll(values: Map<String, String>) {
        this.values.putAll(values)
    }

    override fun clear() {
        values.clear()
    }
}
