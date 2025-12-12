package com.example.presentation.features.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.User
import com.example.domain.model.user.UserActivityLevel
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.ConfirmationDialog
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.InfoDialog
import com.example.presentation.common.ui.modifiers.shimmerEffect
import com.example.presentation.common.ui.values.FoodTrackTheme
import com.example.presentation.extensions.displayName
import com.example.presentation.features.main.profile.components.ProfileEditDialogs
import com.example.presentation.features.main.profile.models.ProfileEditDialogType
import com.example.presentation.features.main.profile.models.ProfileUiState
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun ProfileRoute(
    viewModel: ProfileVM = hiltViewModel(),
    onDeleteAccount: () -> Unit,
    onAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    ProfileScreen(
        uiState = uiState,
        baseUiState = baseUiState,
        onLogoutClick = viewModel::onLogoutClick,
        onLogoutConfirmation = viewModel::onLogoutConfirmation,
        onDeleteAccountClick = onDeleteAccount,
        onRetry = { viewModel.loadUserProfile() },
        onErrorConsume = viewModel::clearErrors,
        onEditClick = viewModel::onEditClick,
        onAboutClick = onAbout,
        onDismissDialog = viewModel::onDialogDismiss
    )

    ProfileEditDialogs(
        uiState = uiState,
        onDismiss = viewModel::onDialogDismiss,
        onSave = viewModel::saveDialogChanges,
        onGenderUpdate = viewModel::updateTempGender,
        onGoalUpdate = viewModel::updateTempGoal,
        onActivityLevelUpdate = viewModel::updateTempActivityLevel,
        onWeightChangeUpdate = viewModel::updateTempWeightChange,
        onCurrentWeightUpdate = viewModel::updateTempCurrentWeight,
        onHeightUpdate = viewModel::updateTempHeight,
        onBirthDateUpdate = viewModel::updateTempBirthDate,
        onCaloriesGoalUpdate = viewModel::updateTempCaloriesGoal,
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    baseUiState: BaseUiState,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLogoutConfirmation: (Boolean) -> Unit,
    onDeleteAccountClick: () -> Unit,
    onRetry: () -> Unit,
    onErrorConsume: () -> Unit,
    onEditClick: (ProfileEditDialogType) -> Unit,
    onDismissDialog: () -> Unit
) {
    val isLoading = baseUiState.isLoading
    val hasError = baseUiState.unexpectedError != null || baseUiState.isConnectionError || uiState.user==null

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item { CenterAlignedHeader(stringResource(R.string.profile)) }

            item {
                if (isLoading || hasError) ProfileUserCardShimmer()
                else ProfileUserCard(uiState.user)
                Spacer(Modifier.height(16.dp))
            }

            item {
                if (isLoading || hasError) UserGoalsSectionShimmer()
                else UserGoalsSection(user = uiState.user, onEditClick = onEditClick)

                Divider()
            }

            item {
                if (isLoading || hasError) UserInfoSectionShimmer()
                else UserInfoSection(uiState.user, onEditClick = onEditClick)

                Divider()
            }

            item {
                ProfileActionsSection(
                    onAboutClick = onAboutClick,
                    onLogoutClick = onLogoutClick,
                    onDeleteAccountClick = onDeleteAccountClick
                )
            }
        }

        InfoDialog(
            visible = uiState.showInfoDialog,
            title = stringResource(R.string.congratulations_goal_achieved),
            message = stringResource(R.string.you_ve_successfully_reached_your_target_weight),
            onConfirm = { onDismissDialog() },
        )

        ConfirmationDialog(
            visible = uiState.showLogoutDialog,
            title = stringResource(R.string.logout_title),
            message = stringResource(R.string.logout_message),
            confirmButtonText = stringResource(R.string.logout),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirm = { onLogoutConfirmation(true) },
            onDismiss = { onLogoutConfirmation(false) }
        )

        HandleError(baseUiState = baseUiState, onErrorConsume = {
            onRetry()
            onErrorConsume()
        }, onConnectionRetry = { onRetry() })
    }
}

