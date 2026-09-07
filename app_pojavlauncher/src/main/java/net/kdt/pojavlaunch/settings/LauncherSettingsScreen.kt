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

enum class LauncherSettingsPage {
    MAIN,
    VIDEO,
    CONTROLS,
    JAVA,
    MISC,
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
            title = "Launcher",
            description = "Gerencie seu launcher"
        )
        /*
         * =========================
         * VÍDEO
         * =========================
         */

        SettingsActionCard(
            title = "Vídeo e Renderizador",
            description = "Resolução e desempenho",
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
            title = "Controles",
            description = "Gestos, botões e escala",
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
            title = "Ajustes do Java",
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
            title = "Configurações diversas",
            description = "Organize seu jogo",
            onClick = {
                onPageChange(
                    LauncherSettingsPage.MISC
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
            title = "Opções experimentais",
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