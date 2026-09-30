package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import net.kdt.pojavlaunch.R

enum class LauncherSettingsPage {
    MAIN,
    VIDEO,
    CONTROLS,
    JAVA,
    MISC,
    LANGUAGE,
    EXPERIMENTAL
}

@Composable
fun LauncherSettingsScreen(
    currentPage: LauncherSettingsPage,
    onPageChange: (LauncherSettingsPage) -> Unit,
    onOpenRuntimeManager: () -> Unit,
) {

    when (currentPage) {

        LauncherSettingsPage.VIDEO -> {
            VideoRendererSettingsScreen()
            return
        }

        LauncherSettingsPage.CONTROLS -> {
            ControlSettingsScreen()
            return
        }

        LauncherSettingsPage.JAVA -> {
            JavaSettingsScreen(
                onOpenRuntimeManager = onOpenRuntimeManager
            )
            return
        }

        LauncherSettingsPage.MISC -> {
            MiscSettingsScreen()
            return
        }

        LauncherSettingsPage.LANGUAGE -> {
            LanguageSettingsScreen()
            return
        }

        LauncherSettingsPage.EXPERIMENTAL -> {
            ExperimentalSettingsScreen()
            return
        }

        LauncherSettingsPage.MAIN -> {
            // continua abaixo
        }
    }

    /*
     * =========================
     * TELA PRINCIPAL
     * =========================
     */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        SettingsPageHeader(
            title = stringResource(R.string.settings_launcher),
            description = stringResource(R.string.settings_manage_launcher)
        )
        /*
         * =========================
         * VÍDEO
         * =========================
         */

        SettingsActionCard(
            title = stringResource(R.string.settings_video_renderer),
            description = stringResource(R.string.settings_resolution_performance),
            onClick = {
                onPageChange(
                    LauncherSettingsPage.VIDEO
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * CONTROLES
         * =========================
         *
         * Por enquanto ainda abre
         * o Fragment antigo.
         */

        SettingsActionCard(
            title = stringResource(R.string.settings_controls),
            description = stringResource(R.string.settings_controls_description),
            onClick = {
                onPageChange(
                    LauncherSettingsPage.CONTROLS
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * JAVA
         * =========================
         */

        SettingsActionCard(
            title = stringResource(R.string.settings_java_tweaks),
            description =
                "Java Runtimes, argumentos JVM, quantidade de RAM e sandbox",
            onClick = {
                onPageChange(
                    LauncherSettingsPage.JAVA
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * DIVERSAS
         * =========================
         */

        SettingsActionCard(
            title = stringResource(R.string.settings_miscellaneous),
            description = stringResource(R.string.settings_organize_game),
            onClick = {
                onPageChange(
                    LauncherSettingsPage.MISC
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        SettingsActionCard(
            title = stringResource(R.string.settings_language),
            description = stringResource(R.string.settings_change_language),
            onClick = {
                onPageChange(
                    LauncherSettingsPage.LANGUAGE
                )
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * EXPERIMENTAIS
         * =========================
         */

        SettingsActionCard(
            title = stringResource(R.string.settings_experimental),
            description =
                "Não fornecemos suporte a essas opções",
            onClick = {
                onPageChange(
                    LauncherSettingsPage.EXPERIMENTAL
                )
            }
        )
    }
}