@Composable
private fun Divider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
        thickness = 1.dp,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun ProfileUserCard(
    user: User,
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AsyncImage(
                model = user.photoUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = user.name!!,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = user.email!!,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            if (user.birthDate != null) {
                val age = Period.between(user.birthDate, LocalDate.now()).years
                Text(
                    text = context.resources.getQuantityString(R.plurals.years_old, age, age),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun ProfileUserCardShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun UserGoalsSection(
    user: User,
    onEditClick: (ProfileEditDialogType) -> Unit
) {
    Column {
        ProfileItem(
            title = stringResource(R.string.goal),
            value = user.goal.displayName(),
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.GOAL) }
        )

        ProfileItem(
            title = stringResource(R.string.calories),
            value = stringResource(
                R.string.calories_format,
                user.targetCalories),
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.CALORIES_GOAL) }
        )

        if (user.goal!=Goal.MAINTAIN)
            ProfileItem(
                title = stringResource(R.string.goal_weight),
                value = stringResource(
                    R.string.kg,
                    user.targetWeight?: user.currentWeight!!
                ),
                hasArrow = true,
                onClick = { onEditClick(ProfileEditDialogType.WEIGHT_CHANGE) }
            )
    }
}

@Composable
private fun UserGoalsSectionShimmer() {
    Column {
        repeat(3) {
            ProfileItemShimmer()
        }
    }
}

@Composable
private fun UserInfoSection(
    user: User,
    onEditClick: (ProfileEditDialogType) -> Unit
) {
    Column {
        ProfileItem(
            icon = painterResource(R.drawable.speedometer),
            title = stringResource(R.string.current_weight),
            value = stringResource(
                R.string.kg,
                user.currentWeight!!
            ),
            iconTint = MaterialTheme.colorScheme.primary,
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.CURRENT_WEIGHT) }
        )

        ProfileItem(
            icon = painterResource(R.drawable.ruler),
            title = stringResource(R.string.height),
            value = stringResource(R.string.cm, user.height ?: 170),
            iconTint = MaterialTheme.colorScheme.primary,
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.HEIGHT) }
        )

        ProfileItem(
            icon = painterResource(R.drawable.calendar_dots),
            title = stringResource(R.string.date_of_birth),
            value = user.birthDate!!.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            iconTint = MaterialTheme.colorScheme.primary,
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.DATE_OF_BIRTH) }
        )

        ProfileItem(
            icon = painterResource(R.drawable.gender_inter_sex),
            title = stringResource(R.string.gender),
            value = user.gender.displayName(),
            iconTint = MaterialTheme.colorScheme.primary,
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.GENDER) }
        )

        ProfileItem(
            icon = painterResource(R.drawable.person_simple),
            title = stringResource(R.string.activity_level),
            value = user.userActivityLevel.displayName(),
            iconTint = MaterialTheme.colorScheme.primary,
            hasArrow = true,
            onClick = { onEditClick(ProfileEditDialogType.ACTIVITY_LEVEL) }
        )
    }
}

@Composable
private fun UserInfoSectionShimmer() {
    Column{
        repeat(5) {
            ProfileItemShimmer()
        }
    }
}

@Composable
private fun ProfileActionsSection(
    onAboutClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {}
) {
    Column {
        ProfileItem(
            icon = painterResource(R.drawable.info),
            title = stringResource(R.string.about),
            onClick = onAboutClick,
            iconTint = MaterialTheme.colorScheme.onSurface
        )

        ProfileItem(
            icon = painterResource(R.drawable.sign_out),
            title = stringResource(R.string.logout),
            onClick = onLogoutClick,
            iconTint = MaterialTheme.colorScheme.onSurface
        )

        ProfileItem(
            icon = painterResource(R.drawable.trash),
            title = stringResource(R.string.delete_account),
            onClick = onDeleteAccountClick,
            iconTint = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ProfileItem(
    title: String,
    value: String = "",
    icon: Painter? = null,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    hasArrow: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title, style = MaterialTheme.typography.bodyMedium, color = textColor
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value, style = MaterialTheme.typography.bodyMedium, color = textColor
            )

            if (hasArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(R.drawable.arrow_right),
                    contentDescription = "Edit",
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .shimmerEffect(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {}
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val fakeUser = User(
        name = "Нагалка Анна",
        birthDate = LocalDate.of(2006, 12, 22),
        gender = Gender.MALE,
        goal = Goal.MAINTAIN,
        currentWeight = 75f,
        height = 180,
        targetCalories = 2200,
        userActivityLevel = UserActivityLevel.ACTIVE,
        photoUrl = null,
        targetWeight = 75f
    )

    val uiState = ProfileUiState(
        user = fakeUser,
        showLogoutDialog = false
    )

    val baseUiState = BaseUiState(
        isLoading = false,
        unexpectedError = null,
        isConnectionError = false
    )

    FoodTrackTheme {
        ProfileScreen(
            uiState = uiState,
            baseUiState = baseUiState,
            onLogoutClick = {},
            onLogoutConfirmation = {},
            onDeleteAccountClick = {},
            onRetry = {},
            onErrorConsume = {},
            onEditClick = {},
            onAboutClick = {},
            onDismissDialog = {}
        )
    }
}
