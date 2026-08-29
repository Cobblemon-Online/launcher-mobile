package net.kdt.pojavlaunch.settings.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.LauncherActivity;
import net.kdt.pojavlaunch.PojavProfile;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.fragments.MicrosoftLoginFragment;
import net.kdt.pojavlaunch.value.MinecraftAccount;

import java.util.List;

public class AccountSettingsFragment extends Fragment {

    private TextView mCurrentEmail;

    private FrameLayout mAddMicrosoft;
    private FrameLayout mAddOffline;

    private ImageView mCurrentIcon;

    private LinearLayout mAccountList;

    public AccountSettingsFragment() {
        super(R.layout.fragment_settings_account);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupListeners();
        loadCurrentAccount();
        loadOtherAccounts();
    }

    private void loadOtherAccounts() {

        mAccountList.removeAllViews();

        String currentProfile =
                PojavProfile.getCurrentProfileName(
                        requireContext()
                );

        List<MinecraftAccount> accounts =
                PojavProfile.getAllProfiles();

        for (MinecraftAccount account : accounts) {

            // Não mostra a conta que já está em "Conta atual"
            if (account.username.equals(currentProfile)) {
                continue;
            }

            addAccountCard(account);
        }
    }
    private void addAccountCard(MinecraftAccount account) {

        View card =
                LayoutInflater.from(requireContext())
                        .inflate(
                                R.layout.item_account_existing,
                                mAccountList,
                                false
                        );

        ImageView icon =
                card.findViewById(
                        R.id.account_existing_icon
                );

        TextView email =
                card.findViewById(
                        R.id.account_existing_email
                );

        ImageView switchButton =
                card.findViewById(
                        R.id.account_existing_switch
                );

        ImageView deleteButton =
                card.findViewById(
                        R.id.account_existing_delete
                );

        // Ícone conforme o método de login
        if (account.isMicrosoft) {
            icon.setImageResource(R.drawable.ic_microsoft);
        } else {
            icon.setImageResource(R.drawable.ic_pirate);
        }

        email.setText(account.username);

        switchButton.setOnClickListener(
                view -> switchAccount(account)
        );

        card.setOnClickListener(
                view -> switchAccount(account)
        );

        deleteButton.setOnClickListener(
                view -> deleteAccount(account)
        );

        mAccountList.addView(card);

        LinearLayout.LayoutParams params =
                (LinearLayout.LayoutParams) card.getLayoutParams();

        params.topMargin =
                (int) (
                        10 * getResources()
                                .getDisplayMetrics()
                                .density
                );

        card.setLayoutParams(params);
    }

    private void bindViews(View view) {

        mCurrentEmail =
                view.findViewById(R.id.account_current_email);

        mAccountList =
                view.findViewById(R.id.account_list);

        mAddMicrosoft =
                view.findViewById(R.id.account_add_microsoft);

        mAddOffline =
                view.findViewById(R.id.account_add_offline);

        mCurrentIcon =
                view.findViewById(R.id.account_current_icon);
    }

    private void setupListeners() {

        mAddMicrosoft.setOnClickListener(
                view -> addMicrosoftAccount()
        );

        mAddOffline.setOnClickListener(
                view -> addOfflineAccount()
        );
    }

    private void addMicrosoftAccount() {

        Tools.swapFragment(
                requireActivity(),
                MicrosoftLoginFragment.class,
                MicrosoftLoginFragment.TAG,
                null
        );
    }

    private void loadCurrentAccount() {

        MinecraftAccount account =
                PojavProfile.getCurrentProfileContent(
                        requireContext(),
                        null
                );

        if (account == null) {
            mCurrentEmail.setText("Nenhuma conta");
            return;
        }

        mCurrentEmail.setText(account.username);

        if (account.isMicrosoft) {
            mCurrentIcon.setImageResource(
                    R.drawable.ic_microsoft
            );
        } else {
            mCurrentIcon.setImageResource(
                    R.drawable.ic_pirate
            );
        }
    }
    private void switchAccount(MinecraftAccount account) {

        PojavProfile.setCurrentProfile(
                requireContext(),
                account.username
        );

        loadCurrentAccount();
        loadOtherAccounts();

        ((LauncherActivity) requireActivity())
                .refreshAccountSelection();
    }

    private void deleteAccount(MinecraftAccount account) {

        // Ainda vamos implementar a exclusão.
    }

    private void addOfflineAccount() {

        /*
         * Implementaremos usando o fluxo offline original.
         */
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCurrentEmail != null) {
            loadCurrentAccount();
        }
    }
}