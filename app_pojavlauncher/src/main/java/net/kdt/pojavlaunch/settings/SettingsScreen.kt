package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.kdt.pojavlaunch.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun SettingsScreen(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit,
    onBack: () -> Unit,
    onAddMicrosoftAccount: () -> Unit,
    onOpenRuntimeManager: () -> Unit,
    updateState: PlayUpdateUiState,
    onCheckForUpdate: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {
    var launcherPage by rememberSaveable {
        mutableStateOf(
            LauncherSettingsPage.MAIN
        )
    }
    val handleBack: () -> Unit = {

        if (
            selectedTab == SettingsTab.LAUNCHER &&
            launcherPage != LauncherSettingsPage.MAIN
        ) {

            launcherPage =
                LauncherSettingsPage.MAIN

        } else {

            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsBackground)
    ) {

        /*
 * =========================================================
 * BACKGROUND DO HEADER
 * =========================================================
 */
        Image(
            painter = painterResource(
                R.drawable.bg_settings_art
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            contentScale = ContentScale.Crop
        )


        /*
         * =========================================================
         * FADE PARA O FUNDO
         * =========================================================
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.00f to SettingsBackground.copy(alpha = 0.70f),
                            1.00f to SettingsBackground
                        )
                    )
                )
        )

        /*
         * =========================================================
         * CONTEÚDO
         * =========================================================
         */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 56.dp,
                    bottom = 14.dp
                )
        ) {

            Row(
                modifier = Modifier.fillMaxSize()
            ) {

                SettingsSidebar(
                    selectedTab = selectedTab,

                    onTabSelected = { tab ->

                        if (tab != SettingsTab.LAUNCHER) {
                            launcherPage =
                                LauncherSettingsPage.MAIN
                        }

                        onTabSelected(tab)
                    },

                    onBack = handleBack
                )

                Spacer(
                    modifier = Modifier.width(18.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {

                    when (selectedTab) {
                        SettingsTab.ACCOUNT ->
                            AccountSettingsScreen(
                                onAddMicrosoftAccount = onAddMicrosoftAccount
                            )

                        SettingsTab.LAUNCHER ->
                            LauncherSettingsScreen(
                                currentPage =
                                    launcherPage,

                                onPageChange = {
                                    launcherPage = it
                                },

                                onOpenRuntimeManager =
                                    onOpenRuntimeManager
                            )

                        SettingsTab.MODPACK ->
                            ModpackSettingsScreen()

                        SettingsTab.ABOUT ->
                            AboutSettingsScreen(
                                updateState = updateState,
                                onCheckForUpdate = onCheckForUpdate,
                                onStartUpdate = onStartUpdate,
                                onCompleteUpdate = onCompleteUpdate
                            )
                    }
                }
            }
        }

        /*
         * =========================================================
         * TÍTULO
         * =========================================================
         */
        Text(
            text = "CONFIGURAÇÕES",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp),
            color = Color.White,
            fontFamily = MinecraftBoldFont,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = androidx.compose.ui.text.TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.6f),
                    offset = Offset(
                        x = 0f,
                        y = 2f
                    ),
                    blurRadius = 3f
                )
            )
        )
    }
}
