package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.RadioButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.prefs.LauncherPreferences

private data class DownloadSourceOption(
    val name: String,
    val value: String
)

@Composable
fun MiscSettingsScreen() {

    val context = LocalContext.current
    val prefs = LauncherPreferences.DEFAULT_PREF

    /*
     * =========================
     * Preferências atuais
     * =========================
     */

    var checkLibraries by remember {
        mutableStateOf(
            LauncherPreferences.PREF_CHECK_LIBRARY_SHA
        )
    }

    var verifyManifest by remember {
        mutableStateOf(
            LauncherPreferences.PREF_VERIFY_MANIFEST
        )
    }

    var downloadSource by remember {
        mutableStateOf(
            LauncherPreferences.PREF_DOWNLOAD_SOURCE
        )
    }

    var showDownloadSourceDialog by remember {
        mutableStateOf(false)
    }

    /*
     * Não hardcodamos "default", "bmclapi", etc.
     *
     * Pegamos exatamente os valores existentes
     * no Pojav através dos arrays originais.
     */
    val downloadSources = remember {

        val names =
            context.resources.getStringArray(
                R.array.download_source_names
            )

        val values =
            context.resources.getStringArray(
                R.array.download_source_values
            )

        names.indices.map { index ->
            DownloadSourceOption(
                name = names[index],
                value = values[index]
            )
        }
    }

    val selectedDownloadSourceName =
        downloadSources
            .firstOrNull {
                it.value == downloadSource
            }
            ?.name
            ?: downloadSource

    fun reloadPreferences() {
        LauncherPreferences.loadPreferences(context)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        SettingsPageHeader(
            title = "Configurações diversas",
            description =
                "Gerencie seu launcher"
        )

        /*
         * =========================
         * Verificar bibliotecas
         * =========================
         */

        SettingsSwitchCard(
            title =
                "Verificar as bibliotecas depois de baixar",
            description =
                "Esta opção força o launcher a verificar o hash da biblioteca caso estiver disponível. Impede baixar arquivos com problemas.",
            checked = checkLibraries,
            onCheckedChange = {

                checkLibraries = it

                prefs.edit()
                    .putBoolean(
                        "checkLibraries",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        /*
         * =========================
         * Fonte de download
         * =========================
         */

        SettingsActionCard(
            title = "Fonte de download do jogo",
            description = selectedDownloadSourceName,
            onClick = {
                showDownloadSourceDialog = true
            }
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        /*
         * =========================
         * Verificar manifesto
         * =========================
         */

        SettingsSwitchCard(
            title =
                "Verificar manifesto da versão do jogo",
            description =
                "Quando ativado, o launcher verificará o manifesto da versão do jogo com as bibliotecas.",
            checked = verifyManifest,
            onCheckedChange = {

                verifyManifest = it

                prefs.edit()
                    .putBoolean(
                        "verifyManifest",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }

    /*
     * =========================
     * Dialog fonte de download
     * =========================
     */

    if (showDownloadSourceDialog) {

        DownloadSourceDialog(
            options = downloadSources,
            selectedValue = downloadSource,

            onSelect = { option ->

                downloadSource =
                    option.value

                prefs.edit()
                    .putString(
                        "downloadSource",
                        option.value
                    )
                    .apply()

                reloadPreferences()

                showDownloadSourceDialog =
                    false
            },

            onDismiss = {
                showDownloadSourceDialog =
                    false
            }
        )
    }
}

@Composable
private fun DownloadSourceDialog(
    options: List<DownloadSourceOption>,
    selectedValue: String,
    onSelect: (DownloadSourceOption) -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color(0xFF555555)
                )
                .padding(14.dp)
        ) {

            Text(
                text =
                    "Fonte de download do jogo",
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 13.sp
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            options.forEach { option ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect(option)
                        },
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected =
                            option.value ==
                                    selectedValue,
                        onClick = {
                            onSelect(option)
                        }
                    )

                    Text(
                        text = option.name,
                        color = Color.White,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.End
            ) {

                Text(
                    text = "Cancelar",
                    color = Color.White,
                    fontFamily = MinecraftFont,
                    fontSize = 9.sp,
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(6.dp)
                        )
                        .background(
                            Color(0xFF7A7A7A)
                        )
                        .clickable {
                            onDismiss()
                        }
                        .padding(
                            horizontal = 22.dp,
                            vertical = 10.dp
                        )
                )
            }
        }
    }
}