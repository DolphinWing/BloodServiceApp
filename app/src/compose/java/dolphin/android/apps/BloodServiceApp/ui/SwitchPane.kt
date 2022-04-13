package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.util.DebugOnlyNoCoverage

@ExperimentalFoundationApi
@Composable
fun SwitchCenterPane(
    list: List<BloodCenter.Center>,
    selected: BloodCenter.Center,
    modifier: Modifier = Modifier,
    onCenterChange: ((BloodCenter.Center) -> Unit)? = null,
) {
    val selection = list.filter { c -> c.id != selected.id }

    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.section0_summary),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp, end = 4.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        LazyRow(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(selection) { center ->
                SuggestionChipImpl(
                    content = center.name,
                    onClick = { onCenterChange?.invoke(center) },
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SuggestionChipImpl(
    content: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick,
        modifier = modifier.height(32.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        elevation = ButtonDefaults.buttonElevation(1.dp),
    ) {
        Text(content, style = MaterialTheme.typography.labelLarge)
    }
}

@DebugOnlyNoCoverage
@OptIn(ExperimentalFoundationApi::class)
@Preview("Switch center")
@Composable
private fun PreviewSwitchUi() {
    AppTheme {
        SwitchCenterPane(list = PreviewSample.centers, selected = PreviewSample.selectedCenter)
    }
}

@Composable
fun SwitchCenterDialog(
    visible: Boolean,
    list: List<BloodCenter.Center>,
    selected: BloodCenter.Center,
    modifier: Modifier = Modifier,
    onCenterChange: ((BloodCenter.Center) -> Unit)? = null,
) {
    if (visible) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onCenterChange?.invoke(selected) },
            confirmButton = {
//                TextButton(onClick = { onCenterChange?.invoke(selected) }) {
//                    Text(stringResource(id = android.R.string.cancel))
//                }
            },
            title = { Text(stringResource(id = R.string.section0_summary)) },
            text = {
                SwitchCenterDialogContent(list, onCenterChange)
            },
        )
    }
}

@Composable
private fun SwitchCenterDialogContent(
    list: List<BloodCenter.Center>,
    onCenterChange: ((BloodCenter.Center) -> Unit)? = null,
) {
    Column {
        list.forEach { center ->
            TextButton(onClick = { onCenterChange?.invoke(center) }) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(center.name)
                }
            }
        }
    }
}

@Preview("dialog")
@Composable
private fun PreviewSwitchCenterDialog() {
    AppTheme {
        SwitchCenterDialogContent(list = PreviewSample.centers)
    }
}
