package com.example.choreharmony.views.assets

import NavigationItem
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun ChoreHarmonyNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Chat,
        NavigationItem.Roommates,
        NavigationItem.Settings
    )

    NavigationBar {
        items.forEach { item ->
            AddItem(
                screen = item,
                name = item.title,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: NavigationItem,
    name: String,
    navController: NavController
) {
    NavigationBarItem(
        label = {
            Text(text = screen.title)
        },
        icon = {
            Icon(
                screen.icon,
                contentDescription = screen.title
            )
        },
        selected = true,
        alwaysShowLabel = true,
        onClick = {
            if (name == "Home") {
                navController.navigate("home") {
                    popUpTo(navController.graph.id)
                }
            }
            if (name == "Chat") {
                navController.navigate("chat") {
                    popUpTo(navController.graph.id)
                }
            }
            if (name == "Roommates") {
                navController.navigate("roommates") {
                    popUpTo(navController.graph.id)
                }
            }
            if (name == "Settings") {
                navController.navigate("settings") {
                    popUpTo(navController.graph.id)
                }
            }
        }
    )
}
