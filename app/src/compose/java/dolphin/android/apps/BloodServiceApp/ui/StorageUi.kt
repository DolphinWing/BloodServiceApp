package dolphin.android.apps.BloodServiceApp.ui

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalFoundationApi
@Composable
fun StoragePane(
    map: HashMap<String, Int>,
    modifier: Modifier = Modifier,
    onToolTip: (() -> Unit)? = null,
) {
    val colorMap = arrayOf(
        Color.Gray,
        colorResource(id = android.R.color.holo_red_light),
        colorResource(id = android.R.color.holo_orange_light),
        colorResource(id = android.R.color.holo_green_light),
    )
    // add semantics to icon
    val statusMap =
        stringArrayResource(id = dolphin.android.apps.BloodServiceApp.R.array.blood_storage_status)
    val typeMap = stringArrayResource(id = dolphin.android.apps.BloodServiceApp.R.array.blood_type)

    Row(
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp),
        // horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(id = dolphin.android.apps.BloodServiceApp.R.string.section1_summary),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.requiredWidth(8.dp))

        arrayOf("A", "B", "O", "AB").forEachIndexed { index, type ->
            TypeStorageStatusIcon(
                type = type,
                color = colorMap[map[type] ?: 0],
                contentDescription = typeMap[index] + statusMap[map[type] ?: 0],
            )
        }
        onToolTip?.let { callback ->
            IconButton(onClick = callback) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun TypeStorageStatusIcon(
    type: String,
    color: Color,
    contentDescription: String,
) {
    val context = LocalContext.current
    fun showToast() {
        Toast.makeText(context, contentDescription, Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .testTag("bloodType$type")
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(color = color, shape = CircleShape)
            .requiredSize(32.dp)
            .combinedClickable(enabled = true, onLongClick = { showToast() }, onClick = {})
            .semantics(mergeDescendants = true) { stateDescription = contentDescription },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            type,
            modifier = Modifier.clearAndSetSemantics { },
            fontSize = 16.sp,
            fontFamily = FontFamily.SansSerif,
            color = Color.White.copy(alpha = .95f),
            fontWeight = FontWeight.Bold,
        )
    }
}
