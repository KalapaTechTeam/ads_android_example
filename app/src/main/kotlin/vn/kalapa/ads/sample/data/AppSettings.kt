package vn.kalapa.ads.sample.data

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.ChecksSdkIntAtLeast

/** The language the sample (and the SDK screens it opens) show. */
internal enum class LanguageChoice(val tag: String?) {
    SYSTEM(null),
    VIETNAMESE("vi"),
    ENGLISH("en"),
}

/**
 * The sample's language, kept by the platform per app: the system applies it to every activity of the app, so the
 * SDK's mini-app screen follows too. A device older than Android 13 keeps following the device language, and the
 * setting is not offered.
 */
internal object AppSettings {
    @get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    val supportsLanguage: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun language(context: Context): LanguageChoice {
        if (!supportsLanguage) return LanguageChoice.SYSTEM
        val locales = context.getSystemService(LocaleManager::class.java).applicationLocales
        val current = if (locales.isEmpty) null else locales[0].language
        return LanguageChoice.entries.firstOrNull { it.tag != null && it.tag == current } ?: LanguageChoice.SYSTEM
    }

    fun setLanguage(context: Context, choice: LanguageChoice) {
        if (!supportsLanguage) return
        val locales = choice.tag?.let(LocaleList::forLanguageTags) ?: LocaleList.getEmptyLocaleList()
        context.getSystemService(LocaleManager::class.java).applicationLocales = locales
    }
}
