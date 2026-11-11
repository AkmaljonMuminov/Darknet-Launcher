package uz.darknet.launcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** Ilova ustida bajariladigan barcha amallar. */
data class AppActions(
    val onLaunch: (AppInfo) -> Unit,
    val onToggleFavorite: (AppInfo) -> Unit,
    val onToggleHidden: (AppInfo) -> Unit,
    val onInfo: (AppInfo) -> Unit,
    val onUninstall: (AppInfo) -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    app: AppInfo,
    isFavorite: Boolean,
    isHidden: Boolean,
    actions: AppActions,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .combinedClickable(
                    onClick = { actions.onLaunch(app) },
                    onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuOpen = true
                    },
                )
                .padding(6.dp),
        ) {
            Image(
                bitmap = app.icon,
                contentDescription = app.label,
                modifier = Modifier.size(54.dp),
            )
            if (showLabel) {
                Text(
                    text = app.label,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            if (isFavorite) R.string.menu_remove_dock else R.string.menu_add_dock,
                        ),
                    )
                },
                onClick = {
                    menuOpen = false
                    actions.onToggleFavorite(app)
                },
            )
            DropdownMenuItem(
                text = {
                    Text(stringResource(if (isHidden) R.string.menu_unhide else R.string.menu_hide))
                },
                onClick = {
                    menuOpen = false
                    actions.onToggleHidden(app)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_info)) },
                onClick = {
                    menuOpen = false
                    actions.onInfo(app)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_uninstall)) },
                onClick = {
                    menuOpen = false
                    actions.onUninstall(app)
                },
            )
        }
    }
}
