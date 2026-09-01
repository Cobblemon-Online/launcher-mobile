package net.kdt.pojavlaunch.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment

class SettingsComposeFragment : Fragment() {

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
                    onBack = {
                        (requireActivity() as LauncherActivity)
                            .showHomeFromSettings()
                    },

                    onAddMicrosoftAccount = {

                        Tools.swapFragment(
                            requireActivity(),
                            MicrosoftLoginFragment::class.java,
                            MicrosoftLoginFragment.TAG,
                            null
                        )
                    }
                )
            }
        }
    }
}