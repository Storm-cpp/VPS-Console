package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AppSetting
import com.example.ui.theme.*
import com.example.viewmodel.VpsViewModel

@Composable
fun SettingsScreen(
    viewModel: VpsViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        item {
            Text(
                text = "Console Settings & Configuration",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            // General settings panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Developer Admin Console",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoogleBlue
                )

                Divider(color = BorderColor)

                // 2. Developer/Under construction mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mode under Development",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Toggle the 'In-Development' overlay & showcase placeholder mock dashboards.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.isDeveloperMode,
                        onCheckedChange = { viewModel.updateDeveloperMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoogleBlue,
                            checkedTrackColor = GoogleBlue.copy(alpha = 0.4f),
                            uncheckedBorderColor = BorderColor
                        ),
                        modifier = Modifier.testTag("developer_mode_toggle")
                    )
                }

                Divider(color = BorderColor)

                // 1. Live simulation toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Live VPS Daemon Telemetry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Automatically fluctuate host stats (CPU/RAM/Bandwidth) every 3 seconds.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = settings.isSimulationActive,
                        onCheckedChange = { viewModel.updateSimulationActive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoogleGreen,
                            checkedTrackColor = GoogleGreen.copy(alpha = 0.4f),
                            uncheckedBorderColor = BorderColor
                        ),
                        modifier = Modifier.testTag("simulation_toggle")
                    )
                }
            }
        }

        item {
            // Stats summary card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(GoogleBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "", tint = GoogleBlue, modifier = Modifier.size(14.dp))
                        }
                        Text("Active Schema State Metrics", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoogleBlue)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SQLite Local Storage", fontSize = 11.sp, color = TextSecondary)
                            Text("Database online", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Binds: ${servers.size} Hosts", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = GoogleGreen)
                            Text("Contracts: ${orders.size} Plans", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Live developer mode sub-panel
        item {
            AnimatedVisibility(
                visible = settings.isDeveloperMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0C0D11), RoundedCornerShape(12.dp))
                        .border(1.dp, GoogleYellow.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "",
                            tint = GoogleYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "BUILD MODE SYSTEM SIGNALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoogleYellow
                        )
                    }

                    Text(
                        text = "The 'In-Development Mode' has been toggled ON. Below is the system log telemetry streams coming from the active Android host container.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )

                    // Logs simulator box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "[KSP Compiler] Generated 3 Room Entity converters successfully.",
                            color = Color(0xFF81C995),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "[Android Edge Setup] enableEdgeToEdge() initialized correctly.",
                            color = Color(0xFF81C995),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "[Router Sockets] Routing requests map state: ${if (settings.isSimulationActive) "FLUID_SIMULATION_ON" else "SIMULATION_HALTED"}.",
                            color = Color(0xFF8AB4F8),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "[Client Mail Engine] Bound to secure email: romanptashnik268@gmail.com",
                            color = Color(0xFFFDE293),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Watching state changes on sqlite schema...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
