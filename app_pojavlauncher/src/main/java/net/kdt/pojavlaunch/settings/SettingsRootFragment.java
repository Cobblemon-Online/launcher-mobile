package net.kdt.pojavlaunch.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.settings.fragments.AboutSettingsFragment;
import net.kdt.pojavlaunch.settings.fragments.AccountSettingsFragment;
import net.kdt.pojavlaunch.settings.fragments.LauncherSettingsFragment;
import net.kdt.pojavlaunch.settings.fragments.ModpackSettingsFragment;

public class SettingsRootFragment extends Fragment {

    private TextView mAccountTab;
    private TextView mModpackTab;
    private TextView mLauncherTab;
    private TextView mAboutTab;

    private SettingsTab mSelectedTab;

    public SettingsRootFragment() {
        super(R.layout.fragment_settings_root);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupNavigation();

        selectTab(SettingsTab.ACCOUNT);
    }

    private void bindViews(View view) {

        mAccountTab =
                view.findViewById(R.id.settings_tab_account);

        mModpackTab =
                view.findViewById(R.id.settings_tab_modpack);


        mLauncherTab =
                view.findViewById(R.id.settings_tab_launcher);

        mAboutTab =
                view.findViewById(R.id.settings_tab_about);
    }

    private void setupNavigation() {

        mAccountTab.setOnClickListener(
                view -> selectTab(SettingsTab.ACCOUNT)
        );

        mModpackTab.setOnClickListener(
                view -> selectTab(SettingsTab.MODPACK)
        );

        mLauncherTab.setOnClickListener(
                view -> selectTab(SettingsTab.LAUNCHER)
        );

        mAboutTab.setOnClickListener(
                view -> selectTab(SettingsTab.ABOUT)
        );

        requireView()
                .findViewById(R.id.settings_back)
                .setOnClickListener(view ->
                        ExtraCore.setValue(
                                ExtraConstants.BACK_PREFERENCE,
                                "true"
                        )
                );
    }

    private void selectTab(SettingsTab tab) {

        if (mSelectedTab == tab) {
            return;
        }

        mSelectedTab = tab;

        updateSidebar(tab);

        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.settings_tab_container,
                        createFragment(tab)
                )
                .commit();
    }

    private void updateSidebar(SettingsTab selected) {

        resetTab(mAccountTab);
        resetTab(mModpackTab);
        resetTab(mLauncherTab);
        resetTab(mAboutTab);

        TextView selectedView =
                getTabView(selected);

        selectedView.setTextColor(Color.WHITE);

        selectedView.setTextColor(Color.WHITE);
    }

    private void resetTab(TextView view) {

        view.setTextColor(
                Color.parseColor("#747474")
        );
    }

    private TextView getTabView(SettingsTab tab) {

        switch (tab) {

            case ACCOUNT:
                return mAccountTab;

            case MODPACK:
                return mModpackTab;

            case LAUNCHER:
                return mLauncherTab;

            case ABOUT:
            default:
                return mAboutTab;
        }
    }

    private Fragment createFragment(SettingsTab tab) {

        switch (tab) {

            case ACCOUNT:
                return new AccountSettingsFragment();

            case MODPACK:
                return new ModpackSettingsFragment();

            case LAUNCHER:
                return new LauncherSettingsFragment();

            case ABOUT:
            default:
                return new AboutSettingsFragment();
        }
    }
}