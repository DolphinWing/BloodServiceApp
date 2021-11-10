package dolphin.android.tests

import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText

/**
 * helper method to quick access to [onNodeWithTag] with unmerged tree.
 *
 * @param tag test tag
 */
fun ComposeContentTestRule.tag(tag: String): SemanticsNodeInteraction =
    onNodeWithTag(tag, useUnmergedTree = true)

/**
 * helper method to quick access to [onNodeWithText] with unmerged tree.
 *
 * @param text content text
 */
fun ComposeContentTestRule.text(text: String): SemanticsNodeInteraction =
    onNodeWithText(text, useUnmergedTree = true)

/**
 *  helper method to quick assert if a node with text is displayed.
 *
 *  @param text content text
 */
fun ComposeContentTestRule.assertTextDisplayed(text: String) {
    text(text = text).assertIsDisplayed()
}

/**
 * helper method to find all tagged nodes
 *
 * @param tag test tag
 * @return node list with test tag
 */
fun ComposeContentTestRule.findAll(tag: String): List<SemanticsNode> = try {
    onAllNodesWithTag(tag, true).fetchSemanticsNodes(false)
} catch (e: Exception) {
    emptyList()
}
