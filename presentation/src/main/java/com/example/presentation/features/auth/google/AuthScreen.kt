package com.example.presentation.features.auth.google

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.Text
import com.example.presentation.features.main.MainVM

@Composable
fun AuthRoute(
    viewModel: AuthVM = hiltViewModel(),
    mainVM: MainVM = hiltViewModel(),
    onNewSignIn: () -> Unit,
) {
    val context = LocalContext.current
    AuthScreen {
        viewModel.signInWithGoogle(context, mainVM, false, onNewSignIn)
    }
}

@Composable
fun AuthScreen(
    onLoginClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Wellcome!", color = Color.Black)
        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = {
                onLoginClicked()
            },
        ) {
            Text("Login with Google")
        }
    }
}

@Preview
@Composable
private fun AuthScreenPreview(){
    AuthScreen{}
}