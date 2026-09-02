package net.kdt.pojavlaunch.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import net.kdt.pojavlaunch.PojavProfile
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.BuildConfig
import net.kdt.pojavlaunch.R
import net.kdt.pojavlaunch.value.MinecraftAccount
import androidx.compose.foundation.layout.heightIn
import net.kdt.pojavlaunch.modpacks.ManagedModpack
import net.kdt.pojavlaunch.modpacks.ManagedModpackCatalog


private val MinecraftFont = FontFamily(
    Font(R.font.minecraft_standard)
)

private val MinecraftBoldFont = FontFamily(
    Font(R.font.minecraft_standard_bold)
)

private val SettingsBackground = Color(0xFF0D0D0D)

@Composable
fun SettingsScreen(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit,
    onBack: () -> Unit,
    onAddMicrosoftAccount: () -> Unit,

    onOpenVideoSettings: () -> Unit,
    onOpenControlSettings: () -> Unit,
    onOpenJavaSettings: () -> Unit,
    onOpenMiscSettings: () -> Unit,
    onOpenExperimentalSettings: () -> Unit,
    updateState: PlayUpdateUiState,
    onCheckForUpdate: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {

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
                    onTabSelected = onTabSelected,
                    onBack = onBack
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
                                onOpenVideoSettings =
                                    onOpenVideoSettings,

                                onOpenControlSettings =
                                    onOpenControlSettings,

                                onOpenJavaSettings =
                                    onOpenJavaSettings,

                                onOpenMiscSettings =
                                    onOpenMiscSettings,

                                onOpenExperimentalSettings =
                                    onOpenExperimentalSettings
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
@Composable
private fun SettingsSidebar(
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .fillMaxHeight()
    ) {

        Box(
            modifier = Modifier
                .width(116.dp)
                .wrapContentHeight()
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {

            Image(
                painter = painterResource(
                    R.drawable.btn_voltar
                ),
                contentDescription = "Voltar",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Text(
                text = "Voltar",
                color = Color.White,
                fontFamily = MinecraftFont,
                fontSize = 9.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        SettingsSidebarItem(
            text = "Conta",
            selected = selectedTab == SettingsTab.ACCOUNT,
            onClick = {
                onTabSelected(SettingsTab.ACCOUNT)
            }
        )

        SettingsSidebarItem(
            text = "Launcher",
            selected = selectedTab == SettingsTab.LAUNCHER,
            onClick = {
                onTabSelected(SettingsTab.LAUNCHER)
            }
        )

        SettingsSidebarItem(
            text = "Modpack",
            selected = selectedTab == SettingsTab.MODPACK,
            onClick = {
                onTabSelected(SettingsTab.MODPACK)
            }
        )

        SettingsSidebarItem(
            text = "Sobre",
            selected = selectedTab == SettingsTab.ABOUT,
            onClick = {
                onTabSelected(SettingsTab.ABOUT)
            }
        )
    }
}
@Composable
private fun SettingsSidebarItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                start = 8.dp
            ),
            color = if (selected) {
                Color.White
            } else {
                Color(0xFF747474)
            },
            fontFamily = MinecraftFont,
            fontSize = 10.sp
        )
    }
}
@Composable
private fun LauncherSettingsScreen(
    onOpenVideoSettings: () -> Unit,
    onOpenControlSettings: () -> Unit,
    onOpenJavaSettings: () -> Unit,
    onOpenMiscSettings: () -> Unit,
    onOpenExperimentalSettings: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "Launcher",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Gerencie seu launcher",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        LauncherSettingsCard(
            title = "Vídeo e Renderizador",
            description = "Resolução e desempenho",
            onClick = onOpenVideoSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Controles",
            description = "Gestos, botões e escala",
            onClick = onOpenControlSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Ajustes do Java",
            description =
                "Java Runtimes, argumentos JVM, quantidade de RAM e sandbox",
            onClick = onOpenJavaSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Configurações diversas",
            description = "Organize seu jogo",
            onClick = onOpenMiscSettings
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        LauncherSettingsCard(
            title = "Opções experimentais",
            description =
                "Não fornecemos suporte a essas opções",
            onClick =
                onOpenExperimentalSettings
        )
    }
}

