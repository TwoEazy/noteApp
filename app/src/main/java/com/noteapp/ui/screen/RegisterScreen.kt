// app/src/main/java/com/noteapp/ui/screen/RegisterScreen.kt
package com.noteapp.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.noteapp.navigation.Navigator
import com.noteapp.navigation.Screens
import com.noteapp.ui.theme.BlackGoldColors
import com.noteapp.ui.theme.CustomShapes
import com.noteapp.viewmodel.RegisterState
import com.noteapp.viewmodel.RegisterViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navigator: Navigator,
    onRegisterSuccess: () -> Unit
) {
    val registerState by viewModel.registerState.collectAsState()

    // Animation states
    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "register_animation"
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with Back Button
            AnimatedVisibility(
                visible = animationStarted,
                enter = slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navigator.navigateTo(Screens.Login) },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = BlackGoldColors.Gold
                        )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back to Login",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = BlackGoldColors.Gold
                    )
                }
            }

            // Welcome Message
            AnimatedVisibility(
                visible = animationStarted,
                enter = slideInVertically(
                    initialOffsetY = { -200 },
                    animationSpec = tween(700, delayMillis = 100, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(700, delayMillis = 100))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Join NoteApp",
                        style = MaterialTheme.typography.displaySmall,
                        color = BlackGoldColors.Gold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start your journey with secure note-taking",
                        style = MaterialTheme.typography.bodyLarge,
                        color = BlackGoldColors.LightGold.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Registration Form Card
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
                    RegisterContent(
                        onRegisterClick = { email, password, firstName, lastName, birthDate, gender ->
                            viewModel.register(email, password, firstName, lastName)
                        },
                        modifier = Modifier
                            .padding(32.dp)
                            .alpha(animatedProgress)
                    )
                }
            }
        }

        // Loading, Success, and Error states
        when (val state = registerState) {
            is RegisterState.Loading -> {
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
                                text = "Creating your account...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = BlackGoldColors.LightGold
                            )
                        }
                    }
                }
            }
            is RegisterState.Success -> {
                LaunchedEffect(Unit) {
                    onRegisterSuccess()
                }
            }
            is RegisterState.Error -> {
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
                                Icons.Default.Person,
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
fun RegisterContent(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var passwordVisible by remember { mutableStateOf(false) }

    val genders = listOf("male", "female")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.headlineSmall,
            color = BlackGoldColors.Gold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    "Email",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Email",
                    tint = BlackGoldColors.Gold
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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

        // Password Field with Visibility Toggle (only if material-icons-extended is added)
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

        // First Name Field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = {
                Text(
                    "First Name",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "First Name",
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

        // Last Name Field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = {
                Text(
                    "Last Name",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Last Name",
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

        // Birth Date Field
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = {
                Text(
                    "Birthdate (YYYY/MM/DD)",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f)
                )
            },
            placeholder = {
                Text(
                    "YYYY/MM/DD",
                    color = BlackGoldColors.LightGold.copy(alpha = 0.5f)
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = CustomShapes.textFieldShape,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlackGoldColors.Gold,
                unfocusedBorderColor = BlackGoldColors.MediumGray,
                focusedTextColor = BlackGoldColors.LightGold,
                unfocusedTextColor = BlackGoldColors.LightGold,
                cursorColor = BlackGoldColors.Gold
            )
        )

        // Gender Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = CustomShapes.textFieldShape,
            colors = CardDefaults.cardColors(
                containerColor = BlackGoldColors.DarkGray.copy(alpha = 0.3f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    colors = listOf(BlackGoldColors.MediumGray, BlackGoldColors.Gold.copy(alpha = 0.3f))
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Gender",
                    style = MaterialTheme.typography.labelLarge,
                    color = BlackGoldColors.LightGold.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    genders.forEach { genderOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .selectable(
                                    selected = gender == genderOption,
                                    onClick = { gender = genderOption },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = gender == genderOption,
                                onClick = { gender = genderOption },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BlackGoldColors.Gold,
                                    unselectedColor = BlackGoldColors.MediumGray
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = genderOption.replaceFirstChar { it.uppercase() },
                                color = BlackGoldColors.LightGold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Register Button
        Button(
            onClick = {
                onRegisterClick(email, password, firstName, lastName, birthDate, gender)
            },
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
                "Create Account",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}