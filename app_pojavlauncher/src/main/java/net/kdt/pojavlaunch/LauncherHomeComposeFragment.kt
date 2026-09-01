package net.kdt.pojavlaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

class LauncherHomeComposeFragment : Fragment() {

    private var loadingState =
        mutableStateOf(false)

    private var accountRefreshKey =
        mutableIntStateOf(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val launcherActivity =
            requireActivity() as LauncherActivity

        loadingState.value =
            launcherActivity.isLauncherLoading()

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {

                LauncherHomeScreen(
                    loading = loadingState.value,
                    accountRefreshKey = accountRefreshKey.intValue,

                    onPlay = {
                        launcherActivity.launchGameFromCompose()
                    },

                    onSettings = {
                        launcherActivity.showSettingsFromCompose()
                    },

                    onSocial = { urlResource ->
                        launcherActivity.openSocialLinkFromCompose(
                            urlResource
                        )
                    }
                )
            }
        }
    }

    fun setLoadingState(
        loading: Boolean
    ) {
        loadingState.value = loading
    }

    fun refreshAccount() {
        accountRefreshKey.intValue++
    }
}