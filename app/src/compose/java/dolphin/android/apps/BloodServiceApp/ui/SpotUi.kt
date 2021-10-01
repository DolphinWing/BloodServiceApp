package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.SpotInfo
import dolphin.android.apps.BloodServiceApp.provider.SpotList

@Composable
fun SpotUi(
    selected: BloodCenter.Center,
    list: List<SpotList>,
    modifier: Modifier = Modifier,
    onBackPress: (() -> Unit)? = null,
    onSpotClick: ((SpotInfo) -> Unit)? = null,
) {
    var city by remember { mutableStateOf(if (list.isNotEmpty()) list.first().cityId else 0) }

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
                    stringResource(id = R.string.title_section3),
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        },
    ) { padding ->
        Column(modifier = modifier.padding(padding)) {
            if (list.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                CityPane(
                    list = list,
                    onCityClick = { c -> city = c.cityId },
                    modifier = Modifier.fillMaxWidth(),
                    selected = city,
                )
                Separator(modifier = Modifier.fillMaxWidth())
                PlacePane(
                    list = list.find { c -> c.cityId == city } ?: SpotList(0),
                    onSpotClick = { spot -> onSpotClick?.invoke(spot) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Preview("list", showSystemUi = true)
@Composable
private fun PreviewDonationPlacePane() {
    AppTheme {
        SpotUi(
            selected = PreviewSample.selectedCenter,
            list = PreviewSample.spots,
        )
    }
}

@Composable
private fun CityPane(
    list: List<SpotList>,
    onCityClick: (SpotList) -> Unit,
    modifier: Modifier,
    selected: Int = 0,
) {
    val primaryColor = MaterialTheme.colors.primary
    val secondaryColor = primaryColor.copy(alpha = .8f)

    LazyRow(modifier = modifier.padding(8.dp)) {
        items(list) { city ->
            TextButton(
                onClick = { onCityClick.invoke(city) },
                border = BorderStroke(
                    1.dp,
                    if (selected == city.cityId) primaryColor else Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (selected == city.cityId) primaryColor else secondaryColor
                )
            ) {
                Text(
                    city.cityName ?: city.cityId.toString(),
                )
            }
        }
    }
}

@Composable
private fun PlacePane(list: SpotList, onSpotClick: (SpotInfo) -> Unit, modifier: Modifier) {
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
                )
                IconButton(onClick = { onSpotClick.invoke(spot) }) {
                    Icon(
                        Icons.Rounded.OpenInBrowser,
                        contentDescription = stringResource(id = R.string.action_go_to_website),
                        tint = Color.LightGray,
                    )
                }
            }
        }
    }
}
