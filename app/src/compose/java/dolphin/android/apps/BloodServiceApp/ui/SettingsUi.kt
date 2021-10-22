package dolphin.android.apps.BloodServiceApp.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil

/**
 * Settings UI callbacks
 */
interface SettingsUiCallback {
    /**
     * Show asset file content in a dialog.
     *
     * @param title title id
     * @param asset asset file name
     */
    fun showAssetInDialog(@StringRes title: Int, asset: String)

    /**
     * App version info.
     *
     * @return app version info
     */
    fun versionInfo(): String

    /**
     * Enable or disable if we can read change logs.
     *
     * @return true if change logs can be shown in a dialog
     */
    fun enableVersionSummary(): Boolean
}

/**
 * Settings UI in Compose way.
 */
@Composable
fun SettingsUi(
    modifier: Modifier = Modifier,
    onBackPress: (() -> Unit)? = null,
    version: String = "v0.0.0",
    onReview: ((Int, String) -> Unit)? = null,
    showChangeLog: Boolean = false,
) {
    var dialog by remember { mutableStateOf(false) }
    var assets by remember { mutableStateOf("") }

    fun showDialog(@StringRes title: Int, asset: String) {
        onReview?.invoke(title, asset) ?: run {
            assets = asset
            dialog = true
        }
    }

    ShowAssetContentDialog(asset = assets, visible = dialog, onDismiss = { dialog = false })

    Scaffold(
        modifier = modifier,
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
        Column(modifier = Modifier.padding(padding)) {
            SettingsSectionTitle(stringResource(id = R.string.app_build_info))
            SettingsTwoLinedText(
                title = stringResource(id = R.string.app_version),
                modifier = Modifier
                    .clickable(enabled = showChangeLog) {
                        showDialog(R.string.app_change_log, "version.txt")
                    }
                    .fillMaxWidth(),
                summary = version,
            )
            SettingsTwoLinedText(
                stringResource(id = R.string.app_privacy_policy),
                modifier = Modifier
                    .clickable { showDialog(R.string.app_privacy_policy, "privacy_policy.txt") }
                    .fillMaxWidth(),
            )
            // Separator()
            SettingsSectionTitle(stringResource(id = R.string.title_open_source))
            SettingsTwoLinedText(
                title = stringResource(id = R.string.title_open_source_jetpack),
                summary = stringResource(id = R.string.summary_open_source_jetpack),
            )
            SettingsTwoLinedText(
                title = stringResource(id = R.string.title_open_source_okhttp),
                summary = stringResource(id = R.string.summary_open_source_okhttp),
            )
            // Separator()
        }
    }
}

@Preview("Settings", showSystemUi = true)
@Preview("Settings Night", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSettingsUi() {
    AppTheme {
        SettingsUi(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SettingsSectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp, end = 24.dp),
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.secondary,
        // fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun SettingsTwoLinedText(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .defaultMinSize(minHeight = 48.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(title, style = MaterialTheme.typography.h6, modifier = Modifier.fillMaxWidth())
        if (summary?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.requiredHeight(2.dp))
            Text(
                summary,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Show a alert dialog with assent content.
 *
 * @param asset asset file name
 * @param visible true if alert dialog is visible
 * @param onDismiss dismiss callback of the alert dialog
 */
@Composable
fun ShowAssetContentDialog(asset: String, visible: Boolean, onDismiss: () -> Unit) {
    val context = LocalContext.current

    if (visible) {
        val content = PrefsUtil.read_asset_text(context, asset, "UTF-8")

        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxHeight(.95f),
            buttons = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                }
            },
            text = {
                Text(content ?: "TODO")
            }
        )
    }
}