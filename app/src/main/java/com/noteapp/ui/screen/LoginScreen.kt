
package com.noteapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noteapp.ui.theme.BlackGoldColors
import com.noteapp.ui.theme.CustomShapes
import com.noteapp.viewmodel.LoginState
import com.noteapp.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    val loginState by viewModel.loginState.collectAsState()

    // Animation states
    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "login_animation"
    )

    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlackGoldColors.DeepBlack,
                        BlackGoldColors.CharcoalBlack,
                        BlackGoldColors.DeepBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo/Title with animation
            AnimatedVisibility(
                visible = animationStarted,
                enter = slideInVertically(
                    initialOffsetY = { -300 },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 48.dp)
                ) {
                    Text(
                        text = "✨Goldy Notes✨",
                        style = MaterialTheme.typography.displayMedium,
                        color = BlackGoldColors.Gold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your thoughts, secured in gold",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackGoldColors.LightGold.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Login Form Card
            AnimatedVisibility(
                visible = animationStarted,
                enter = slideInVertically(
                    initialOffsetY = { 300 },
                    animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(800, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(animatedProgress.coerceAtLeast(0.8f)),
                    shape = CustomShapes.cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = BlackGoldColors.CharcoalBlack.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    LoginContent(
                        onLoginClick = { username, password ->
                            viewModel.login(username, password)
                        },
                        onSignUpClick = onSignUpClick,
                        modifier = Modifier
                            .padding(32.dp)
                            .alpha(animatedProgress)
                    )
                }
            }
        }

        // Loading, Success, and Error states
        when (val state = loginState) {
            is LoginState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BlackGoldColors.DeepBlack.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = CustomShapes.cardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = BlackGoldColors.CharcoalBlack
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = BlackGoldColors.Gold,
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Signing you in...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = BlackGoldColors.LightGold
                            )
                        }
                    }
                }
            }
            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
            }
            is LoginState.Error -> {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(
                        initialOffsetY = { 100 },
                        animationSpec = tween(300)
                    ) + fadeIn(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Card(
                        shape = CustomShapes.cardShape,
                        colors = CardDefaults.cardColors(
                            containerColor = BlackGoldColors.ErrorRed.copy(alpha = 0.9f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = BlackGoldColors.White,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = state.message,
                                color = BlackGoldColors.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            color = BlackGoldColors.Gold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyLarge,
            color = BlackGoldColors.LightGold.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username Field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    "Email",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Email",
                    tint = BlackGoldColors.Gold
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = CustomShapes.textFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlackGoldColors.Gold,
                unfocusedBorderColor = BlackGoldColors.MediumGray,
                focusedTextColor = BlackGoldColors.LightGold,
                unfocusedTextColor = BlackGoldColors.LightGold,
                cursorColor = BlackGoldColors.Gold
            )
        )

        // Password Field with Visibility Toggle
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Password",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Password",
                    tint = BlackGoldColors.Gold
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = BlackGoldColors.Gold
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = CustomShapes.textFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlackGoldColors.Gold,
                unfocusedBorderColor = BlackGoldColors.MediumGray,
                focusedTextColor = BlackGoldColors.LightGold,
                unfocusedTextColor = BlackGoldColors.LightGold,
                cursorColor = BlackGoldColors.Gold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Login Button
        Button(
            onClick = { onLoginClick(username, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = CustomShapes.buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackGoldColors.Gold,
                contentColor = BlackGoldColors.DeepBlack
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                "Sign In",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Link
        TextButton(
            onClick = onSignUpClick,
            colors = ButtonDefaults.textButtonColors(
                contentColor = BlackGoldColors.Gold
            )
        ) {
            Text(
                "Don't have an account? Sign up",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}