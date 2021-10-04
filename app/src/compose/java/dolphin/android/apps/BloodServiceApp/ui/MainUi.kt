package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.OpenInBrowser
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay

@ExperimentalFoundationApi
@Composable
fun MainUi(
    centers: List<BloodCenter.Center>,
    selected: BloodCenter.Center,
    modifier: Modifier = Modifier,
    onCenterChange: ((BloodCenter.Center) -> Unit)? = null,
    onFacebookClick: ((BloodCenter.Center) -> Unit)? = null,
    onMobileSiteClick: ((BloodCenter.Center) -> Unit)? = null,
    onStationsClick: ((BloodCenter.Center) -> Unit)? = null,
    donations: List<DonateDay> = ArrayList(),
    storage: HashMap<String, Int> = HashMap(),
    showAddCalendar: Boolean = true,
    onAddCalendar: ((DonateActivity) -> Unit)? = null,
    showSearchOnMap: Boolean = true,
    onSearchOnMap: ((DonateActivity) -> Unit)? = null,
    onDonorClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar {
                Text(
                    selected.name,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                )

                IconButton(onClick = { onFacebookClick?.invoke(selected) }) {
                    Image(
                        painterResource(id = R.drawable.ic_action_facebook),
                        contentDescription = stringResource(id = R.string.action_go_to_facebook),
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar {
                TextButton(
                    onClick = { onDonorClick?.invoke() },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.onPrimary,
                    )
                ) {
                    Icon(
                        Icons.Rounded.OpenInBrowser,
                        contentDescription = stringResource(id = R.string.action_go_to_personal_summary),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                    Text(stringResource(id = R.string.action_go_to_personal))
                }
//                Text(
//                    stringResource(id = R.string.action_go_to_personal_summary),
//                    fontSize = 12.sp,
//                    modifier = Modifier.weight(1f),
//                    color = MaterialTheme.colors.onPrimary
//                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onSettingsClick?.invoke() }) {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = stringResource(id = R.string.title_activity_settings),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            StoragePane(
                map = storage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
            )

            Separator()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.section2_summary),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .weight(1f),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                TextButton(onClick = { onMobileSiteClick?.invoke(selected) }) {
                    Icon(
                        Icons.Rounded.OpenInBrowser,
                        contentDescription = stringResource(id = R.string.action_go_to_website),
                    )
                    Text(stringResource(id = R.string.action_go_to_website))
                }
                Spacer(modifier = Modifier.requiredWidth(4.dp))
            }
            if (donations.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                DonationPane(
                    donations = donations,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    showAddCalendar, onAddCalendar, showSearchOnMap, onSearchOnMap,
                )
            }

            Separator()
            Row(
                modifier = Modifier
                    .clickable { onStationsClick?.invoke(selected) }
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.section3_summary),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.LightGray,
                )
            }

            Separator()
            SwitchCenterPane(
                list = centers,
                selected = selected,
                modifier = Modifier.fillMaxWidth(),
                onCenterChange = { center -> onCenterChange?.invoke(center) },
            )
        }
    }

}


@ExperimentalFoundationApi
@Preview("Main Ui", showSystemUi = true)
@Composable
private fun PreviewMainUi() {
    AppTheme {
        MainUi(
            centers = PreviewSample.centers,
            selected = PreviewSample.selectedCenter,
            donations = PreviewSample.donations,
            storage = PreviewSample.storage(3, 2, 1, 3),
        )
    }
}

