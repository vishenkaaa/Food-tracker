package com.example.presentation.features.main.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.presentation.common.ui.values.White
import com.example.presentation.common.ui.values.White50
import com.example.presentation.extensions.toIcon
import com.example.presentation.extensions.toTitle

@Composable
fun AppNavigationBar(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 6.dp, bottom = 32.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp),
                clip = false,
                ambientColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopLevelDestinations.entries.forEach { screen ->
            val isSelected = currentDestination?.route == screen.route::class.qualifiedName

            CustomNavigationBarItem(
                selected = isSelected,
                tab = screen,
                modifier = Modifier.weight(1f)
            ) {
                navController.navigate(screen.route)
            }
        }
    }
}

@Composable
fun CustomNavigationBarItem(
    selected: Boolean,
    tab: TopLevelDestinations,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable { onClick() }
    ) {
        Icon(
            painter = tab.toIcon(),
            modifier = Modifier.size(28.dp),
            contentDescription = tab.contentDescription,
            tint = if (selected) White else White50,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = tab.toTitle(),
            color = if (selected) White else White50,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp)
        )
    }
}