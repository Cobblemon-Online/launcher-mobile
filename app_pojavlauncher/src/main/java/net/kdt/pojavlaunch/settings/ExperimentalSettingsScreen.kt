package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import kotlin.random.Random

@Composable
fun ExperimentalSettingsScreen() {

    val context = LocalContext.current
    val prefs = LauncherPreferences.DEFAULT_PREF

    var dumpShaders by remember {
        mutableStateOf(
            LauncherPreferences.PREF_DUMP_SHADERS
        )
    }

    var bigCoreAffinity by remember {
        mutableStateOf(
            LauncherPreferences.PREF_BIG_CORE_AFFINITY
        )
    }

    /*
     * O LauncherPreferences não possui um campo
     * PREF_SODIUM_OVERRIDE.
     *
     * Então lemos a mesma chave usada pelo XML original.
     */
    var sodiumOverride by remember {
        mutableStateOf(
            prefs.getBoolean(
                "sodium_override",
                false
            )
        )
    }

    var showSodiumDialog by remember {
        mutableStateOf(false)
    }

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
            title = "Opções experimentais",
            description =
                "Não fornecemos suporte a essas opções"
        )

        /*
         * =========================
         * Dump shaders
         * =========================
         */

        SettingsSwitchCard(
            title = "Salvar shaders no log",
            description =
                "Registra shaders convertidos no arquivo de log.",
            checked = dumpShaders,
            onCheckedChange = {

                dumpShaders = it

                prefs.edit()
                    .putBoolean(
                        "dump_shaders",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * Big Core Affinity
         * =========================
         */

        SettingsSwitchCard(
            title =
                "Forçar renderizador no núcleo de alto desempenho",
            description =
                "Executa a thread de renderização do Minecraft no núcleo com maior frequência máxima.",
            checked = bigCoreAffinity,
            onCheckedChange = {

                bigCoreAffinity = it

                prefs.edit()
                    .putBoolean(
                        "bigCoreAffinity",
                        it
                    )
                    .apply()

                reloadPreferences()
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        /*
         * =========================
         * Sodium Override
         * =========================
         */

        SettingsSwitchCard(
            title = "Forçar execução do Sodium",
            description =
                if (sodiumOverride) {
                    "Sodium não é suportado. Problemas causados por esta opção não recebem suporte."
                } else {
                    "O uso do Sodium pode causar erros gráficos e crashes."
                },
            checked = sodiumOverride,
            onCheckedChange = { enabled ->

                /*
                 * DESATIVAR:
                 *
                 * Igual ao MathQuestionPreference original:
                 * pode desligar imediatamente.
                 */
                if (!enabled) {

                    sodiumOverride = false

                    prefs.edit()
                        .putBoolean(
                            "sodium_override",
                            false
                        )
                        .apply()

                    return@SettingsSwitchCard
                }

                /*
                 * ATIVAR:
                 *
                 * Não ativamos imediatamente.
                 * Primeiro mostramos o teste matemático.
                 */
                showSodiumDialog = true
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }

    if (showSodiumDialog) {

        SodiumConfirmationDialog(
            onDismiss = {
                showSodiumDialog = false
            },
            onConfirmed = {

                prefs.edit()
                    .putBoolean(
                        "sodium_override",
                        true
                    )
                    .apply()

                sodiumOverride = true
                showSodiumDialog = false
            }
        )
    }
}

@Composable
private fun SodiumConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit
) {

    /*
     * Mesma ideia do MathQuestionPreference:
     *
     * (a * b) + c - d
     */

    val a = remember {
        Random.nextInt(1, 11)
    }

    val b = remember {
        Random.nextInt(1, 11)
    }

    val c = remember {
        Random.nextInt(1, 11)
    }

    val d = remember {
        Random.nextInt(1, 11)
    }

    val answer = remember(a, b, c, d) {
        (a * b) + c - d
    }

    var input by remember {
        mutableStateOf("")
    }

    var secondsLeft by remember {
        mutableIntStateOf(45)
    }

    var wrongAnswer by remember {
        mutableStateOf(false)
    }

    /*
     * O Pojav original bloqueia o botão
     * durante 45 segundos.
     */
    LaunchedEffect(Unit) {

        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,

        title = {
            Text(
                text =
                    "Resolva a questão para ativar"
            )
        },

        text = {

            Column {

                Text(
                    text =
                        "Sodium não é oficialmente suportado. " +
                                "Seu uso pode causar bugs gráficos, crashes " +
                                "e problemas nos mundos salvos.\n\n" +
                                "Para confirmar, resolva:\n\n" +
                                "$a × $b + $c - $d = ?"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                        wrongAnswer = false
                    },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        ),
                    label = {
                        Text("Resposta")
                    }
                )

                if (wrongAnswer) {

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Resposta incorreta.",
                        color =
                            Color(0xFFFF7777)
                    )
                }
            }
        },

        confirmButton = {

            Button(
                enabled =
                    secondsLeft == 0,

                onClick = {

                    val userAnswer =
                        input.toIntOrNull()

                    if (
                        userAnswer ==
                        answer
                    ) {
                        onConfirmed()
                    } else {
                        wrongAnswer = true
                    }
                }
            ) {

                Text(
                    text =
                        if (secondsLeft > 0) {
                            "OK (${secondsLeft}s)"
                        } else {
                            "OK"
                        }
                )
            }
        },

        dismissButton = {

            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}