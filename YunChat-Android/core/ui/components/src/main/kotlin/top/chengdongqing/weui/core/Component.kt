package top.chengdongqing.weui.core

import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import top.chengdongqing.weui.core.ui.components.R
import top.chengdongqing.weui.core.utils.types.ToastType

/**
 * Material风格的加载界面
 * @param paddingValues
 */
@Composable
fun DefaultLoadPage(paddingValues: PaddingValues? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues ?: PaddingValues(0.dp))
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/**
 * Material风格的提示界面
 * @param text
 * @param type
 * @param paddingValues
 */
@Composable
fun DefaultInfoPage(
    text: String,
    type: ToastType = ToastType.INFO,
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val tipIcon = when(type) {
        ToastType.ERROR -> R.drawable.ic_error
        ToastType.WARNING -> R.drawable.ic_warning
        ToastType.SUCCESS -> R.drawable.ic_success
        else -> R.drawable.ic_info
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(100.dp),
                contentDescription = type.toString(),
                painter = painterResource(tipIcon)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = text,
                fontSize = 24.sp,
                color = Color.Gray
            )
        }
    }
}

/**
 * Material风格的视频组件
 * @param videoUri
 */
@Composable
@OptIn(UnstableApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
fun DefaultVideoPlayer(videoUri: Uri) {
    val context = LocalContext.current
    val aspectRatioState = remember { mutableFloatStateOf(16f / 9f) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()

            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    val width = videoSize.width
                    val height = videoSize.height
                    if (width > 0 && height > 0) {
                        aspectRatioState.floatValue = width.toFloat() / height.toFloat()
                    }
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxWidth = maxWidth
        val height = if (aspectRatioState.floatValue > 0) {
            (maxWidth / aspectRatioState.floatValue).coerceAtMost(maxHeight)
        } else 200.dp

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(6.dp))
        )
    }
}
