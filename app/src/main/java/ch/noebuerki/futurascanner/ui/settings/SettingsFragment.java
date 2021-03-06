package ch.noebuerki.futurascanner.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.regex.Pattern;

import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.database.local.dal.SettingsDao;
import ch.noebuerki.futurascanner.database.local.objects.Settings;
import ch.noebuerki.futurascanner.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private Settings settings;
    private boolean defaultSettings = false;
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
        }

        if (settings.equals(new Settings(1, "", 1, ""))) {
            defaultSettings = true;
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasDeviceNumberChanged = !Integer.toString(settings.getDeviceNumber()).equals(binding.deviceNumberField.getText().toString()) && !binding.deviceNumberField.getText().toString().equals("");

                boolean hasServerIpChanged = !settings.getServerIp().equals(binding.serverIpField.getText().toString()) && !binding.serverIpField.getText().toString().equals("");

                boolean hasBranchChanged = !Integer.toString(settings.getBranch()).equals(binding.branchField.getText().toString()) && !binding.branchField.getText().toString().equals("");

                boolean hasDateChanged = !settings.getDate().equals(binding.dateField.getText().toString()) && !binding.dateField.getText().toString().equals("");

                if (defaultSettings) {
                    binding.updateButton.setEnabled(hasServerIpChanged && hasDateChanged);
                } else {
                    binding.updateButton.setEnabled(hasDeviceNumberChanged || hasServerIpChanged || hasBranchChanged || hasDateChanged);
                }
            }
        };

        binding.deviceNumberField.setText(Integer.toString(settings.getDeviceNumber()));
        binding.deviceNumberField.addTextChangedListener(textWatcher);
        binding.serverIpField.setText(settings.getServerIp());
        binding.serverIpField.addTextChangedListener(textWatcher);
        binding.branchField.setText(Integer.toString(settings.getBranch()));
        binding.branchField.addTextChangedListener(textWatcher);
        binding.dateField.setText(settings.getDate());
        binding.dateField.addTextChangedListener(textWatcher);

        binding.updateButton.setOnClickListener(v -> {
            EditText deviceNumberField = binding.deviceNumberField;
            String deviceNumber = deviceNumberField.getText().toString();
            boolean isDeviceNumberValid = false;
            if (Pattern.matches("^[1-9]{1}[0-9]{0,}$", deviceNumber)) {
                settings.setDeviceNumber(Integer.parseInt(deviceNumber));
                deviceNumberField.setError(null);
                isDeviceNumberValid = true;
            } else {
                deviceNumberField.setError(requireContext().getString(R.string.numeric_value_required));
            }

            EditText serverIpField = binding.serverIpField;
            String serverIp = serverIpField.getText().toString();
            boolean isServerIpValid = false;
            if (Pattern.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$", serverIp)) {
                settings.setServerIp(serverIp);
                serverIpField.setError(null);
                isServerIpValid = true;
            } else {
                serverIpField.setError(requireContext().getString(R.string.valid_ip_required));
            }

            EditText branchField = binding.branchField;
            String branch = branchField.getText().toString();
            boolean isBranchValid = false;
            if (Pattern.matches("^[1-9]{1}[0-9]{0,}$", branch)) {
                settings.setBranch(Integer.parseInt(branch));
                branchField.setError(null);
                isBranchValid = true;
            } else {
                branchField.setError(requireContext().getString(R.string.valid_branch_required));
            }

            EditText dateField = binding.dateField;
            String date = dateField.getText().toString();
            boolean isDateValid = false;
            if (Pattern.matches("^\\s*(3[01]|[12][0-9]|0?[1-9])\\.(1[012]|0?[1-9])\\.((?:19|20)\\d{2})\\s*$", date)) {
                settings.setDate(date);
                dateField.setError(null);
                isDateValid = true;
            } else {
                dateField.setError(requireContext().getString(R.string.valid_date_required));
            }

            if (isDeviceNumberValid && isServerIpValid && isBranchValid && isDateValid) {
                AppDataBase.databaseWriteExecutor.execute(() -> settingsDao.update(settings));
                textWatcher.afterTextChanged(null);
                defaultSettings = false;
            }
        });
        binding.updateButton.setEnabled(false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}