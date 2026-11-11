package uz.darknet.launcher

import android.content.Context

/** Dock (sevimlilar) va yashirilgan ilovalarni SharedPreferences'da saqlaydi. */
class LauncherPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("darknet_launcher", Context.MODE_PRIVATE)

    fun favorites(): List<String> =
        prefs.getString(KEY_FAVORITES, "").orEmpty().split(SEPARATOR).filter { it.isNotBlank() }

    fun setFavorites(ids: List<String>) {
        prefs.edit().putString(KEY_FAVORITES, ids.joinToString(SEPARATOR)).apply()
    }

    fun hidden(): Set<String> =
        prefs.getStringSet(KEY_HIDDEN, emptySet()).orEmpty().toSet()

    fun setHidden(ids: Set<String>) {
        prefs.edit().putStringSet(KEY_HIDDEN, ids).apply()
    }

    private companion object {
        const val KEY_FAVORITES = "favorites"
        const val KEY_HIDDEN = "hidden"
        const val SEPARATOR = "\n"
    }
}
