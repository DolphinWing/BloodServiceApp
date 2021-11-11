package dolphin.android.apps.BloodServiceApp.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.util.DebugOnlyNoCoverage
import dolphin.android.util.NoCoverageRequired
import dolphin.android.util.readFromAssets

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

    /**
     * Enable or disable mobile ads
     *
     * @param checked true if user allows mobile ads in app
     */
    fun toggleAds(checked: Boolean)
}

/**
 * Settings UI in Compose way.
 */
@ExperimentalMaterial3Api
@Composable
fun SettingsUi(
    modifier: Modifier = Modifier,
    onBackPress: (() -> Unit)? = null,
    version: String = "v0.0.0",
    onReview: ((Int, String) -> Unit)? = null,
    showChangeLog: Boolean = false,
    showAds: Boolean = false,
    onToggleAds: ((Boolean) -> Unit)? = null,
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
            MediumTopAppBar(
                title = { Text(stringResource(id = R.string.title_activity_settings)) },
                navigationIcon = {
                    IconButton(onClick = { onBackPress?.invoke() }) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SettingsSectionTitle(stringResource(id = R.string.app_build_info))
            SettingsTwoLinedText(
                title = stringResource(id = R.string.app_version),
                modifier = Modifier
                    .testTag("version")
                    .clickable(enabled = showChangeLog) {
                        showDialog(R.string.app_change_log, "version.txt")
                    }
                    .fillMaxWidth(),
                summary = version,
            )
            SettingsTwoLinedText(
                stringResource(id = R.string.app_privacy_policy),
                modifier = Modifier
                    .testTag("privacy")
                    .clickable { showDialog(R.string.app_privacy_policy, "privacy_policy.txt") }
                    .fillMaxWidth(),
            )
            SettingsSectionTitle(stringResource(id = R.string.title_display))
            Row(verticalAlignment = Alignment.CenterVertically) {
                SettingsTwoLinedText(
                    title = stringResource(id = R.string.title_enable_adview),
                    summary = stringResource(id = R.string.title_enable_adview_in_storage_list),
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = showAds,
                    onCheckedChange = { checked -> onToggleAds?.invoke(checked) },
                    modifier = Modifier.padding(end = 24.dp),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.tertiary,
                        checkedTrackColor = MaterialTheme.colorScheme.tertiary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
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
            if (showAds) {
                Spacer(modifier = Modifier.weight(1f))
                BannerAds(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@DebugOnlyNoCoverage
@ExperimentalMaterial3Api
@Preview("Settings", showSystemUi = true)
@Preview("Settings Night", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSettingsUi() {
    AppTheme {
        SettingsUi(modifier = Modifier.fillMaxSize())
    }
}

@ExperimentalMaterial3Api
@Preview("Settings with ads", showSystemUi = true)
@Preview("Settings Night with ads", showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewSettingsUiWithAds() {
    AppTheme {
        SettingsUi(modifier = Modifier.fillMaxSize(), showAds = true)
    }
}

@Composable
private fun SettingsSectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp, end = 24.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.tertiary,
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
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        if (summary?.isNotEmpty() == true) {
            Spacer(modifier = Modifier.requiredHeight(2.dp))
            Text(
                summary,
                style = MaterialTheme.typography.bodyMedium,
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
        val content = context.readFromAssets(asset, "UTF-8")

        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = Modifier.fillMaxHeight(.95f),
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            text = {
                Text(content ?: "TODO")
            }
        )
    }
}

/**
 * Google AdMobs
 */
@NoCoverageRequired
@Composable
fun BannerAds(modifier: Modifier = Modifier) {
    val admobAppId = stringResource(id = R.string.banner_ad_unit_id)
    // https://stackoverflow.com/a/68953869
    if (LocalInspectionMode.current) {
        Text(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(horizontal = 2.dp, vertical = 6.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            text = "Advert Here",
        )
    } else {
        AndroidView(
            factory = { context ->
                AdView(context).apply {
                    adSize = AdSize.BANNER
                    adUnitId = admobAppId
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = modifier.testTag("ads"),
            update = {
                // https://foso.github.io/Jetpack-Compose-Playground/viewinterop/androidview/
            }
        )
    }
}
