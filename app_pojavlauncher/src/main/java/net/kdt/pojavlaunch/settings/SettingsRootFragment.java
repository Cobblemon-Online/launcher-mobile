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
import net.kdt.pojavlaunch.settings.fragments.JavaSettingsFragment;
import net.kdt.pojavlaunch.settings.fragments.LauncherSettingsFragment;
import net.kdt.pojavlaunch.settings.fragments.MinecraftSettingsFragment;

public class SettingsRootFragment extends Fragment {

    private TextView mTitle;
    private TextView mSubtitle;

    private TextView mAccountTab;
    private TextView mMinecraftTab;
    private TextView mJavaTab;
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

        mTitle =
                view.findViewById(R.id.settings_title);

        mSubtitle =
                view.findViewById(R.id.settings_subtitle);

        mAccountTab =
                view.findViewById(R.id.settings_tab_account);

        mMinecraftTab =
                view.findViewById(R.id.settings_tab_minecraft);

        mJavaTab =
                view.findViewById(R.id.settings_tab_java);

        mLauncherTab =
                view.findViewById(R.id.settings_tab_launcher);

        mAboutTab =
                view.findViewById(R.id.settings_tab_about);
    }

    private void setupNavigation() {

        mAccountTab.setOnClickListener(
                view -> selectTab(SettingsTab.ACCOUNT)
        );

        mMinecraftTab.setOnClickListener(
                view -> selectTab(SettingsTab.MINECRAFT)
        );

        mJavaTab.setOnClickListener(
                view -> selectTab(SettingsTab.JAVA)
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

        updateHeader(tab);
        updateSidebar(tab);

        getChildFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.settings_tab_container,
                        createFragment(tab)
                )
                .commit();
    }

    private void updateHeader(SettingsTab tab) {

        mTitle.setText(tab.getTitle());
        mSubtitle.setText(tab.getSubtitle());
    }

    private void updateSidebar(SettingsTab selected) {

        resetTab(mAccountTab);
        resetTab(mMinecraftTab);
        resetTab(mJavaTab);
        resetTab(mLauncherTab);
        resetTab(mAboutTab);

        TextView selectedView =
                getTabView(selected);

        selectedView.setTextColor(Color.WHITE);

        selectedView.setBackgroundColor(
                Color.parseColor("#1C1C1C")
        );
    }

    private void resetTab(TextView view) {

        view.setTextColor(
                Color.parseColor("#747474")
        );

        view.setBackgroundColor(
                Color.TRANSPARENT
        );
    }

    private TextView getTabView(SettingsTab tab) {

        switch (tab) {

            case ACCOUNT:
                return mAccountTab;

            case MINECRAFT:
                return mMinecraftTab;

            case JAVA:
                return mJavaTab;

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

            case MINECRAFT:
                return new MinecraftSettingsFragment();

            case JAVA:
                return new JavaSettingsFragment();

            case LAUNCHER:
                return new LauncherSettingsFragment();

            case ABOUT:
            default:
                return new AboutSettingsFragment();
        }
    }
}