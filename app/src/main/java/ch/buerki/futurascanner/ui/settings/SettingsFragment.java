package ch.buerki.futurascanner.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
            settings.setId(1);
            AppDataBase.databaseWriteExecutor.execute(() -> settingsDao.insert(settings));
            settings = settingsDao.getAll().get(0);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String deviceNumber = binding.seFieldDeviceNumber.getText().toString();
                boolean hasDeviceNumberChanged = false;
                if (!deviceNumber.equals("")) {
                    hasDeviceNumberChanged = settings.getDeviceNumber() != Integer.parseInt(deviceNumber);
                }

                String serverIp = binding.seFieldServerIp.getText().toString();
                boolean hasServerIpChanged = !settings.getServerIp().equals(serverIp);


                String branch = binding.seFieldBranch.getText().toString();
                boolean hasBranchChanged = false;
                if (!branch.equals("")) {
                    hasBranchChanged = settings.getBranch() != Integer.parseInt(branch);
                }

                String date = binding.seFieldDate.getText().toString();
                boolean hasDateChanged = !settings.getDate().equals(date);

                binding.seButtonUpdate.setEnabled((hasDeviceNumberChanged || hasServerIpChanged || hasBranchChanged || hasDateChanged));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        binding.seFieldDeviceNumber.setText(Integer.toString(settings.getDeviceNumber()));
        binding.seFieldDeviceNumber.addTextChangedListener(textWatcher);
        binding.seFieldServerIp.setText(settings.getServerIp());
        binding.seFieldServerIp.addTextChangedListener(textWatcher);
        binding.seFieldBranch.setText(Integer.toString(settings.getBranch()));
        binding.seFieldBranch.addTextChangedListener(textWatcher);
        binding.seFieldDate.setText(settings.getDate());
        binding.seFieldDate.addTextChangedListener(textWatcher);

        binding.seButtonUpdate.setOnClickListener(v -> {
            String deviceNumberString = binding.seFieldDeviceNumber.getText().toString();
            String branchString = binding.seFieldBranch.getText().toString();
            settings.setDeviceNumber(Integer.parseInt(deviceNumberString));
            settings.setServerIp(binding.seFieldServerIp.getText().toString());
            settings.setBranch(Integer.parseInt(branchString));
            settings.setDate(binding.seFieldDate.getText().toString());

            AppDataBase.databaseWriteExecutor.execute(() -> settingsDao.update(settings));
            textWatcher.onTextChanged("", 0, 0, 0);
        });
        binding.seButtonUpdate.setEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}