@Composable
private fun LauncherSettingsCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 14.dp,
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = title,
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 10.sp,
            lineHeight = 12.sp
        )

//        Spacer(
//            modifier = Modifier.height(2.dp)
//        )

        Text(
            text = description,
            color = Color(0xFF858585),
            fontFamily = MinecraftFont,
            fontSize = 8.sp,
            lineHeight = 10.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun ModpackSettingsScreen() {

    val context = LocalContext.current

    val activity =
        context as? LauncherActivity

    val modpacks =
        remember {
            ManagedModpackCatalog.getPacks()
        }

    var selectedModpackId by remember {
        mutableStateOf(
            activity?.selectedManagedModpackId
                ?: ManagedModpackCatalog.DEFAULT_PACK_ID
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        Text(
            text = "Modpack",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Selecione o modpack que deseja jogar",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        modpacks.forEachIndexed { index, modpack ->

            ModpackOptionCard(
                modpack = modpack,
                selected =
                    selectedModpackId == modpack.id,
                onClick = {

                    if (
                        selectedModpackId != modpack.id
                    ) {

                        selectedModpackId =
                            modpack.id

                        activity
                            ?.selectManagedModpackFromCompose(
                                modpack.id
                            )
                    }
                }
            )


            if (
                index < modpacks.lastIndex
            ) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )
            }
        }
    }
}
@Composable
private fun ModpackOptionCard(
    modpack: ManagedModpack,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .then(
                if (selected) {

                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFF54E2B0),
                        shape = RoundedCornerShape(8.dp)
                    )

                } else {

                    Modifier
                }
            )
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text =
                    modpack.name,
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    9.sp
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "MINECRAFT ${modpack.minecraftVersion}",
                color =
                    Color(0xFF8A8A8A),
                fontFamily =
                    MinecraftFont,
                fontSize =
                    7.sp
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    "VERSÃO DO MODPACK: ${modpack.modpackVersion}",
                color =
                    Color(0xFF8A8A8A),
                fontFamily =
                    MinecraftFont,
                fontSize =
                    7.sp
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    modpack.description,
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    8.sp
            )
        }


        Spacer(
            modifier =
                Modifier.width(12.dp)
        )


        Box(
            modifier = Modifier
                .width(124.dp)
                .height(36.dp)
                .clip(
                    RoundedCornerShape(6.dp)
                )
                .background(
                    if (selected) {

                        Color(0xFF8B8B8B)

                    } else {

                        Color(0xFF7B2FD3)
                    }
                )
                .clickable(
                    enabled = !selected
                ) {

                    onClick()
                },
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    if (selected) {
                        "Selecionado"
                    } else {
                        "Selecionar"
                    },
                color =
                    Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize =
                    9.sp
            )
        }
    }
}
@Composable
private fun AboutSettingsScreen(
    updateState: PlayUpdateUiState,
    onCheckForUpdate: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {

    LaunchedEffect(Unit) {
        if (updateState.status == PlayUpdateStatus.IDLE) {
            onCheckForUpdate()
        }
    }

    val statusText = when (updateState.status) {
        PlayUpdateStatus.IDLE -> "Verificação pendente"
        PlayUpdateStatus.CHECKING -> "Verificando atualização"
        PlayUpdateStatus.UP_TO_DATE -> "Versão atualizada"
        PlayUpdateStatus.AVAILABLE -> "Atualização disponível"
        PlayUpdateStatus.DOWNLOADING -> "Baixando atualização"
        PlayUpdateStatus.READY_TO_INSTALL -> "Pronta para instalar"
        PlayUpdateStatus.ERROR -> "Não foi possível verificar"
    }

    val statusColor = when (updateState.status) {
        PlayUpdateStatus.AVAILABLE,
        PlayUpdateStatus.READY_TO_INSTALL -> Color(0xFFFFC107)
        PlayUpdateStatus.ERROR -> Color(0xFFE57373)
        else -> Color(0xFF79D800)
    }

    val buttonText = when (updateState.status) {
        PlayUpdateStatus.CHECKING -> "VERIFICANDO..."
        PlayUpdateStatus.AVAILABLE -> "ATUALIZAR AGORA"
        PlayUpdateStatus.DOWNLOADING -> updateState.progressPercent?.let { "BAIXANDO $it%" }
            ?: "BAIXANDO..."
        PlayUpdateStatus.READY_TO_INSTALL -> "REINICIAR E INSTALAR"
        else -> "CHECAR ATUALIZAÇÃO"
    }

    val onUpdateClick = when (updateState.status) {
        PlayUpdateStatus.AVAILABLE -> onStartUpdate
        PlayUpdateStatus.READY_TO_INSTALL -> onCompleteUpdate
        PlayUpdateStatus.CHECKING,
        PlayUpdateStatus.DOWNLOADING -> ({})
        else -> onCheckForUpdate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        /*
         * =========================================================
         * CABEÇALHO
         * =========================================================
         */

        Text(
            text = "Sobre",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Sobre o Launcher",
            color = Color(0xFF777777),
            fontFamily = MinecraftFont,
            fontSize = 9.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        /*
         * =========================================================
         * CARD DO LAUNCHER
         * =========================================================
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(98.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color(0xFF202020)
                )
                .padding(
                    horizontal = 30.dp,
                    vertical = 18.dp
                )
        ) {

            /*
             * Nome do launcher
             */
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(
                        R.drawable.ic_launcher_foreground
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .width(27.dp)
                        .height(27.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )

                Text(
                    text =
                        "COBBLEMON ONLINE LAUNCHER",
                    color = Color.White,
                    fontFamily =
                        MinecraftFont,
                    fontSize = 9.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            /*
             * Versão + botão
             */
            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                /*
                 * Status da versão
                 */
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * Círculo verde
                     */
                    Image(
                        painter = painterResource(
                            R.drawable.ic_check
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .width(27.dp)
                            .height(27.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            statusText,
                        color =
                            statusColor,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 9.sp
                    )

                    Spacer(
                        modifier =
                            Modifier.width(7.dp)
                    )

                    Text(
                        text = BuildConfig.VERSION_NAME,
                        color = Color.White,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 9.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )

                /*
                 * Botão atualizar
                 */
                Box(
                    modifier = Modifier
                        .width(180.dp)
                        .height(25.dp)
                        .background(
                            Color(0xFF858585)
                        )
                        .border(
                            width = 1.dp,
                            color =
                                Color(0xFFA7A7A7)
                        )
                        .clickable(onClick = onUpdateClick),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            buttonText,
                        color = Color.White,
                        fontFamily =
                            MinecraftFont,
                        fontSize = 7.sp
                    )
                }
            }
        }

        Spacer(
            modifier =
                Modifier.height(12.dp)
        )

        /*
         * =========================================================
         * RELEASE NOTES
         * =========================================================
         */

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color(0xFF1D1D1D)
                )
                .padding(
                    start = 30.dp,
                    end = 30.dp,
                    top = 12.dp,
                    bottom = 20.dp
                )
        ) {

            Text(
                text =
                    "Notas de atualização",
                color = Color.White,
                fontFamily =
                    MinecraftFont,
                fontSize = 19.sp
            )

            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )

            /*
             * Tag da versão
             */
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFF853AC7)
                    )
                    .padding(
                        horizontal = 6.dp,
                        vertical = 4.dp
                    )
            ) {

                Text(
                    text =
                    "ATUALIZAÇÕES PELA GOOGLE PLAY",
                    color =
                        Color(0xFFE4D5F2),
                    fontFamily =
                        MinecraftFont,
                    fontSize = 6.sp
                )
            }

            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )

            /*
             * Desempenho
             */
            AboutReleaseSection(
                title = "Status",
                description = updateState.message
                    ?: "A Google Play mantém o launcher atualizado automaticamente."
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            /*
             * Segurança
             */
            AboutReleaseSection(
                title = "Novidades",
                description =
                    "Consulte as novidades e o histórico da versão na página do launcher na Google Play."
            )
        }
    }
}
@Composable
private fun AboutReleaseSection(
    title: String,
    description: String
) {

    Column {

        Text(
            text = title,
            color = Color.White,
            fontFamily =
                MinecraftFont,
            fontSize = 11.sp,
            lineHeight = 13.sp
        )

        Text(
            text = description,
            color = Color(0xFF8A8A8A),
            fontFamily =
                MinecraftFont,
            fontSize = 9.sp,
            lineHeight = 11.sp
        )
    }
}

