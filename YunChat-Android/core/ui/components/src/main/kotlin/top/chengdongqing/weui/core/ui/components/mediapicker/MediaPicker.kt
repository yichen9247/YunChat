package top.chengdongqing.weui.core.ui.components.mediapicker

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.chengdongqing.weui.core.data.model.MediaItem
import top.chengdongqing.weui.core.data.model.VisualMediaType
import top.chengdongqing.weui.core.ui.components.actionsheet.ActionSheetItem
import top.chengdongqing.weui.core.ui.components.button.ButtonSize
import top.chengdongqing.weui.core.ui.components.button.WeButton
import top.chengdongqing.weui.core.ui.components.loading.WeLoadMore
import top.chengdongqing.weui.core.ui.components.mediapreview.previewMedias
import top.chengdongqing.weui.core.utils.RequestMediaPermission

@Composable
@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun WeMediaPicker(
    type: VisualMediaType,
    count: Int,
    onCancel: () -> Unit,
    onConfirm: (Array<MediaItem>) -> Unit
) {
    val state = rememberMediaPickerState(type, count)

    RequestMediaPermission(onRevoked = onCancel) {
        Scaffold(
            topBar = {
                MediaPickerTopAppBar(
                    state = state,
                    onCancel = onCancel
                )
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .padding(innerPadding),
                color = MaterialTheme.colorScheme.onBackground
            ) {
                Column {
                    if (!state.isLoading) {
                        MediaGrid(state)
                        BottomBar(state) {
                            onConfirm(state.selectedMediaList.toTypedArray())
                        }
                    } else WeLoadMore()
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MediaPickerTopAppBar(
    state: MediaPickerState,
    onCancel: () -> Unit
) {
    val typeOptions = remember {
        listOf(
            ActionSheetItem("选择图片", value = VisualMediaType.IMAGE),
            ActionSheetItem("选择视频", value = VisualMediaType.VIDEO),
            ActionSheetItem("选择图片和视频", value = VisualMediaType.IMAGE_AND_VIDEO)
        )
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.secondary,
            containerColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.secondary,
            navigationIconContentColor = MaterialTheme.colorScheme.secondary
        ),
        title = {
            Text(
                fontSize = 20.sp,
                text = typeOptions.find { it.value == state.type }?.label!!
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                onCancel()
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
private fun BottomBar(state: MediaPickerState, onConfirm: () -> Unit) {
    val context = LocalContext.current
    val selectedCount = state.selectedMediaList.size
    val countDescription = if (selectedCount > 0) "（$selectedCount）" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "预览$countDescription",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            modifier = Modifier
                .alpha(if (selectedCount > 0) 1f else 0.6f)
                .clickable(enabled = selectedCount > 0) {
                    context.previewMedias(state.selectedMediaList)
                }
        )
        WeButton(
            size = ButtonSize.SMALL,
            text = "确定$countDescription",
            disabled = selectedCount == 0
        ) {
            onConfirm()
        }
    }
}