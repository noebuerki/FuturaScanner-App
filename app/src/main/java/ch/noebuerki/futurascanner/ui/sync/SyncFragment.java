package ch.noebuerki.futurascanner.ui.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.dal.SettingsDao;
import ch.noebuerki.futurascanner.database.local.objects.Item;
import ch.noebuerki.futurascanner.database.local.objects.Settings;
import ch.noebuerki.futurascanner.database.server.Connector;
import ch.noebuerki.futurascanner.database.server.ConnectorCallback;
import ch.noebuerki.futurascanner.database.server.StringConverter;
import ch.noebuerki.futurascanner.databinding.FragmentSyncBinding;

public class SyncFragment extends Fragment {

    private FragmentSyncBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);

        BlockDao blockDao = AppDataBase.getDatabase(getContext()).blockDao();
        ItemDao itemDao = AppDataBase.getDatabase(getContext()).itemDao();
        SettingsDao settingsDao = AppDataBase.getDatabase(getContext()).settingsDao();

        boolean canTestConnection = true;

        List<Item> itemList = itemDao.getAll();
        binding.itemCount.setText(Integer.toString(itemList.size()));

        if (itemList.size() < 1) {
            canTestConnection = false;
        }

        Settings settings = null;
        List<Settings> settingsList = settingsDao.getAll();
        if (!settingsList.isEmpty()) {
            settings = settingsList.get(0);

            binding.deviceNumber.setText(Integer.toString(settings.getDeviceNumber()));

            if (settings.getServerIp().equals("")) {
                canTestConnection = false;
                binding.serverIp.setText("-");
            } else {
                binding.serverIp.setText(settings.getServerIp());
            }

            binding.branch.setText(Integer.toString(settings.getBranch()));

            if (settings.getDate().equals("")) {
                canTestConnection = false;
                binding.date.setText("-");
            } else {
                binding.date.setText(settings.getDate());
            }
        } else {
            canTestConnection = false;
            binding.itemCount.setText("-");
            binding.serverIp.setText("-");
            binding.branch.setText("-");
            binding.date.setText("-");
        }

        if (canTestConnection) {
            binding.checkConnectionButton.setEnabled(true);
        }

        Connector connector = new Connector();
        Settings finalSettings = settings;

        binding.checkConnectionButton.setOnClickListener(v -> {
            binding.checkConnectionOkImage.setVisibility(View.INVISIBLE);
            binding.checkConnectionFailImage.setVisibility(View.INVISIBLE);
            binding.checkConnectionProgress.setVisibility(View.VISIBLE);
            connector.testConnection(finalSettings, getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syncDataButton.setEnabled(true);
                    binding.checkConnectionOkImage.setVisibility(View.VISIBLE);
                    binding.checkConnectionFailImage.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFail() {
                    binding.checkConnectionFailImage.setVisibility(View.VISIBLE);
                    binding.checkConnectionOkImage.setVisibility(View.INVISIBLE);
                }
            });
        });

        binding.syncDataButton.setOnClickListener(v -> {
            StringConverter stringConverter = new StringConverter();
            String message = stringConverter.generateString(blockDao, itemDao, finalSettings);
            binding.syncDataOkImage.setVisibility(View.INVISIBLE);
            binding.syncDataFailImage.setVisibility(View.INVISIBLE);
            binding.syncDataProgress.setVisibility(View.VISIBLE);

            binding.checkConnectionButton.setEnabled(false);
            requireActivity().findViewById(R.id.navigation_scan).setClickable(false);
            requireActivity().findViewById(R.id.navigation_sync).setClickable(false);
            requireActivity().findViewById(R.id.navigation_settings).setClickable(false);

            connector.sendString(message, finalSettings, getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syncDataButton.setEnabled(false);
                    binding.deleteLocalButton.setEnabled(true);
                    binding.checkConnectionButton.setEnabled(false);
                    binding.syncDataOkImage.setVisibility(View.VISIBLE);
                    binding.syncDataProgress.setVisibility(View.INVISIBLE);

                    requireActivity().findViewById(R.id.navigation_scan).setClickable(true);
                    requireActivity().findViewById(R.id.navigation_sync).setClickable(true);
                    requireActivity().findViewById(R.id.navigation_settings).setClickable(true);
                }

                @Override
                public void onFail() {
                    binding.syncDataFailImage.setVisibility(View.VISIBLE);
                    binding.syncDataProgress.setVisibility(View.INVISIBLE);

                    binding.checkConnectionButton.setEnabled(false);
                    requireActivity().findViewById(R.id.navigation_scan).setClickable(true);
                    requireActivity().findViewById(R.id.navigation_sync).setClickable(true);
                    requireActivity().findViewById(R.id.navigation_settings).setClickable(true);
                }
            });
        });

        binding.deleteLocalButton.setOnClickListener(v -> {
            blockDao.deleteAll();
            itemDao.deleteAll();

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(R.id.navigation_sync);
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}