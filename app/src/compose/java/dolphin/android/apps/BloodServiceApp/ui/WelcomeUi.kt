package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter
import dolphin.android.util.DebugOnlyNoCoverage

/**
 * Welcome UI callbacks
 */
interface WelcomeUiCallback {
    /**
     * Review privacy policy.
     */
    fun reviewPrivacy()

    /**
     * Review and accept privacy policy.
     *
     * @param center selected blood center
     */
    fun reviewComplete(center: BloodCenter.Center)
}

/**
 * Welcome UI in Compose way.
 */
@ExperimentalMaterial3Api
@Composable
fun WelcomeUi(
    list: List<BloodCenter.Center>,
    onComplete: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSource: (() -> Unit)? = null,
    onReview: (() -> Unit)? = null,
) {
    var selected by remember { mutableStateOf(3) }

    Scaffold(
        modifier = modifier,
        topBar = {
            SmallTopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                navigationIcon = {
                    Image(
                        painterResource(id = R.mipmap.ic_adaptive_launcher_fg),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            WelcomeText(onClick = onSource)
            WelcomeCenterSelectionUi(
                list = list,
                selected = selected,
                onSelectedChange = { selected = it },
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            PrivacyPolicyPane(
                onAccept = { onComplete.invoke(selected) },
                onReview = onReview,
            )
            Spacer(modifier = Modifier.requiredHeight(32.dp))
        }
    }
}

@Composable
private fun WelcomeText(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val layoutResult = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    val annotatedString = buildAnnotatedString {
        val text = stringResource(id = R.string.app_intro)
        val startIndex = text.indexOf("[")
        val endIndex = text.indexOf("]")
        append(text.substring(0, startIndex))
        pushStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary,
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Bold,
            )
        )
        append(text.substring(startIndex + 1, endIndex))
        pop()
        append(text.substring(endIndex + 1))
        addStringAnnotation(
            tag = "URL",
            annotation = "review",
            start = startIndex,
            end = endIndex,
        )
    }

    Text(
        annotatedString,
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .pointerInput(Unit) {
                detectTapGestures { offsetPosition ->
                    layoutResult.value?.let { textLayoutResult ->
                        val position = textLayoutResult.getOffsetForPosition(offsetPosition)
                        annotatedString
                            .getStringAnnotations(position, position)
                            .firstOrNull()
                            ?.let { onClick?.invoke() }
                    }
                }
            },
        onTextLayout = { l -> layoutResult.value = l },
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun WelcomeCenterSelectionUi(
    list: List<BloodCenter.Center>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(4.dp),
    ) {
        Text(
            stringResource(id = R.string.choose_near_by_blood_center),
            style = MaterialTheme.typography.bodyMedium,
            // fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        list.forEachIndexed { index, center ->
            Row(
                modifier = Modifier
                    .clickable { onSelectedChange.invoke(index) }
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (index == selected) {
                    Icon(
                        Icons.Rounded.Check,
                        contentDescription = stringResource(id = R.string.title_display),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                } else {
                    Spacer(modifier = Modifier.requiredSize(24.dp))
                }
                Spacer(modifier = Modifier.requiredWidth(8.dp))
                Column {
                    Text(
                        center.name,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Text(
                        center.cities,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            if (index != list.lastIndex) Separator(horizontalPadding = 0.dp)
        }
    }
}

@Composable
private fun PrivacyPolicyPane(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
    onReview: (() -> Unit)? = null,
) {
    var dialog by remember { mutableStateOf(false) }
    val layoutResult = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }
    val annotatedString = buildAnnotatedString {
        val text = stringResource(R.string.splash_privacy_policy_review)
        val target = stringResource(R.string.app_privacy_policy)
        val startIndex = text.indexOf(target)
        append(text.substring(0, startIndex))
        pushStyle(
            style = SpanStyle(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.secondary,
            )
        )
        append(target)
        pop()
        append(text.substring(startIndex + target.length))
        addStringAnnotation(
            tag = "URL",
            annotation = "review",
            start = startIndex,
            end = startIndex + target.length,
        )
    }

    ShowAssetContentDialog(asset = "privacy_policy.txt", visible = dialog) {
        dialog = false
    }

    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            annotatedString,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { offsetPosition ->
                    layoutResult.value?.let { textLayoutResult ->
                        val position = textLayoutResult.getOffsetForPosition(offsetPosition)
                        annotatedString
                            .getStringAnnotations(position, position)
                            .firstOrNull()
                            ?.let {
                                onReview?.invoke() ?: kotlin.run { dialog = true }
                            }
                    }
                }
            },
            onTextLayout = { l -> layoutResult.value = l },
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))
        Button(onClick = onAccept, modifier = Modifier.requiredWidth(120.dp)) {
            Text(stringResource(id = R.string.splash_continue))
        }
    }
}

@DebugOnlyNoCoverage
@ExperimentalMaterial3Api
@Preview("Welcome", showSystemUi = true)
@Composable
private fun PreviewWelcomeUi() {
    AppTheme {
        WelcomeUi(
            list = PreviewSample.centers,
            onComplete = {},
        )
    }
}
