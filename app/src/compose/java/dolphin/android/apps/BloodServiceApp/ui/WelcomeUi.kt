package dolphin.android.apps.BloodServiceApp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dolphin.android.apps.BloodServiceApp.R
import dolphin.android.apps.BloodServiceApp.pref.PrefsUtil
import dolphin.android.apps.BloodServiceApp.provider.BloodCenter

@Composable
fun WelcomeUi(
    list: List<BloodCenter.Center>,
    onComplete: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf(3) }

    Column(modifier = modifier.padding(vertical = 64.dp)) {
        WelcomeCenterSelectionUi(
            list = list,
            selected = selected,
            onSelectedChange = { selected = it },
            modifier = Modifier.weight(1f),
        )
        PrivacyPolicyPane(onAccept = { onComplete.invoke(selected) })
    }
}

@Composable
fun WelcomeCenterSelectionUi(
    list: List<BloodCenter.Center>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Text(
            stringResource(id = R.string.choose_near_by_blood_center),
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.requiredHeight(16.dp))
        list.forEachIndexed { index, center ->
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = index == selected,
                        onClick = { onSelectedChange.invoke(index) },
                    )
                    Spacer(modifier = Modifier.requiredWidth(8.dp))
                    Text(
                        center.name,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectedChange.invoke(index) },
                    )
                }
                Text(
                    center.cities,
                    modifier = Modifier.padding(start = 48.dp, end = 16.dp),
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyPane(onAccept: () -> Unit, modifier: Modifier = Modifier) {
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
                color = MaterialTheme.colors.primary,
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

    val context = LocalContext.current
    if (dialog) {
        val content = PrefsUtil.read_asset_text(context, "privacy_policy.txt", "UTF-8")

        AlertDialog(
            onDismissRequest = { dialog = false },
            buttons = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { dialog = false }) {
                        Text(stringResource(id = android.R.string.ok))
                    }
                }
            },
            text = {
                Text(content ?: "TODO")
            }
        )
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            annotatedString,
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures { offsetPosition ->
                        layoutResult.value?.let { textLayoutResult ->
                            val position = textLayoutResult.getOffsetForPosition(offsetPosition)
                            annotatedString
                                .getStringAnnotations(position, position)
                                .firstOrNull()
                                ?.let {
                                    dialog = true
                                }
                        }
                    }
                }
                .padding(16.dp),
            onTextLayout = { l -> layoutResult.value = l },
            fontSize = 12.sp,
        )
        // Spacer(modifier = Modifier.requiredHeight(8.dp))
        Button(onClick = onAccept, modifier = Modifier.requiredWidth(120.dp)) {
            Text(stringResource(id = R.string.splash_continue))
        }
    }
}

@Preview("Welcome", showSystemUi = true)
@Composable
private fun PreviewWelcomeUi() {
    AppTheme {
        WelcomeUi(
            list = listOf(
                BloodCenter.Center("Taipei"),
                BloodCenter.Center("Hsinchu"),
                BloodCenter.Center("Taichung"),
                BloodCenter.Center("Tainan"),
                BloodCenter.Center("Kaohsiung"),
            ),
            onComplete = {},
        )
    }
}
