package top.chengdongqing.weui.core.ui.components.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.chengdongqing.weui.core.ui.components.divider.WeDivider

@Composable
fun WeTextarea(
    value: String?,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    disabled: Boolean = false,
    labelWidth: Dp = 68.dp,
    max: Int? = null,
    minLines: Int = 3,
    topBorder: Boolean = false,
    onChange: ((String) -> Unit)? = null
) {
    val localValue = value ?: ""

    Column {
        if (topBorder) {
            WeDivider()
        }
        Row(
            modifier = modifier.padding(vertical = 14.dp)
        ) {
            if (label?.isNotEmpty() == true) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    modifier = Modifier.width(labelWidth),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            BasicTextField(
                value = localValue,
                onValueChange = { str ->
                    onChange?.apply {
                        if (max != null && str.length > max) {
                            invoke(str.slice(0..<max))
                        } else invoke(str)
                    }
                },
                modifier = Modifier.weight(1f),
                readOnly = disabled,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                ),
                minLines = minLines,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            ) { innerTextField ->
                Box {
                    innerTextField()

                    if (localValue.isEmpty() && placeholder?.isNotEmpty() == true) {
                        Text(
                            fontSize = 16.sp,
                            text = placeholder,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    max?.let {
                        Text(
                            fontSize = 14.sp,
                            text = "${localValue.length}/$it",
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
        /*WeDivider()*/
    }
}