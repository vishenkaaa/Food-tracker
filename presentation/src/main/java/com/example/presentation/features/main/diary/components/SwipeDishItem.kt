package com.example.presentation.features.main.diary.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.domain.model.diary.Dish
import com.example.presentation.R
import com.example.presentation.common.ui.modifiers.softShadow

@Composable
fun SwipeDishItem(
    dish: Dish,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onRemove()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            SwipeToDismissBoxValue.StartToEnd -> {
                onEdit()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = Modifier.softShadow(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f),
            blurRadius = 12.dp,
            offsetY = 1.dp,
            offsetX = 1.dp,
            cornerRadius = 16.dp
        ),
        backgroundContent = { SwipeBackground(dismissState = dismissState) },
        content = {
            DishCard(dish)
        }
    )
}

@Composable
private fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val alignment = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
        else -> Alignment.Center
    }

    val icon = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> painterResource(R.drawable.pencil)
        SwipeToDismissBoxValue.EndToStart -> painterResource(R.drawable.trash)
        else -> null
    }

    val scale by animateFloatAsState(
        targetValue = if (
            dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd ||
            dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart
        ) 1.3f else 1f,
        animationSpec = tween(400)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 24.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                painter = it,
                contentDescription = "Action",
                tint = Color.Unspecified,
                modifier = Modifier
                    .scale(scale)
                    .size(24.dp)
            )
        }
    }
}