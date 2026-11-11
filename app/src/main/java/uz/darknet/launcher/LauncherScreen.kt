package uz.darknet.launcher

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.LocalContentColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import uz.darknet.launcher.ui.NeonCyan
import uz.darknet.launcher.ui.NeonGreen
import uz.darknet.launcher.ui.Panel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LauncherApp(vm: LauncherViewModel) {
    val context = LocalContext.current
    val apps by vm.apps.collectAsStateWithLifecycle()
    val favIds by vm.favorites.collectAsStateWithLifecycle()
    val hiddenIds by vm.hidden.collectAsStateWithLifecycle()
    val drawerOpen by vm.drawerOpen.collectAsStateWithLifecycle()

    val actions = remember(vm, context) {
        AppActions(
            onLaunch = { app ->
                AppRepository.launch(context, app)
                vm.closeDrawer()
            },
            onToggleFavorite = { app -> vm.toggleFavorite(app.id) },
            onToggleHidden = { app -> vm.toggleHidden(app.id) },
            onInfo = { app -> AppRepository.openInfo(context, app) },
            onUninstall = { app -> AppRepository.uninstall(context, app) },
        )
    }

    val favApps = remember(apps, favIds) {
        favIds.mapNotNull { id -> apps.firstOrNull { it.id == id } }
    }

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
        Box(modifier = Modifier.fillMaxSize()) {
            HomeScreen(
                favApps = favApps,
                hiddenIds = hiddenIds,
                actions = actions,
                onOpenDrawer = vm::openDrawer,
            )

            AnimatedVisibility(
                visible = drawerOpen,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            ) {
                DrawerScreen(
                    apps = apps,
                    favIds = favIds,
                    hiddenIds = hiddenIds,
                    actions = actions,
                    onClose = vm::closeDrawer,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Bosh ekran: soat, dock, swipe-up
// ---------------------------------------------------------------------------------------------

@Composable
private fun HomeScreen(
    favApps: List<AppInfo>,
    hiddenIds: Set<String>,
    actions: AppActions,
    onOpenDrawer: () -> Unit,
) {
    // Bosh ekranda "orqaga" hech narsa qilmasligi kerak.
    BackHandler(enabled = true) { }

    val openDrawer by rememberUpdatedState(onOpenDrawer)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.65f)),
                ),
            )
            .pointerInput(Unit) {
                var total = 0f
                detectVerticalDragGestures(
                    onDragStart = { total = 0f },
                    onDragEnd = { if (total < -120f) openDrawer() },
                    onVerticalDrag = { change, dy ->
                        change.consume()
                        total += dy
                    },
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp),
        ) {
            ClockBlock(modifier = Modifier.padding(top = 56.dp))

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.swipe_hint),
                color = NeonGreen.copy(alpha = 0.85f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { openDrawer() }
                    .padding(vertical = 10.dp),
            )

            Dock(favApps = favApps, hiddenIds = hiddenIds, actions = actions)
        }
    }
}

@Composable
private fun ClockBlock(modifier: Modifier = Modifier) {
    val now by produceState(initialValue = LocalDateTime.now()) {
        while (true) {
            value = LocalDateTime.now()
            delay(60_000L - (System.currentTimeMillis() % 60_000L))
        }
    }
    val timeFormat = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val dateFormat = remember { DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()) }

    Column(modifier = modifier) {
        Text(
            text = now.format(timeFormat),
            color = NeonGreen,
            fontSize = 64.sp,
        )
        Text(
            text = now.format(dateFormat).replaceFirstChar { it.uppercase() },
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun Dock(favApps: List<AppInfo>, hiddenIds: Set<String>, actions: AppActions) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(shape)
            .background(Panel.copy(alpha = 0.78f))
            .border(1.dp, NeonCyan.copy(alpha = 0.25f), shape)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (favApps.isEmpty()) {
            Text(
                text = stringResource(R.string.dock_hint),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 16.dp),
            )
        } else {
            favApps.forEach { app ->
                AppIcon(
                    app = app,
                    isFavorite = true,
                    isHidden = app.id in hiddenIds,
                    actions = actions,
                    showLabel = false,
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------
// Ilovalar drawer'i: qidiruv, grid, yashirinlar
// ---------------------------------------------------------------------------------------------

@Composable
private fun DrawerScreen(
    apps: List<AppInfo>,
    favIds: List<String>,
    hiddenIds: Set<String>,
    actions: AppActions,
    onClose: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var showHidden by remember { mutableStateOf(false) }

    // Avval qidiruvni tozalaydi, keyin drawer'ni yopadi.
    BackHandler(enabled = true) {
        if (query.isNotEmpty()) query = "" else onClose()
    }

    val visible = remember(apps, hiddenIds, showHidden, query) {
        apps.filter { app ->
            (showHidden || app.id !in hiddenIds) &&
                (query.isBlank() || app.label.contains(query.trim(), ignoreCase = true))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            .safeDrawingPadding()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.search_hint)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { visible.firstOrNull()?.let { actions.onLaunch(it) } },
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = NeonCyan.copy(alpha = 0.4f),
                    cursorColor = NeonGreen,
                ),
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { showHidden = !showHidden }) {
                Text(
                    text = stringResource(R.string.hidden_apps),
                    color = if (showHidden) NeonGreen
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(visible, key = { it.id }) { app ->
                AppIcon(
                    app = app,
                    isFavorite = app.id in favIds,
                    isHidden = app.id in hiddenIds,
                    actions = actions,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
