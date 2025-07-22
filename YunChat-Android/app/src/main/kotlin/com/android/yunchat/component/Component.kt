package com.android.yunchat.component

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.yunchat.R
import com.android.yunchat.YunChat
import com.android.yunchat.service.instance.activityServiceInstance
import com.android.yunchat.service.instance.dialogServiceInstance
import com.android.yunchat.service.instance.encryptServiceInstance
import com.android.yunchat.service.instance.fileServiceInstance
import kotlinx.coroutines.launch
import okhttp3.Headers
import top.chengdongqing.weui.core.data.model.MediaType
import top.chengdongqing.weui.core.ui.components.dialog.rememberDialogState
import top.chengdongqing.weui.core.ui.components.toast.rememberToastState
import top.chengdongqing.weui.core.utils.isValidUrl

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SquareAsyncImage(
    model: String,
    size: Dp = 0.dp,
    scale: Float = 1f,
    srcUrl: String = "",
    save: Boolean = false,
    maxSize: Boolean = false,
    preview: Boolean = false,
    autoSize: Boolean = false,
    placeholder: Boolean = true,
    shape: Shape = RoundedCornerShape(6.dp),
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val toastState = rememberToastState()
    val dialogState = rememberDialogState()
    val imageLoader = rememberImageLoader()
    val coroutineScope = rememberCoroutineScope()
    val imageRequest = rememberImageRequest(model, context)

    val containerModifier = when {
        autoSize -> Modifier
            .fillMaxWidth()
        maxSize -> Modifier
            .fillMaxSize()
        else -> Modifier
            .size(size)
    }.then(
        Modifier
            .clip(shape)
            .background(
                shape = shape,
                color = Color.White
            )
    )

    val clickModifier = if (save || preview) {
        Modifier.combinedClickable(
            indication = null,
            interactionSource = null,
            onClick = {
                if (!preview) return@combinedClickable
                coroutineScope.launch {
                    fileServiceInstance.openMediaPreview(
                        content = srcUrl,
                        context = context,
                        toastState = toastState,
                        mediaType = MediaType.IMAGE
                    )
                }
            },
            onLongClick = if (save) {
                {
                    dialogServiceInstance.openSaveImageDialog(
                        url = srcUrl,
                        toastState = toastState,
                        dialogState = dialogState
                    )
                }
            } else null
        )
    } else Modifier

    AsyncImage(
        onError = {},
        model = imageRequest,
        imageLoader = imageLoader,
        contentScale = contentScale,
        modifier = Modifier
            .scale(scale)
            .then(clickModifier)
            .then(containerModifier),
        contentDescription = "Square Image",
        error = painterResource(R.drawable.ic_image_error),
        placeholder = if (placeholder) painterResource(R.drawable.ic_image_load) else null,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SubPageToolbar(
    name: String,
    action: @Composable (RowScope.() -> Unit) = {},
    onBack: (Context) -> Unit = {
        activityServiceInstance.intentActivityBack(it)
    }
) {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            titleContentColor = MaterialTheme.colorScheme.secondary,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Text(
                text = name,
                fontSize = 20.sp
            )
        },
        actions = action,
        navigationIcon = {
            IconButton(onClick = {
                onBack(context)
            }) {
                Icon(
                    contentDescription = "返回",
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack
                )
            }
        }
    )
}

@Composable
fun LoadingFrame() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun PlainButton(
    text: String,
    onClick: () -> Unit
) {
    Box (
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primary
            )
            .clip(RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun rememberImageLoader(): ImageLoader {
    val context = YunChat.instance.applicationContext
    return (context as YunChat).imageLoader
}

@Composable
private fun rememberImageRequest(model: String, context: Context): ImageRequest {
    val authorization = encryptServiceInstance.encryptAuthorization()
    return remember(model) {
        if (isValidUrl(model)) {
            ImageRequest.Builder(context)
                .headers(
                    Headers.Builder()
                        .add("Authorization", "Bearer $authorization")
                        .build()
                )
                .data(model)
                .crossfade(true)
                // 显式设置缓存策略（可选，默认已启用）
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        } else {
            ImageRequest.Builder(context)
                .data("file:///android_asset/${model}")
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .build()
        }
    }
}

@Composable
fun ArrowRightIcon(size: Dp = 18.dp) {
    Icon(
        modifier = Modifier
            .size(size),
        contentDescription = "GO",
        tint = Color(0xff999999),
        painter = painterResource(id = R.drawable.ic_arrow_right)
    )
}

@Composable
fun CustomDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(15.dp, 0.dp, 15.dp, 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0XFFE3E8F7))
        )
    }
}