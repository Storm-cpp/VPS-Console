package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.VpsDatabase
import com.example.data.VpsRepository
import com.example.ui.screens.OrderScreen
import com.example.ui.screens.PromoScreen
import com.example.ui.screens.ServerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.*
import com.example.viewmodel.VpsViewModel
import com.example.viewmodel.VpsViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      val database = remember { VpsDatabase.getDatabase(context) }
      val repository = remember { VpsRepository(database.vpsDao()) }
      val application = context.applicationContext as Application

      val viewModel: VpsViewModel = viewModel(
          factory = VpsViewModelFactory(application, repository)
      )

      val settings by viewModel.settings.collectAsState()
      var currentTab by remember { mutableStateOf(0) } // 0 = Servers, 1 = Order, 2 = Ads, 3 = Settings

      MyApplicationTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(GoogleDarkBg),
            topBar = {
              Column {
                // Main Google Cloud Console-like Top Bar
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoogleBlue),
                        contentAlignment = Alignment.Center
                    ) {
                      Icon(
                          imageVector = Icons.Default.Home,
                          contentDescription = "Console",
                          tint = GoogleDarkBg,
                          modifier = Modifier.size(16.dp)
                      )
                    }

                    Text(
                        text = "VPS Console",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                  }

                  // Right side actions / Info indicator
                  Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    // Developer/Under Construction Active Badge
                    AnimatedVisibility(visible = settings.isDeveloperMode) {
                      val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                      val opacity by infiniteTransition.animateFloat(
                          initialValue = 0.4f,
                          targetValue = 1.0f,
                          animationSpec = infiniteRepeatable(
                              animation = tween(1000, easing = LinearEasing),
                              repeatMode = RepeatMode.Reverse
                          ),
                          label = "pulse_alpha"
                      )

                      Surface(
                          color = GoogleYellow.copy(alpha = 0.15f * opacity),
                          contentColor = GoogleYellow,
                          shape = RoundedCornerShape(8.dp),
                          modifier = Modifier
                              .border(1.dp, GoogleYellow.copy(alpha = opacity), RoundedCornerShape(8.dp))
                              .clickable { currentTab = 3 } // Navigate to settings
                      ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                          Icon(
                              imageVector = Icons.Default.Build,
                              contentDescription = "Developer active",
                              modifier = Modifier.size(12.dp)
                          )
                          Text(
                              text = "DEV MODE ON",
                              fontSize = 9.sp,
                              fontWeight = FontWeight.Bold
                          )
                        }
                      }
                    }

                    // Simple Account profile indicator with dynamic ring
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.2.dp, GoogleBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                      Text(
                          text = "R",
                          color = GoogleBlue,
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp
                      )
                    }
                  }
                }

                Divider(color = BorderColor)
              }
            },
            bottomBar = {
              NavigationBar(
                  containerColor = GoogleDarkSurface,
                  tonalElevation = 8.dp,
                  modifier = Modifier
                      .navigationBarsPadding()
                      .border(0.5.dp, BorderColor, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                      .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
              ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "VPS list") },
                    label = { Text("VPS List", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoogleDarkBg,
                        selectedTextColor = GoogleBlue,
                        indicatorColor = GoogleBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_btn_servers")
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Order VPS") },
                    label = { Text("Order", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoogleDarkBg,
                        selectedTextColor = GoogleBlue,
                        indicatorColor = GoogleBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_btn_order")
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.Notifications, contentDescription = "Promotions and Ads") },
                    label = { Text("Promo Ads", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoogleDarkBg,
                        selectedTextColor = GoogleBlue,
                        indicatorColor = GoogleBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_btn_promo")
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings and Developer console") },
                    label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GoogleDarkBg,
                        selectedTextColor = GoogleBlue,
                        indicatorColor = GoogleBlue,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    ),
                    modifier = Modifier.testTag("nav_btn_settings")
                )
              }
            }
        ) { innerPadding ->
          Box(
              modifier = Modifier
                  .fillMaxSize()
                  .background(GoogleDarkBg)
                  .padding(innerPadding)
          ) {
            when (currentTab) {
              0 -> ServerScreen(viewModel = viewModel)
              1 -> OrderScreen(viewModel = viewModel)
              2 -> PromoScreen()
              3 -> SettingsScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}
