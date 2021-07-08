package ch.buerki.futurascanner.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.database.local.dal.SettingsDao;
import ch.buerki.futurascanner.database.local.objects.Settings;
import ch.buerki.futurascanner.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements TextWatcher {

    private Settings settings;
    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        SettingsDao settingsDao = AppDataBase.getDatabase(getContext()).settingsDao();

        List<Settings> settingsList = settingsDao.getAll();

        if (settingsList.size() == 1) {
            settings = settingsList.get(0);
        } else {
            settings = new Settings(1, "", 1, "");
            settingsDao.insert(settings);
        }

        binding.settingsFieldDevicenumber.setText(Integer.toString(settings.getDeviceNumber()));
        binding.settingsFieldDevicenumber.addTextChangedListener(this);
        binding.settingsFieldServerip.setText(settings.getServerIp());
        binding.settingsFieldServerip.addTextChangedListener(this);
        binding.settingsFieldBranch.setText(Integer.toString(settings.getBranch()));
        binding.settingsFieldBranch.addTextChangedListener(this);
        binding.settingsFieldDate.setText(settings.getDate());
        binding.settingsFieldDate.addTextChangedListener(this);

        binding.settingsUpdateButton.setOnClickListener(v -> {
            String deviceNumberString = binding.settingsFieldDevicenumber.getText().toString();
            String branchString = binding.settingsFieldBranch.getText().toString();
            settings.setDeviceNumber(Integer.parseInt(deviceNumberString));
            settings.setServerIp(binding.settingsFieldServerip.getText().toString());
            settings.setBranch(Integer.parseInt(branchString));
            settings.setDate(binding.settingsFieldDate.getText().toString());

            AppDataBase.databaseWriteExecutor.execute(() -> settingsDao.update(settings));
            afterTextChanged(null);
        });
        binding.settingsUpdateButton.setEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String deviceNumber = binding.settingsFieldDevicenumber.getText().toString();
        boolean isDeviceNumberEmpty = deviceNumber.equals("");
        boolean hasDeviceNumberChanged = false;
        if (!isDeviceNumberEmpty) {
            hasDeviceNumberChanged = settings.getDeviceNumber() != Integer.parseInt(deviceNumber);
        }

        String serverIp = binding.settingsFieldServerip.getText().toString();
        boolean isServerIpEmpty = serverIp.equals("");
        boolean hasServerIpChanged = !settings.getServerIp().equals(serverIp);


        String branch = binding.settingsFieldBranch.getText().toString();
        boolean isBranchEmpty = branch.equals("");
        boolean hasBranchChanged =false;
        if (!isBranchEmpty){
            hasBranchChanged = settings.getBranch() != Integer.parseInt(branch);
        }

        String date = binding.settingsFieldDate.getText().toString();
        boolean isDateEmpty = date.equals("");
        boolean hasDateChanged = !settings.getDate().equals(date);

        binding.settingsUpdateButton.setEnabled((!isDeviceNumberEmpty && !isServerIpEmpty && !isBranchEmpty && !isDateEmpty) && (hasDeviceNumberChanged || hasServerIpChanged || hasBranchChanged || hasDateChanged));
    }
}