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
        binding.textBlockCountResult.setText(Integer.toString(blockList.size()));
        List<Item> itemList = itemDao.getAll();

        if (blockList.size() < 1 || itemList.size() < 1) {
            canTestConnection = false;
        }

        Settings settings = null;
        List<Settings> settingsList = settingsDao.getAll();
        if (!settingsList.isEmpty()) {
            settings = settingsList.get(0);

            binding.textDeviceNumberResult.setText(Integer.toString(settings.getDeviceNumber()));

            if (settings.getServerIp().equals("")) {
                canTestConnection = false;
                binding.textServerResult.setText("-");
            } else {
                binding.textServerResult.setText(settings.getServerIp());
            }

            binding.textBranchResult.setText(Integer.toString(settings.getBranch()));

            if (settings.getDate().equals("")) {
                canTestConnection = false;
                binding.textDateResult.setText("-");
            } else {
                binding.textDateResult.setText(settings.getDate());
            }
        } else {
            canTestConnection = false;
            binding.textDeviceNumberResult.setText("-");
            binding.textServerResult.setText("-");
            binding.textBranchResult.setText("-");
            binding.textDateResult.setText("-");
        }

        if (canTestConnection) {
            binding.buttonCheckConnection.setEnabled(true);
        }

        Connector connector = new Connector();
        Settings finalSettings = settings;

        binding.buttonCheckConnection.setOnClickListener(v -> {
            binding.checkConnectionOk.setVisibility(View.INVISIBLE);
            binding.checkConnectionFail.setVisibility(View.INVISIBLE);
            binding.progressCheckConnection.setVisibility(View.VISIBLE);
            connector.testConnection(finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.buttonSyncData.setEnabled(true);
                    binding.checkConnectionOk.setVisibility(View.VISIBLE);
                    binding.progressCheckConnection.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFail() {
                    binding.checkConnectionFail.setVisibility(View.VISIBLE);
                    binding.progressCheckConnection.setVisibility(View.INVISIBLE);
                }
            });
        });

        binding.buttonSyncData.setOnClickListener(v -> {
            StringConverter stringConverter = new StringConverter();
            String message = stringConverter.generateString(blockDao, itemDao, finalSettings);
            binding.syncDataOk.setVisibility(View.INVISIBLE);
            binding.syncDataFail.setVisibility(View.INVISIBLE);
            binding.progressSyncData.setVisibility(View.VISIBLE);
            connector.sendString(message, finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.buttonSyncData.setEnabled(false);
                    binding.buttonDeleteLocal.setEnabled(true);
                    binding.buttonCheckConnection.setEnabled(false);
                    binding.syncDataOk.setVisibility(View.VISIBLE);
                    binding.progressSyncData.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFail() {
                    binding.syncDataFail.setVisibility(View.VISIBLE);
                    binding.progressSyncData.setVisibility(View.INVISIBLE);
                }
            });
        });

        binding.buttonDeleteLocal.setOnClickListener(v -> {
            blockDao.deleteAll();
            itemDao.deleteAll();

            binding.buttonDeleteLocal.setEnabled(false);
            binding.deleteLocalOk.setVisibility(View.VISIBLE);
        });


        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}