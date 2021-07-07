package ch.buerki.futurascanner.ui.settings;

import android.os.Bundle;
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

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        SettingsDao settingsDao = AppDataBase.getDatabase(getContext()).settingsDao();

        List<Settings> settingsList = settingsDao.getAll();

        Settings settings;
        if (settingsList.size() == 1) {
            settings = settingsList.get(0);
        } else {
            settings = new Settings(1, "", 1, "");
            settingsDao.insert(settings);
        }

        binding.settingsFieldDevicenumber.setText(Integer.toString(settings.getDeviceNumber()));
        binding.settingsFieldServerip.setText(settings.getServerIp());
        binding.settingsFieldBranch.setText(Integer.toString(settings.getBranch()));
        binding.settingsFieldDate.setText(settings.getDate());

        binding.settingsUpdateButton.setOnClickListener(v -> {
            String deviceNumberString = binding.settingsFieldDevicenumber.getText().toString();
            if (deviceNumberString.equals("")) {
                binding.settingsFieldDevicenumber.setText("1");
                deviceNumberString = "1";
            }
            settings.setDeviceNumber(Integer.parseInt(deviceNumberString));
            settings.setServerIp(binding.settingsFieldServerip.getText().toString());
            String branchString = binding.settingsFieldBranch.getText().toString();
            if (branchString.equals("")) {
                binding.settingsFieldBranch.setText("1");
                branchString = "1";
            }
            settings.setBranch(Integer.parseInt(branchString));
            settings.setDate(binding.settingsFieldDate.getText().toString());

            AppDataBase.databaseWriteExecutor.execute(() -> settingsDao.update(settings));
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}