@Composable
private fun AccountSettingsScreen(
    onAddMicrosoftAccount: () -> Unit
) {

    val context = LocalContext.current

    var showOfflineDialog by remember {
        mutableStateOf(false)
    }

    var currentAccount by remember {
        mutableStateOf(
            PojavProfile.getCurrentProfileContent(
                context,
                null
            )
        )
    }

    val activity =
        context as? LauncherActivity

    var accounts by remember {
        mutableStateOf(
            PojavProfile.getAllProfiles()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Conta",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Text(
            text = "Gerencie suas contas",
            color = Color(0xFF8D8D8D),
            fontFamily = MinecraftFont,
            fontSize = 10.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // CONTA ATUAL
        currentAccount?.let { account ->

            AccountCard(
                account = account,
                current = true,
                onSwitch = {},
                onDelete = {}
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        Text(
            text = "Trocar de conta",
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 15.sp
        )

        Spacer(
            modifier = Modifier.height(13.dp)
        )

        // OUTRAS CONTAS
        accounts
            .filter {
                it.username != currentAccount?.username
            }
            .forEach { account ->

                AccountCard(
                    account = account,
                    current = false,
                    onSwitch = {

                        PojavProfile.setCurrentProfile(
                            context,
                            account.username
                        )

                        currentAccount = account

                        accounts =
                            PojavProfile.getAllProfiles()
                    },
                    onDelete = {

                        activity?.deleteAccountFromCompose(
                            account.username
                        )

                        accounts =
                            PojavProfile.getAllProfiles()

                        currentAccount =
                            PojavProfile.getCurrentProfileContent(
                                context,
                                null
                            )
                    }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }

        // ADICIONAR MICROSOFT
        AddAccountCard(
            icon = R.drawable.ic_microsoft,
            title = "MICROSOFT",
            action = "+ ADICIONAR CONTA",
            onClick = onAddMicrosoftAccount
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ADICIONAR OFFLINE
        AddAccountCard(
            icon = R.drawable.ic_pirate,
            title = "CONTA OFFLINE",
            action = "+ ADICIONAR CONTA",
            onClick = {
                showOfflineDialog = true
            }
        )

        if (showOfflineDialog) {

            OfflineAccountDialog(
                onDismiss = {
                    showOfflineDialog = false
                },

                onConfirm = { username ->

                    val account = MinecraftAccount().apply {
                        this.username = username
                        this.accessToken = "0"
                    }

                    account.save()

                    PojavProfile.setCurrentProfile(
                        context,
                        username
                    )

                    currentAccount = account

                    accounts =
                        PojavProfile.getAllProfiles()

                    showOfflineDialog = false
                }
            )
        }
    }
}
@Composable
private fun OfflineAccountDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {

    var username by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    val focusRequester =
        remember {
            FocusRequester()
        }

    Dialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .width(310.dp)
                .clip(
                    RoundedCornerShape(6.dp)
                )
                .background(
                    Color(0xFF171717)
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFF343434),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(18.dp)
        ) {

            Column {

                Text(
                    text = "CONTA OFFLINE",
                    color = Color.White,
                    fontFamily = MinecraftBoldFont,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text = "Escolha o nick que será usado no jogo.",
                    color = Color(0xFF888888),
                    fontFamily = MinecraftFont,
                    fontSize = 8.sp
                )

                Spacer(
                    modifier =
                        Modifier.height(14.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .background(
                            Color(0xFF0D0D0D)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(
                            horizontal = 10.dp
                        ),
                    contentAlignment =
                        Alignment.CenterStart
                ) {

                    if (username.isEmpty()) {

                        Text(
                            text = "Nick",
                            color = Color(0xFF666666),
                            fontFamily = MinecraftFont,
                            fontSize = 9.sp
                        )
                    }

                    BasicTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            error = null
                        },
                        singleLine = true,
                        textStyle =
                            androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontFamily =
                                    MinecraftFont,
                                fontSize = 9.sp
                            ),
                        cursorBrush =
                            SolidColor(Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(
                                focusRequester
                            )
                    )
                }

                if (error != null) {

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text = error!!,
                        color = Color(0xFFFF6767),
                        fontFamily = MinecraftFont,
                        fontSize = 7.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.End
                ) {

                    Text(
                        text = "CANCELAR",
                        color = Color(0xFF888888),
                        fontFamily = MinecraftFont,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .clickable {
                                onDismiss()
                            }
                            .padding(8.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text = "ADICIONAR",
                        color = Color.White,
                        fontFamily = MinecraftBoldFont,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .clickable {

                                val normalized =
                                    username.trim()

                                when {

                                    normalized.isEmpty() -> {
                                        error =
                                            "Digite um nick."
                                    }

                                    normalized.length > 16 -> {
                                        error =
                                            "O nick deve ter no máximo 16 caracteres."
                                    }

                                    !normalized.matches(
                                        Regex(
                                            "^[A-Za-z0-9_]+$"
                                        )
                                    ) -> {
                                        error =
                                            "Use apenas letras, números e _."
                                    }

                                    PojavProfile
                                        .getAllProfiles()
                                        .any {
                                            it.username.equals(
                                                normalized,
                                                ignoreCase = true
                                            )
                                        } -> {
                                        error =
                                            "Já existe uma conta com esse nick."
                                    }

                                    else -> {
                                        onConfirm(
                                            normalized
                                        )
                                    }
                                }
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
@Composable
private fun AccountCard(
    account: MinecraftAccount,
    current: Boolean,
    onSwitch: () -> Unit,
    onDelete: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(51.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (current) {
                    Color(0xFF171717)
                } else {
                    Color(0xFF1C1C1C)
                }
            )
            .then(
                if (current) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(0xFF42D99A),
                        shape = RoundedCornerShape(6.dp)
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(
                if (account.isMicrosoft) {
                    R.drawable.ic_microsoft
                } else {
                    R.drawable.ic_pirate
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
        )

        Text(
            text = account.username,
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        )

        if (!current) {

            Image(
                painter = painterResource(R.drawable.ic_change),
                contentDescription = "Trocar conta",
                modifier = Modifier
                    .width(17.dp)
                    .clickable {
                        onSwitch()
                    }
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Image(
                painter = painterResource(R.drawable.ic_trash),
                contentDescription = "Excluir conta",
                modifier = Modifier
                    .width(17.dp)
                    .clickable {
                        onDelete()
                    }
            )
        }
    }
}
@Composable
private fun AddAccountCard(
    icon: Int,
    title: String,
    action: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(51.dp)
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(
                Color(0xFF1C1C1C)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier
                .width(24.dp)
                .height(24.dp)
        )

        Text(
            text = title,
            color = Color.White,
            fontFamily = MinecraftFont,
            fontSize = 10.sp,
            modifier = Modifier.padding(start = 12.dp)
        )

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = action,
            color = Color(0xFF888888),
            fontFamily = MinecraftFont,
            fontSize = 8.sp
        )
    }
}
