package net.kdt.pojavlaunch.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment
import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension
import net.kdt.pojavlaunch.multirt.MultiRTConfigDialog
import net.kdt.pojavlaunch.ui.MicrosoftLoginActivity
import net.kdt.pojavlaunch.ui.OfflineLoginActivity

enum class PlayUpdateStatus {
    IDLE,
    CHECKING,
    UP_TO_DATE,
    AVAILABLE,
    DOWNLOADING,
    READY_TO_INSTALL,
    ERROR
}

data class PlayUpdateUiState(
    val status: PlayUpdateStatus = PlayUpdateStatus.IDLE,
    val availableVersionCode: Int? = null,
    val progressPercent: Int? = null,
    val message: String? = null
)

class SettingsComposeFragment : Fragment() {
    private var selectedTab by mutableStateOf(
        SettingsTab.ACCOUNT
    )

    private lateinit var appUpdateManager: AppUpdateManager
    private var availableUpdateInfo: AppUpdateInfo? = null
    private var updateState by mutableStateOf(PlayUpdateUiState())

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != Activity.RESULT_OK &&
            updateState.status != PlayUpdateStatus.DOWNLOADING
        ) {
            updateState = updateState.copy(
                status = PlayUpdateStatus.AVAILABLE,
                message = "Atualização cancelada. Você pode tentar novamente."
            )
        }
    }

    private val installStateListener = InstallStateUpdatedListener { state ->
        updateState = when (state.installStatus()) {
            InstallStatus.PENDING -> PlayUpdateUiState(
                status = PlayUpdateStatus.DOWNLOADING,
                message = "Preparando download pela Google Play..."
            )

            InstallStatus.DOWNLOADING -> {
                val totalBytes = state.totalBytesToDownload()
                val progress = if (totalBytes > 0L) {
                    ((state.bytesDownloaded() * 100L) / totalBytes).toInt()
                } else {
                    null
                }
                PlayUpdateUiState(
                    status = PlayUpdateStatus.DOWNLOADING,
                    progressPercent = progress,
                    message = "Baixando atualização pela Google Play..."
                )
            }

            InstallStatus.DOWNLOADED -> PlayUpdateUiState(
                status = PlayUpdateStatus.READY_TO_INSTALL,
                message = "Atualização pronta. Reinicie para concluir."
            )

            InstallStatus.FAILED -> PlayUpdateUiState(
                status = PlayUpdateStatus.ERROR,
                message = "A Google Play não conseguiu baixar a atualização."
            )

            InstallStatus.CANCELED -> PlayUpdateUiState(
                status = PlayUpdateStatus.AVAILABLE,
                message = "Download cancelado."
            )

            else -> updateState
        }
    }

    private var multiRtDialog: MultiRTConfigDialog? = null

    private val vmInstallLauncher =
        registerForActivityResult(
            OpenDocumentWithExtension("xz")
        ) { data ->

            if (data != null) {
                Tools.installRuntimeFromUri(
                    requireContext(),
                    data
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(requireContext())
    }

    override fun onStart() {
        super.onStart()
        appUpdateManager.registerListener(installStateListener)
        refreshPendingUpdate()
    }

    override fun onStop() {
        appUpdateManager.unregisterListener(installStateListener)
        super.onStop()
    }

    private fun checkForUpdate() {
        updateState = PlayUpdateUiState(
            status = PlayUpdateStatus.CHECKING,
            message = "Consultando a Google Play..."
        )

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener(::handleUpdateInfo)
            .addOnFailureListener {
                updateState = PlayUpdateUiState(
                    status = PlayUpdateStatus.ERROR,
                    message = "Não foi possível consultar a Google Play."
                )
            }
    }

    private fun refreshPendingUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.installStatus() == InstallStatus.DOWNLOADED) {
                updateState = PlayUpdateUiState(
                    status = PlayUpdateStatus.READY_TO_INSTALL,
                    message = "Atualização pronta. Reinicie para concluir."
                )
            }
        }
    }

    private fun handleUpdateInfo(info: AppUpdateInfo) {
        availableUpdateInfo = info
        updateState = when {
            info.installStatus() == InstallStatus.DOWNLOADED -> PlayUpdateUiState(
                status = PlayUpdateStatus.READY_TO_INSTALL,
                message = "Atualização pronta. Reinicie para concluir."
            )

            info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> PlayUpdateUiState(
                status = PlayUpdateStatus.AVAILABLE,
                availableVersionCode = info.availableVersionCode(),
                message = "Uma nova versão está disponível na Google Play."
            )

            else -> PlayUpdateUiState(
                status = PlayUpdateStatus.UP_TO_DATE,
                message = "Você está usando a versão mais recente."
            )
        }
    }

    private fun startFlexibleUpdate() {
        val info = availableUpdateInfo ?: run {
            checkForUpdate()
            return
        }

        runCatching {
            appUpdateManager.startUpdateFlowForResult(
                info,
                updateResultLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
            )
        }.onFailure {
            updateState = PlayUpdateUiState(
                status = PlayUpdateStatus.ERROR,
                message = "Não foi possível abrir a atualização da Google Play."
            )
        }
    }

    private fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    private fun openRuntimeManager() {

        val dialog =
            multiRtDialog
                ?: MultiRTConfigDialog().also {

                    it.prepare(
                        requireContext(),
                        vmInstallLauncher
                    )

                    multiRtDialog = it
                }

        dialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                SettingsScreen(
                    selectedTab = selectedTab,

                    onTabSelected = {
                        selectedTab = it
                    },

                    onBack = {
                        (requireActivity() as LauncherActivity)
                            .showHomeFromSettings()
                    },

                    onAddMicrosoftAccount = {

                        startActivity(
                            Intent(
                                requireContext(),
                                MicrosoftLoginActivity::class.java
                            )
                        )
                    },

                    onOpenRuntimeManager =
                        ::openRuntimeManager,
                    updateState = updateState,
                    onCheckForUpdate = ::checkForUpdate,
                    onStartUpdate = ::startFlexibleUpdate,
                    onCompleteUpdate = ::completeUpdate,
                    onAddOfflineAccount = {

                        startActivity(
                            Intent(
                                requireContext(),
                                OfflineLoginActivity::class.java
                            )
                        )
                    }
                )
            }
        }
    }
}
