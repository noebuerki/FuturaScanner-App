package ch.buerki.futurascanner.ui.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.database.local.dal.BlockDao;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.dal.SettingsDao;
import ch.buerki.futurascanner.database.local.objects.Block;
import ch.buerki.futurascanner.database.local.objects.Item;
import ch.buerki.futurascanner.database.local.objects.Settings;
import ch.buerki.futurascanner.database.server.Connector;
import ch.buerki.futurascanner.database.server.ConnectorCallback;
import ch.buerki.futurascanner.database.server.StringConverter;
import ch.buerki.futurascanner.databinding.FragmentSyncBinding;

public class SyncFragment extends Fragment {

    private FragmentSyncBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSyncBinding.inflate(inflater, container, false);

        ItemDao itemDao = AppDataBase.getDatabase(getContext()).itemDao();
        BlockDao blockDao = AppDataBase.getDatabase(getContext()).blockDao();
        SettingsDao settingsDao = AppDataBase.getDatabase(getContext()).settingsDao();

        boolean canTestConnection = true;

        List<Block> blockList = blockDao.getAll();
        binding.syncTextBlockcountResult.setText(Integer.toString(blockList.size()));
        List<Item> itemList = itemDao.getAll();

        if (blockList.size() < 1 || itemList.size() < 1) {
            canTestConnection = false;
        }

        Settings settings = null;
        List<Settings> settingsList = settingsDao.getAll();
        if (!settingsList.isEmpty()) {
            settings = settingsList.get(0);

            binding.syncTextDeviceNumberResult.setText(Integer.toString(settings.getDeviceNumber()));

            if (settings.getServerIp().equals("")) {
                canTestConnection = false;
                binding.syncTextServerResult.setText("-");
            } else {
                binding.syncTextServerResult.setText(settings.getServerIp());
            }

            binding.syncTextBranchResult.setText(Integer.toString(settings.getBranch()));

            if (settings.getDate().equals("")) {
                canTestConnection = false;
                binding.syncTextDateResult.setText("-");
            } else {
                binding.syncTextDateResult.setText(settings.getDate());
            }
        } else {
            canTestConnection = false;
            binding.syncTextDeviceNumberResult.setText("-");
            binding.syncTextServerResult.setText("-");
            binding.syncTextBranchResult.setText("-");
            binding.syncTextDateResult.setText("-");
        }

        if (canTestConnection) {
            binding.syncButtonCheckConnection.setEnabled(true);
        }

        Connector connector = new Connector();
        Settings finalSettings = settings;

        binding.syncButtonCheckConnection.setOnClickListener(v -> {
            connector.testConnection(finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syncButtonSyncData.setEnabled(true);
                    binding.syncConnectionOk.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail() {
                    binding.syncConnectionFailed.setVisibility(View.VISIBLE);
                }
            });
        });

        binding.syncButtonSyncData.setOnClickListener(v -> {
            StringConverter stringConverter = new StringConverter();
            String message = stringConverter.generateString(blockDao, itemDao, finalSettings);
            connector.sendString(message, finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syncButtonDeleteLocal.setEnabled(true);
                    binding.syncSyncOk.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFail() {
                    binding.syncSyncFailed.setVisibility(View.VISIBLE);
                }
            });
        });

        binding.syncButtonDeleteLocal.setOnClickListener(v -> {

            blockDao.deleteAll();
            itemDao.deleteAll();

            binding.syncDeleteOk.setVisibility(View.VISIBLE);

            binding.syncButtonCheckConnection.setEnabled(false);
            binding.syncButtonSyncData.setEnabled(false);
            binding.syncButtonDeleteLocal.setEnabled(false);
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}