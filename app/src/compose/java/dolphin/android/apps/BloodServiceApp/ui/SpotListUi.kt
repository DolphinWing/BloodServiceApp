package dolphin.android.apps.BloodServiceApp.ui

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList

/**
 * Spot list UI callbacks
 */
interface SpotListUiCallback {
    /**
     * Show spot info in browser.
     *
     * @param info spot info
     */
    fun showSpotInfo(info: SpotInfo)
}

/**
 * Spot list UI in Compose way.
 */
@ExperimentalMaterial3Api
@Composable
fun SpotListUi(
    list: List<SpotList>,
    modifier: Modifier = Modifier,
    selectedCity: Int = if (list.isNotEmpty()) list.first().cityId else 0,
    onBackPress: (() -> Unit)? = null,
    onCityClick: ((SpotList) -> Unit)? = null,
    onSpotClick: ((SpotInfo) -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(id = R.string.title_section3)) },
                navigationIcon = {
                    IconButton(onClick = { onBackPress?.invoke() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (list.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                CityPane(
                    list = list,
                    onCityClick = { c -> onCityClick?.invoke(c) },
                    modifier = Modifier.fillMaxWidth(),
                    selected = selectedCity,
                )
                Spacer(modifier = Modifier.requiredHeight(8.dp))
                Separator()
                SpotPane(
                    list = list.find { c -> c.cityId == selectedCity } ?: SpotList(0),
                    onSpotClick = { spot -> onSpotClick?.invoke(spot) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview("list", showSystemUi = true)
@Preview("list night", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewDonationPlacePane() {
    AppTheme {
        SpotListUi(list = PreviewSample.spots)
    }
}

@Composable
private fun CityPane(
    list: List<SpotList>,
    onCityClick: (SpotList) -> Unit,
    modifier: Modifier,
    selected: Int = 0,
) {
    LazyRow(modifier = modifier.padding(horizontal = 16.dp)) {
        items(list) { city ->
            FilterChipImpl(
                content = city.cityName ?: city.cityId.toString(),
                onCheckedChanged = { checked ->
                    if (checked) onCityClick.invoke(city)
                },
                checked = selected == city.cityId,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}

@Composable
private fun FilterChipImpl(
    content: String,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
) {
    Button(
        onClick = { onCheckedChanged.invoke(!checked) },
        border = BorderStroke(
            if (checked) 0.dp else 1.dp,
            if (checked) Color.Transparent else MaterialTheme.colorScheme.outline,
        ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (checked) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (checked) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        ),
        modifier = modifier,
    ) {
        Text(content, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun SpotPane(list: SpotList, onSpotClick: (SpotInfo) -> Unit, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(list.locations) { spot ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.requiredWidth(16.dp))
                Text(
                    spot.spotName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                )
                IconButton(onClick = { onSpotClick.invoke(spot) }) {
                    Icon(
                        Icons.Rounded.OpenInBrowser,
                        contentDescription = stringResource(id = R.string.action_go_to_website),
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = .5f),
                    )
                }
            }
        }
    }
}
