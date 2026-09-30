package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppThemeMode
import com.example.data.speech.VoiceState
import com.example.ui.components.VoiceInputDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CreatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PhotoStudioScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelScriptScreen
import com.example.ui.screens.TrendingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.VibeCyan
import com.example.ui.theme.VibeMagenta
import com.example.ui.theme.VibeVioletPrimary
import com.example.ui.viewmodel.VibeViewModel
import kotlinx.coroutines.launch

enum class Screen(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    CREATOR("Create", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
    STUDIO("Studio", Icons.Filled.CameraAlt, Icons.Outlined.CameraAlt),
    TRENDING("Trending", Icons.AutoMirrored.Filled.TrendingUp, Icons.AutoMirrored.Outlined.TrendingUp),
    HISTORY("History", Icons.Filled.History, Icons.Outlined.History),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person),
    AUTH("Auth", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun VibeApp(viewModel: VibeViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val userProfile by viewModel.userProfile.collectAsState()
    val voiceState by viewModel.voiceInputManager.voiceState.collectAsState()
    val partialVoiceText by viewModel.voiceInputManager.partialText.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var studioSubTab by remember { mutableIntStateOf(0) } // 0: Photo Studio, 1: Reel Scripts
    var showVoiceDialog by remember { mutableStateOf(false) }

    // Theme resolution
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (userProfile.themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemDark
    }

    // Audio Permission Launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showVoiceDialog = true
            viewModel.voiceInputManager.startListening()
        } else {
            Toast.makeText(context, "Microphone permission needed for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    val requestVoiceInput: () -> Unit = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            showVoiceDialog = true
            viewModel.voiceInputManager.startListening()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle UI Events (Snackbars / Toasts)
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Voice Dialog
    if (showVoiceDialog) {
        VoiceInputDialog(
            voiceState = voiceState,
            partialText = partialVoiceText,
            onDismiss = {
                viewModel.voiceInputManager.reset()
                showVoiceDialog = false
            },
            onUseText = { text ->
                viewModel.promptInput.value = text
                viewModel.voiceInputManager.reset()
                showVoiceDialog = false
            }
        )
    }

    // BackHandler for sub-screens
    if (currentScreen != Screen.HOME) {
        BackHandler {
            currentScreen = Screen.HOME
        }
    }

    MyApplicationTheme(darkTheme = isDark) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpanded = maxWidth >= 600.dp

            if (isExpanded) {
                // Tablet / Desktop layout with Navigation Rail
                Row(modifier = Modifier.fillMaxSize()) {
                    if (currentScreen != Screen.AUTH) {
                        NavigationRail(
                            modifier = Modifier.fillMaxHeight(),
                            containerColor = MaterialTheme.colorScheme.surface,
                            header = {
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(PrimaryGradient),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("⚡", fontSize = 22.sp)
                                }
                            }
                        ) {
                            val railScreens = listOf(
                                Screen.HOME, Screen.CREATOR, Screen.STUDIO,
                                Screen.TRENDING, Screen.HISTORY, Screen.PROFILE
                            )

                            railScreens.forEach { screen ->
                                val isSelected = currentScreen == screen
                                NavigationRailItem(
                                    selected = isSelected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = VibeVioletPrimary,
                                        selectedTextColor = VibeVioletPrimary,
                                        indicatorColor = VibeVioletPrimary.copy(alpha = 0.15f)
                                    ),
                                    modifier = Modifier.testTag("nav_rail_${screen.name.lowercase()}")
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        AppScreenContent(
                            currentScreen = currentScreen,
                            studioSubTab = studioSubTab,
                            onStudioSubTabChanged = { studioSubTab = it },
                            viewModel = viewModel,
                            onNavigateToCreator = { currentScreen = Screen.CREATOR },
                            onNavigateToTrending = { currentScreen = Screen.TRENDING },
                            onNavigateToStudio = { currentScreen = Screen.STUDIO },
                            onNavigateToHome = { currentScreen = Screen.HOME },
                            onNavigateToHistory = { currentScreen = Screen.HISTORY },
                            onNavigateToAuth = { currentScreen = Screen.AUTH },
                            onStartVoice = requestVoiceInput
                        )
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            } else {
                // Mobile layout with Bottom Navigation
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        if (currentScreen != Screen.AUTH) {
                            val bottomScreens = listOf(
                                Screen.HOME, Screen.CREATOR, Screen.STUDIO,
                                Screen.TRENDING, Screen.HISTORY, Screen.PROFILE
                            )

                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 6.dp
                            ) {
                                bottomScreens.forEach { screen ->
                                    val isSelected = currentScreen == screen
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentScreen = screen },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                                contentDescription = screen.title
                                            )
                                        },
                                        label = { Text(screen.title, fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = VibeVioletPrimary,
                                            selectedTextColor = VibeVioletPrimary,
                                            indicatorColor = VibeVioletPrimary.copy(alpha = 0.15f)
                                        ),
                                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppScreenContent(
                            currentScreen = currentScreen,
                            studioSubTab = studioSubTab,
                            onStudioSubTabChanged = { studioSubTab = it },
                            viewModel = viewModel,
                            onNavigateToCreator = { currentScreen = Screen.CREATOR },
                            onNavigateToTrending = { currentScreen = Screen.TRENDING },
                            onNavigateToStudio = { currentScreen = Screen.STUDIO },
                            onNavigateToHome = { currentScreen = Screen.HOME },
                            onNavigateToHistory = { currentScreen = Screen.HISTORY },
                            onNavigateToAuth = { currentScreen = Screen.AUTH },
                            onStartVoice = requestVoiceInput
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppScreenContent(
    currentScreen: Screen,
    studioSubTab: Int,
    onStudioSubTabChanged: (Int) -> Unit,
    viewModel: VibeViewModel,
    onNavigateToCreator: () -> Unit,
    onNavigateToTrending: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onStartVoice: () -> Unit
) {
    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            Screen.HOME -> HomeScreen(
                viewModel = viewModel,
                onNavigateToCreator = onNavigateToCreator,
                onNavigateToTrending = onNavigateToTrending,
                onNavigateToStudio = onNavigateToStudio,
                onStartVoice = onStartVoice
            )
            Screen.CREATOR -> CreatorScreen(
                viewModel = viewModel,
                onNavigateBackToHome = onNavigateToHome
            )
            Screen.STUDIO -> {
                // Studio tab with switcher: Photo Studio & Reel Script Generator
                Box(modifier = Modifier.fillMaxSize()) {
                    androidx.compose.foundation.layout.Column(modifier = Modifier.fillMaxSize()) {
                        TabRow(
                            selectedTabIndex = studioSubTab,
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = VibeVioletPrimary
                        ) {
                            Tab(
                                selected = studioSubTab == 0,
                                onClick = { onStudioSubTabChanged(0) },
                                text = { Text("📸 AI Photo Studio", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                modifier = Modifier.testTag("tab_photo_studio")
                            )
                            Tab(
                                selected = studioSubTab == 1,
                                onClick = { onStudioSubTabChanged(1) },
                                text = { Text("🎬 Reel Scripts", fontSize = 13.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                                modifier = Modifier.testTag("tab_reel_scripts")
                            )
                        }

                        if (studioSubTab == 0) {
                            PhotoStudioScreen(
                                viewModel = viewModel,
                                onNavigateToCreator = onNavigateToCreator
                            )
                        } else {
                            ReelScriptScreen(
                                viewModel = viewModel,
                                onNavigateToCreator = onNavigateToCreator
                            )
                        }
                    }
                }
            }
            Screen.TRENDING -> TrendingScreen(
                viewModel = viewModel,
                onNavigateToHome = onNavigateToHome
            )
            Screen.HISTORY -> HistoryScreen(
                viewModel = viewModel,
                onReusePrompt = { prompt ->
                    viewModel.promptInput.value = prompt
                    onNavigateToHome()
                }
            )
            Screen.PROFILE -> ProfileScreen(
                viewModel = viewModel,
                onNavigateToAuth = onNavigateToAuth,
                onNavigateToHistory = onNavigateToHistory
            )
            Screen.AUTH -> AuthScreen(
                viewModel = viewModel,
                onBack = onNavigateToHome
            )
        }
    }
}
