package com.example.presentation.features.main.profile.about

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.common.ui.components.LeftAlignedHeader
import com.example.presentation.common.ui.values.FoodTrackTheme

@Composable
fun AboutRoute(
    onBackPressed: () -> Unit
) {
    AboutScreen(
        onBackPressed = onBackPressed
    )
}

@Composable
fun AboutScreen(
    onBackPressed: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    val packageInfo = try{
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    val versionName = packageInfo?.versionName ?: "1.0.0"
    val versionCode = packageInfo?.longVersionCode ?: 1

    val termsUrl = stringResource(R.string.terms_url)
    val privacyUrl = stringResource(R.string.privacy_url)
    val supportEmail = stringResource(R.string.support_email)

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        topBar = {
            LeftAlignedHeader(
                title = stringResource(R.string.about),
                onNavigateBack = onBackPressed
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            AboutItem(
                icon = R.drawable.mail,
                text = stringResource(R.string.contact_us),
                onClick = { uriHandler.openUri(supportEmail) }
            )

            AboutItem(
                icon = R.drawable.doc,
                text = stringResource(R.string.terms_of_service),
                onClick = { uriHandler.openUri(termsUrl) }
            )

            AboutItem(
                icon = R.drawable.doc,
                text = stringResource(R.string.privacy_policy),
                onClick = { uriHandler.openUri(privacyUrl) }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
                thickness = 1.dp,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 24.dp)
                    .fillMaxWidth()
            )

            AppVersionInfo(
                versionName = versionName,
                versionCode = versionCode
            )
        }
    }
}

@Composable
fun AppVersionInfo(
    versionName: String,
    versionCode: Long
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.check_verified),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FoodSnap AI",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$versionName ($versionCode)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun AboutItem(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DeleteAccountScreenPreview (){
    FoodTrackTheme {
        AboutScreen{}
    }
}