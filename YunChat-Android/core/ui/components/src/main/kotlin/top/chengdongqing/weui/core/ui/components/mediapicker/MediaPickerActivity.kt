package top.chengdongqing.weui.core.ui.components.mediapicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import top.chengdongqing.weui.core.data.model.MediaItem
import top.chengdongqing.weui.core.data.model.VisualMediaType
import top.chengdongqing.weui.core.ui.theme.WeUITheme

class MediaPickerActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = intent.getStringExtra("type")?.run { VisualMediaType.valueOf(this) }
            ?: VisualMediaType.IMAGE_AND_VIDEO
        val count = intent.getIntExtra("count", 99)

        setContent {
            WeUITheme {
                WeMediaPicker(type, count, onCancel = { finish() }) { medias ->
                    val intent = Intent().apply {
                        putExtra("medias", medias)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MediaPickerActivity::class.java)
    }
}

@Composable
fun rememberPickMediasLauncher(onChange: (Array<MediaItem>) -> Unit): (type: VisualMediaType, count: Int) -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getParcelableArrayExtra("medias", MediaItem::class.java)
                } else {
                    @Suppress("DEPRECATION", "UNCHECKED_CAST")
                    (getParcelableArrayExtra("medias") as? Array<MediaItem>)
                }?.let(onChange)
            }
        }
    }

    return { type, count ->
        val intent = MediaPickerActivity.newIntent(context).apply {
            putExtra("type", type.toString())
            putExtra("count", count)
        }
        launcher.launch(intent)
    }
}