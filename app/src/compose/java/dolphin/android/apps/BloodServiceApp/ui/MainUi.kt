package dolphin.android.apps.BloodServiceApp.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.apps.BloodServiceApp.provider.DonateActivity
import dolphin.android.apps.BloodServiceApp.provider.DonateDay

/**
 * Main UI callbacks
 */
interface MainUiCallback {
    /**
     * Change to another blood center.
     *
     * @param center new blood center
     */
    fun changeBloodCenter(center: BloodCenter.Center)

    /**
     * Show Facebook Pages in browser.
     *
     * @param center target blood center
     */
    fun showFacebookPages(center: BloodCenter.Center)

    /**
     * Show donation spot list in cities.
     *
     * @param center target blood center
     */
    fun showSpotList(center: BloodCenter.Center)

    /**
     * Add donation event to phone calendar.
     *
     * @param event donation event
     */
    fun addToCalendar(event: DonateActivity)

    /**
     * Search location on Google Maps.
     *
     * @param event donation event
     */
    fun searchOnMaps(event: DonateActivity)

    /**
     * Show donor info web page in a browser.
     */
    fun showDonorInfo()

    /**
     * Enable or disable search on maps.
     *
     * @return true if enable this feature
     */
    fun enableSearchOnMap(): Boolean

    /**
     * Enable or disable add to phone calendar.
     *
     * @return true if enable this feature
     */
    fun enableAddToCalendar(): Boolean

    /**
     * Enable or disable review policy in main ui.
     *
     * @return true if enable this feature
     */
    fun showReviewPolicy(): Boolean
}

/**
 * Callback alias
 */
typealias BloodCenterCallback = (BloodCenter.Center) -> Unit

/**
 * Main UI in Compose way.
 */
