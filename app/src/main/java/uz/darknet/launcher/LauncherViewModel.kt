package uz.darknet.launcher

import android.app.Application
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = LauncherPrefs(application)
    private val iconCache = ConcurrentHashMap<String, ImageBitmap>()
    private var refreshJob: Job? = null

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _favorites = MutableStateFlow(prefs.favorites())
    val favorites: StateFlow<List<String>> = _favorites.asStateFlow()

    private val _hidden = MutableStateFlow(prefs.hidden())
    val hidden: StateFlow<Set<String>> = _hidden.asStateFlow()

    private val _drawerOpen = MutableStateFlow(false)
    val drawerOpen: StateFlow<Boolean> = _drawerOpen.asStateFlow()

    init {
        refresh()
    }

    /** Ilovalar ro'yxatini fon oqimida qayta o'qiydi (ikonkalar keshdan olinadi). */
    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch(Dispatchers.Default) {
            _apps.value = AppRepository.loadApps(getApplication<Application>(), iconCache)
        }
    }

    /** Ilova o'rnatilganda/yangilanganda/o'chirilganda chaqiriladi. */
    fun onPackageChanged(packageName: String?) {
        if (packageName != null) {
            iconCache.keys.removeAll { it.startsWith("$packageName/") }
        }
        refresh()
    }

    fun toggleFavorite(id: String) {
        val current = _favorites.value
        val next = when {
            id in current -> current - id
            current.size < MAX_DOCK -> current + id
            else -> {
                Toast.makeText(getApplication<Application>(), R.string.dock_full, Toast.LENGTH_SHORT).show()
                current
            }
        }
        if (next != current) {
            _favorites.value = next
            prefs.setFavorites(next)
        }
    }

    fun toggleHidden(id: String) {
        val current = _hidden.value
        val next = if (id in current) current - id else current + id
        _hidden.value = next
        prefs.setHidden(next)
        // Yashirilgan ilova dock'da qolmasin
        if (id in next && id in _favorites.value) toggleFavorite(id)
    }

    fun openDrawer() {
        _drawerOpen.value = true
    }

    fun closeDrawer() {
        _drawerOpen.value = false
    }

    companion object {
        const val MAX_DOCK = 5
    }
}
