// Android/app/src/main/kotlin/com/xtream/player/presentation/player/PlayerScreen.kt
package xtream.emin.player.presentation.player

import android.app.Activity
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import xtream.emin.player.domain.entities.StreamType

private data class TrackOption(
    val group: Tracks.Group,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean
)

private enum class ZoomMode(val resizeMode: Int, val label: String) {
    FIT(AspectRatioFrameLayout.RESIZE_MODE_FIT, "Sığdır"),
    FILL(AspectRatioFrameLayout.RESIZE_MODE_FILL, "Doldur"),
    ZOOM(AspectRatioFrameLayout.RESIZE_MODE_ZOOM, "Yakınlaştır")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    streamId: String,
    streamType: StreamType,
    name: String,
    containerExtension: String?,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current

    LaunchedEffect(streamId, streamType) {
        viewModel.loadStream(streamId, streamType, name, containerExtension)
    }

    // Immersive playback: hide system bars for the lifetime of this screen,
    // restoring them on exit so the rest of the app stays normal.
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    var audioTracks by remember { mutableStateOf(listOf<TrackOption>()) }
    var subtitleTracks by remember { mutableStateOf(listOf<TrackOption>()) }
    var subtitlesEnabled by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableStateOf(1f) }
    var showTrackDialog by remember { mutableStateOf(false) }
    var zoomMode by remember { mutableStateOf(ZoomMode.FIT) }
    var controlsVisible by remember { mutableStateOf(true) }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                val audio = mutableListOf<TrackOption>()
                val subs = mutableListOf<TrackOption>()
                tracks.groups.forEach { group ->
                    for (i in 0 until group.length) {
                        val format = group.getTrackFormat(i)
                        val label = format.label ?: format.language?.uppercase() ?: "Parça ${i + 1}"
                        val option = TrackOption(group, i, label, group.isTrackSelected(i))
                        when (group.type) {
                            C.TRACK_TYPE_AUDIO -> audio += option
                            C.TRACK_TYPE_TEXT -> subs += option
                            else -> Unit
                        }
                    }
                }
                audioTracks = audio
                subtitleTracks = subs
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(uiState.playbackUrl) {
        uiState.playbackUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    // Controls (back, title, track/zoom buttons) auto-hide a few seconds after
    // they're shown, keeping playback clean/distraction-free as requested.
    LaunchedEffect(controlsVisible, showTrackDialog) {
        if (controlsVisible && !showTrackDialog) {
            delay(3500)
            controlsVisible = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage.orEmpty(),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.playbackUrl == null -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = zoomMode.resizeMode
                        }
                    },
                    update = { playerView -> playerView.resizeMode = zoomMode.resizeMode },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { controlsVisible = !controlsVisible }
                )
            }
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    text = name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
                IconButton(onClick = {
                    zoomMode = ZoomMode.values()[(zoomMode.ordinal + 1) % ZoomMode.values().size]
                }) {
                    Icon(Icons.Filled.AspectRatio, contentDescription = zoomMode.label, tint = Color.White)
                }
                if (uiState.playbackUrl != null) {
                    IconButton(onClick = { showTrackDialog = true }) {
                        Icon(Icons.Filled.Subtitles, contentDescription = "Ses, altyazı ve hız", tint = Color.White)
                    }
                }
            }
        }
    }

    if (showTrackDialog) {
        TrackSelectionDialog(
            audioTracks = audioTracks,
            subtitleTracks = subtitleTracks,
            subtitlesEnabled = subtitlesEnabled,
            playbackSpeed = playbackSpeed,
            onAudioSelected = { option ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setOverrideForType(TrackSelectionOverride(option.group.mediaTrackGroup, option.trackIndex))
                    .build()
            },
            onSubtitleSelected = { option ->
                subtitlesEnabled = true
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .setOverrideForType(TrackSelectionOverride(option.group.mediaTrackGroup, option.trackIndex))
                    .build()
            },
            onSubtitlesOff = {
                subtitlesEnabled = false
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                    .build()
            },
            onSpeedSelected = { speed ->
                playbackSpeed = speed
                exoPlayer.setPlaybackSpeed(speed)
            },
            onDismiss = { showTrackDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackSelectionDialog(
    audioTracks: List<TrackOption>,
    subtitleTracks: List<TrackOption>,
    subtitlesEnabled: Boolean,
    playbackSpeed: Float,
    onAudioSelected: (TrackOption) -> Unit,
    onSubtitleSelected: (TrackOption) -> Unit,
    onSubtitlesOff: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Oynatıcı Ayarları") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    Text("Hız", style = MaterialTheme.typography.titleSmall)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f).forEach { speed ->
                            FilterChip(
                                selected = playbackSpeed == speed,
                                onClick = { onSpeedSelected(speed) },
                                label = { Text("${speed}x") },
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }
                    }
                }
                if (audioTracks.isNotEmpty()) {
                    item { Text("Ses", style = MaterialTheme.typography.titleSmall) }
                    items(audioTracks) { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = option.isSelected, onClick = { onAudioSelected(option) })
                            Text(option.label)
                        }
                    }
                }
                if (subtitleTracks.isNotEmpty()) {
                    item { Text("Altyazı", style = MaterialTheme.typography.titleSmall) }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = !subtitlesEnabled, onClick = onSubtitlesOff)
                            Text("Kapalı")
                        }
                    }
                    items(subtitleTracks) { option ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = subtitlesEnabled && option.isSelected,
                                onClick = { onSubtitleSelected(option) }
                            )
                            Text(option.label)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}
