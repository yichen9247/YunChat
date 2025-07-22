package top.chengdongqing.weui.core.ui.components.cardlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.chengdongqing.weui.core.ui.components.divider.WeDivider

@Composable
fun WeCardListItem(label: String, value: String? = null, between: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .heightIn(50.dp)
            .padding(vertical = 12.dp)
            .clickable(
                indication = null,
                onClick = {
                    onClick()
                },
                interactionSource = null
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondary
        )
        value?.let {
            Spacer(modifier = Modifier.width(15.dp))
            if (between) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
            } else Text(
                text = value,
                fontSize = 14.sp,
                modifier = Modifier.weight(2f),
                color = MaterialTheme.colorScheme.onSecondary
            )

        }
    }
    WeDivider()
}