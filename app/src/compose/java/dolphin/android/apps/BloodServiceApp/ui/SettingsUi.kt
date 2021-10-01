package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R

@Composable
fun SettingsUi(
    modifier: Modifier = Modifier,
    onBackPress: (() -> Unit)? = null,
    version: String = "v0.0",
) {
    Scaffold(
        topBar = {
            TopAppBar {
                IconButton(onClick = { onBackPress?.invoke() }) {
                    Icon(
                        Icons.Rounded.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
                Text(
                    stringResource(id = R.string.title_activity_settings),
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        },
    ) { padding ->
        Column(modifier = modifier.padding(padding)) {
            Text(
                version,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Separator(modifier = Modifier.fillMaxWidth())
            Text(
                stringResource(id = R.string.app_privacy_policy),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            Separator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Preview("Settings", showSystemUi = true)
@Composable
private fun PreviewSettingsUi() {
    AppTheme {
        SettingsUi(modifier = Modifier.fillMaxSize())
    }
}