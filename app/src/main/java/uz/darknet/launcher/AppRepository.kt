package uz.darknet.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import java.util.concurrent.ConcurrentHashMap

/**
 * @param id ComponentName.flattenToString() — bitta ilovada bir nechta launcher activity bo'lishi mumkin,
 *           shuning uchun kalit sifatida packageName emas, aynan shu ishlatiladi.
 */
data class AppInfo(
    val id: String,
    val label: String,
    val packageName: String,
    val icon: ImageBitmap,
)

object AppRepository {

    /** Launcher'da ko'rinadigan barcha ilovalarni (o'zimizdan tashqari) alifbo tartibida qaytaradi. */
    fun loadApps(context: Context, iconCache: ConcurrentHashMap<String, ImageBitmap>): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        val result = pm.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != context.packageName }
            .map { info ->
                val component = ComponentName(info.activityInfo.packageName, info.activityInfo.name)
                val id = component.flattenToString()
                val icon = iconCache.getOrPut(id) {
                    info.loadIcon(pm).toBitmap(144, 144).asImageBitmap()
                }
                AppInfo(
                    id = id,
                    label = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    icon = icon,
                )
            }
            .sortedBy { it.label.lowercase() }

        // O'chirilgan ilovalarning ikonkalarini keshdan tozalash
        iconCache.keys.retainAll(result.map { it.id }.toSet())
        return result
    }

    fun launch(context: Context, app: AppInfo) {
        val component = ComponentName.unflattenFromString(app.id) ?: return
        val intent = Intent(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .setComponent(component)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        runCatching { context.startActivity(intent) }
    }

    fun openInfo(context: Context, app: AppInfo) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", app.packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }

    fun uninstall(context: Context, app: AppInfo) {
        val intent = Intent(
            Intent.ACTION_DELETE,
            Uri.fromParts("package", app.packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(intent) }
    }
}
