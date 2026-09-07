package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import net.kdt.pojavlaunch.Architecture
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.prefs.LauncherPreferences

@Composable
fun JavaSettingsScreen(
    onOpenRuntimeManager: () -> Unit
) {

    val context = LocalContext.current
    val prefs = LauncherPreferences.DEFAULT_PREF

    /*
     * =========================================================
     * ARGUMENTOS JVM
     * =========================================================
     */

    var javaArgs by remember {
        mutableStateOf(
            LauncherPreferences.PREF_CUSTOM_JAVA_ARGS
                ?: ""
        )
    }

    var javaArgsDraft by remember {
        mutableStateOf(javaArgs)
    }

    var showJavaArgsDialog by remember {
        mutableStateOf(false)
    }

    /*
     * =========================================================
     * SANDBOX
     * =========================================================
     */

    var javaSandbox by remember {
        mutableStateOf(
            LauncherPreferences.PREF_JAVA_SANDBOX
        )
    }

    /*
     * =========================================================
     * RAM
     * =========================================================
     *
     * Mantemos a mesma regra usada pelo
     * LauncherPreferenceJavaFragment original.
     */

    val deviceRam = remember {
        Tools.getTotalDeviceMemory(context)
    }

    val minRam = remember {
        context.resources.getInteger(
            R.integer.memory_seekbar_min
        )
    }

    val ramIncrement = remember {
        context.resources.getInteger(
            R.integer.memory_seekbar_increment
        ).coerceAtLeast(1)
    }

    val maxRam = remember(deviceRam, minRam) {

        val calculatedMax = if (
            Architecture.is32BitsDevice() ||
            deviceRam < 2048
        ) {

            minOf(
                1024,
                deviceRam
            )

        } else {

            deviceRam -
                    if (deviceRam < 3064) {
                        800
                    } else {
                        1024
                    }
        }

        calculatedMax.coerceAtLeast(
            minRam
        )
    }

    var ramAllocation by remember(
        minRam,
        maxRam
    ) {
        mutableIntStateOf(
            LauncherPreferences
                .PREF_RAM_ALLOCATION
                .coerceIn(
                    minRam,
                    maxRam
                )
        )
    }

    fun reloadPreferences() {
        LauncherPreferences.loadPreferences(
            context
        )
    }

    /*
     * =========================================================
     * CONTEÚDO
     * =========================================================
     */

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        SettingsPageHeader(
            title = "Ajustes do Java",
            description =
                "Java Runtimes, argumentos JVM, memória e sandbox"
        )


        /*
         * =========================================================
         * JAVA RUNTIMES
         * =========================================================
         */

        SettingsActionCard(
            title = "Java Runtimes",
            description =
                "Gerencie, instale e selecione as versões do Java disponíveis.",
            onClick = onOpenRuntimeManager
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * ARGUMENTOS JVM
         * =========================================================
         */

        SettingsActionCard(
            title = "Argumentos JVM",
            description =
                if (javaArgs.isBlank()) {
                    "Nenhum argumento personalizado definido."
                } else {
                    javaArgs
                },
            onClick = {

                javaArgsDraft =
                    javaArgs

                showJavaArgsDialog =
                    true
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * RAM
         * =========================================================
         */

        SettingsSliderCard(
            title = "Quantidade de RAM",
            description =
                "Memória reservada para o Minecraft.",
            value = ramAllocation,
            min = minRam,
            max = maxRam,
            increment = ramIncrement,
            valueSuffix = " MB",
            onValueChange = {
                ramAllocation = it
            },
            onValueChangeFinished = {

                prefs.edit()
                    .putInt(
                        "allocation",
                        ramAllocation
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================================================
         * JAVA SANDBOX
         * =========================================================
         */

        SettingsSwitchCard(
            title = "Sandbox do Java",
            description =
                "Restringe o acesso do Java ao sistema Android para aumentar a segurança.",
            checked =
                javaSandbox,

            onCheckedChange = {

                javaSandbox = it

                prefs.edit()
                    .putBoolean(
                        "java_sandbox",
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
     * =========================================================
     * DIALOG ARGUMENTOS JVM
     * =========================================================
     */

    if (showJavaArgsDialog) {

        AlertDialog(
            onDismissRequest = {
                showJavaArgsDialog =
                    false
            },

            title = {

                Text(
                    text =
                        "Argumentos JVM"
                )
            },

            text = {

                Column {

                    Text(
                        text =
                            "Informe argumentos personalizados para a JVM."
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    OutlinedTextField(
                        value =
                            javaArgsDraft,

                        onValueChange = {
                            javaArgsDraft = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine =
                            true,

                        label = {
                            Text(
                                "Argumentos"
                            )
                        },

                        placeholder = {
                            Text(
                                "-XX:+UseG1GC ..."
                            )
                        }
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text =
                            "Não inclua -Xms ou -Xmx. A memória é configurada separadamente.",
                        color =
                            Color(0xFF858585)
                    )
                }
            },

            confirmButton = {

                Button(
                    onClick = {

                        javaArgs =
                            javaArgsDraft.trim()

                        prefs.edit()
                            .putString(
                                "javaArgs",
                                javaArgs
                            )
                            .apply()

                        reloadPreferences()

                        showJavaArgsDialog =
                            false
                    }
                ) {

                    Text(
                        "Salvar"
                    )
                }
            },

            dismissButton = {

                Button(
                    onClick = {
                        showJavaArgsDialog =
                            false
                    }
                ) {

                    Text(
                        "Cancelar"
                    )
                }
            }
        )
    }
}