@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Composable
fun MainUi(
    centers: List<BloodCenter.Center>,
    selected: BloodCenter.Center,
    modifier: Modifier = Modifier,
    onCenterChange: BloodCenterCallback? = null,
    onFacebookClick: BloodCenterCallback? = null,
    onReviewSource: BloodCenterCallback? = null,
    onSpotListClick: BloodCenterCallback? = null,
    daysList: List<DonateDay> = ArrayList(),
    storageMap: HashMap<String, Int> = HashMap(),
    enableAddCalendar: Boolean = true,
    onAddCalendar: ((DonateActivity) -> Unit)? = null,
    enableSearchOnMap: Boolean = true,
    onSearchOnMap: ((DonateActivity) -> Unit)? = null,
    onDonorClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    showReviewPolicy: Boolean = false,
    onReviewPolicy: (() -> Unit)? = null,
    onReviewIgnore: (() -> Unit)? = null,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(selected.name)
                },
                actions = {
                    IconButton(onClick = { onFacebookClick?.invoke(selected) }) {
                        Image(
                            painterResource(id = R.drawable.ic_action_facebook),
                            contentDescription = stringResource(id = R.string.action_go_to_facebook),
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 4.dp)
            ) {
                TextButton(
                    onClick = { onDonorClick?.invoke() },
                    modifier = Modifier.padding(horizontal = 8.dp),
                ) {
//                    Icon(
//                        Icons.Rounded.OpenInBrowser,
//                        contentDescription = stringResource(id = R.string.action_go_to_personal_summary),
//                    )
                    Text(stringResource(id = R.string.action_go_to_personal))
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onSettingsClick?.invoke() }) {
                    Icon(
                        Icons.Rounded.Settings,
                        contentDescription = stringResource(id = R.string.title_activity_settings),
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (showReviewPolicy) {
                PrivacyReviewSnackbar(
                    modifier = Modifier.fillMaxWidth(),
                    onReviewPolicy = onReviewPolicy,
                    onReviewIgnore = onReviewIgnore,
                )
            }
            StoragePane(
                map = storageMap,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp),
            )

            Separator()
            DayListPane(
                daysList = daysList,
                modifier = Modifier.weight(1f),
                showAddCalendar = enableAddCalendar,
                onAddCalendar = onAddCalendar,
                showSearchOnMap = enableSearchOnMap,
                onSearchOnMap = onSearchOnMap,
                onReviewSource = { onReviewSource?.invoke(selected) },
            )

            Separator()
            Row(
                modifier = Modifier
                    .clickable { onSpotListClick?.invoke(selected) }
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(id = R.string.section3_summary),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
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

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Preview("Main Ui", showSystemUi = true)
@Preview("Main Ui Night", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMainUi() {
    AppTheme {
        MainUi(
            centers = PreviewSample.centers,
            selected = PreviewSample.selectedCenter,
            daysList = PreviewSample.donations,
            storageMap = PreviewSample.storage(3, 2, 1, 3),
        )
    }
}

@ExperimentalMaterial3Api
@ExperimentalFoundationApi
@Preview("Main Ui: hide", showSystemUi = true)
@Preview("Main Ui Night", showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewMainUi2() {
    AppTheme {
        MainUi(
            centers = PreviewSample.centers,
            selected = PreviewSample.selectedCenter,
            daysList = PreviewSample.donations,
            storageMap = PreviewSample.storage(0, 1, 2, 3),
            enableAddCalendar = false,
            enableSearchOnMap = false,
            showReviewPolicy = true,
        )
    }
}

@Composable
private fun PrivacyReviewSnackbar(
    modifier: Modifier = Modifier,
    onReviewPolicy: (() -> Unit)? = null,
    onReviewIgnore: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.snackbar_privacy_policy_updated),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        TextButton(onClick = { onReviewIgnore?.invoke() }) {
            Text(stringResource(id = R.string.snackbar_privacy_policy_ignore))
        }
        TextButton(
            onClick = { onReviewPolicy?.invoke() },
        ) {
            Text(stringResource(id = R.string.snackbar_privacy_policy_review))
        }
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
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .height(32.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(content, style = MaterialTheme.typography.labelLarge)
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
    // add semantics to icon
    val statusMap = stringArrayResource(id = R.array.blood_storage_status)
    val typeMap = stringArrayResource(id = R.array.blood_type)

    Row(
        modifier = modifier.padding(top = 4.dp, bottom = 4.dp),
        // horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            stringResource(id = R.string.section1_summary),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.requiredWidth(8.dp))

        arrayOf("A", "B", "O", "AB").forEachIndexed { index, type ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .background(color = colorMap[map[type] ?: 0], shape = CircleShape)
                    .requiredSize(32.dp)
                    .semantics(mergeDescendants = true) {
                        stateDescription = typeMap[index] + statusMap[map[type] ?: 0]
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    type,
                    modifier = Modifier.clearAndSetSemantics { },
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(alpha = .95f),
                    fontWeight = FontWeight.Bold,
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
private fun DayListPane(
    daysList: List<DonateDay>,
    modifier: Modifier = Modifier,
    showAddCalendar: Boolean = true,
    onAddCalendar: ((DonateActivity) -> Unit)? = null,
    showSearchOnMap: Boolean = true,
    onSearchOnMap: ((DonateActivity) -> Unit)? = null,
    onReviewSource: (() -> Unit)? = null,
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(id = R.string.section2_summary),
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .weight(1f),
                style = MaterialTheme.typography.titleLarge,
            )
            // https://foso.github.io/Jetpack-Compose-Playground/material/dropdownmenu/
            Box(modifier = Modifier.wrapContentSize()) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        Icons.Rounded.MoreVert,
                        contentDescription = stringResource(id = R.string.action_more),
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                ) {
                    DropdownMenuItem(
                        onClick = {
                            showMenu = false
                            onReviewSource?.invoke()
                        },
                    ) {
                        Text(stringResource(id = R.string.action_go_to_website))
                    }
                }
            }
        }

        if (daysList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            DonationPane(
                donations = daysList,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                showAddCalendar, onAddCalendar, showSearchOnMap, onSearchOnMap,
            )
        }
    }
}

@Composable
private fun DonationPane(
    donations: List<DonateDay>,
    modifier: Modifier = Modifier,
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
                    style = MaterialTheme.typography.bodyMedium,
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
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = .1f))
            .padding(start = 16.dp, top = 12.dp, bottom = 8.dp, end = 16.dp),
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
    val buttonColor = MaterialTheme.colorScheme.secondary.copy(alpha = .5f)
    // See https://developer.android.com/jetpack/compose/accessibility
    val addLabel = stringResource(id = R.string.action_add_to_calendar)
    val searchLabel = stringResource(id = R.string.action_search_location)
    val timeToLabel = stringResource(id = R.string.label_time_to)

    Row(
        modifier = modifier
            .padding(vertical = 6.dp)
            .semantics(mergeDescendants = true) {
                customActions = listOf(
                    CustomAccessibilityAction(addLabel) {
                        if (showAddCalendar) onAddCalendar?.invoke(event)
                        showAddCalendar
                    },
                    CustomAccessibilityAction(searchLabel) {
                        if (showSearchOnMap) onSearchOnMap?.invoke(event)
                        showSearchOnMap
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showAddCalendar) {
            Spacer(modifier = Modifier.requiredWidth(4.dp))
            IconButton(
                onClick = { onAddCalendar?.invoke(event) },
                modifier = Modifier.clearAndSetSemantics { }, // merge to Row
            ) {
                Icon(
                    Icons.Rounded.Schedule,
                    contentDescription = addLabel,
                    tint = buttonColor,
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(28.dp))
        }
        Column {
            Spacer(modifier = Modifier.requiredHeight(2.dp))
            Text(
                event.startTimeString,
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(
                modifier = Modifier
                    .requiredHeight(2.dp)
                    .semantics { stateDescription = timeToLabel }, // add hidden semantics
            )
            Text(
                event.endTimeString,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        Spacer(modifier = Modifier.requiredWidth(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                event.name,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.requiredHeight(4.dp))
            Text(
                event.location,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
        if (showSearchOnMap) {
            IconButton(
                onClick = { onSearchOnMap?.invoke(event) },
                modifier = Modifier.clearAndSetSemantics { }, // merge to Row
            ) {
                Icon(
                    Icons.Rounded.Map,
                    contentDescription = searchLabel,
                    tint = buttonColor,
                )
            }
        } else {
            Spacer(modifier = Modifier.requiredWidth(12.dp))
        }
    }
}
