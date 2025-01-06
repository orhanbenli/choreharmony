import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    var title: String,
    var icon: ImageVector
) {
    data object Home :
        NavigationItem(
            "Home",
            Icons.Filled.Home
        )

    data object Chat :
        NavigationItem(
            "Chat",
            Icons.Filled.ChatBubble
        )

    data object Roommates :
        NavigationItem(
            "Roommates",
            Icons.Filled.Groups
        )

    data object Settings :
        NavigationItem(
            "Settings",
            Icons.Filled.Settings
        )
}