@ExperimentalFoundationApi
@Preview("Main Ui: hide", showSystemUi = true)
@Composable
private fun PreviewMainUi2() {
    AppTheme {
        MainUi(
            centers = PreviewSample.centers,
            selected = PreviewSample.selectedCenter,
            donations = PreviewSample.donations,
            storage = PreviewSample.storage(0, 1, 2, 3),
            showAddCalendar = false,
            showSearchOnMap = false,
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun SwitchCenterPane(
    list: List<BloodCenter.Center>,
    selected: BloodCenter.Center,
    modifier: Modifier = Modifier,
    onCenterChange: ((BloodCenter.Center) -> Unit)? = null,
) {
    val selection = list.filter { c -> c.id != selected.id }

    LazyRow(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        stickyHeader {
            Text(
                stringResource(id = R.string.section0_summary),
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 16.dp), // , end = 4.dp
                color = MaterialTheme.colors.onSurface,
            )
        }
        items(selection) { center ->
            TextButton(onClick = { onCenterChange?.invoke(center) }) {
                Text(center.name)
            }
        }
    }
}

@Composable
private fun StoragePane(
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

    Row(
        modifier = modifier,
        // horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.section1_summary),
            // modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.requiredWidth(8.dp))

        arrayOf("A", "B", "O", "AB").forEach { type ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .background(color = colorMap[map[type] ?: 0], shape = CircleShape)
                    .requiredSize(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    type,
                    // fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(alpha = .95f),
                )
            }
        }
        onToolTip?.let { callback ->
            IconButton(onClick = callback) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}

@Composable
private fun DonationPane(
    donations: List<DonateDay>, modifier: Modifier = Modifier,
    showAddCalendar: Boolean = true,
    onAddCalendar: ((DonateActivity) -> Unit)? = null,
    showSearchOnMap: Boolean = true,
    onSearchOnMap: ((DonateActivity) -> Unit)? = null,
) {
    val list = ArrayList<Any>()
    donations.forEach { day ->
        list.add(day)
        if (day.activityCount <= 0) {
            list.add(0)
        } else {
            day.activities.forEach { event ->
                list.add(event)
            }
        }
    }

    LazyColumn(modifier = modifier) {
        items(list) { item ->
            (item as? DonateDay)?.let { day ->
                DayPane(day, modifier = Modifier.fillMaxWidth())
            }
            (item as? DonateActivity)?.let { event ->
                EventPane(
                    event = event,
                    modifier = Modifier.fillMaxWidth(),
                    showAddCalendar = showAddCalendar,
                    onAddCalendar = onAddCalendar,
                    showSearchOnMap = showSearchOnMap,
                    onSearchOnMap = onSearchOnMap,
                )
            }
            (item as? Int)?.let {
                Text(
                    stringResource(id = R.string.title_data_not_available),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
private fun DayPane(day: DonateDay, modifier: Modifier = Modifier) {
    Text(
        day.dateString,
        modifier = modifier
            .background(MaterialTheme.colors.secondary.copy(alpha = .1f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun EventPane(
    event: DonateActivity,
    modifier: Modifier = Modifier,
    showAddCalendar: Boolean = true,
    onAddCalendar: ((DonateActivity) -> Unit)? = null,
    showSearchOnMap: Boolean = true,
    onSearchOnMap: ((DonateActivity) -> Unit)? = null,
) {
    val buttonColor = MaterialTheme.colors.secondary.copy(alpha = .5f)
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (showAddCalendar) {
            Spacer(modifier = Modifier.requiredWidth(4.dp))
            IconButton(onClick = { onAddCalendar?.invoke(event) }) {
                Icon(
                    Icons.Rounded.Schedule,
                    contentDescription = stringResource(id = R.string.action_add_to_calendar),
                    tint = buttonColor,
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(28.dp))
        }
        Column {
            Text(event.startTimeString, style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.requiredHeight(4.dp))
            Text(event.endTimeString, style = MaterialTheme.typography.caption)
        }
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(event.name, style = MaterialTheme.typography.body1)
            Text(event.location, style = MaterialTheme.typography.body2)
        }
        if (showSearchOnMap) {
            IconButton(onClick = { onSearchOnMap?.invoke(event) }) {
                Icon(
                    Icons.Rounded.Map,
                    contentDescription = stringResource(id = R.string.action_search_location),
                    tint = buttonColor,
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(12.dp))
        }
    }
}
