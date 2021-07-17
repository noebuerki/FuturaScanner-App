package ch.buerki.futurascanner.ui.sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.List;

import ch.buerki.futurascanner.R;
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
        binding.syTextBlockCountResult.setText(Integer.toString(blockList.size()));
        List<Item> itemList = itemDao.getAll();

        if (blockList.size() < 1 || itemList.size() < 1) {
            canTestConnection = false;
        }

        Settings settings = null;
        List<Settings> settingsList = settingsDao.getAll();
        if (!settingsList.isEmpty()) {
            settings = settingsList.get(0);

            binding.syTextDeviceNumberResult.setText(Integer.toString(settings.getDeviceNumber()));

            if (settings.getServerIp().equals("")) {
                canTestConnection = false;
                binding.syTextServerResult.setText("-");
            } else {
                binding.syTextServerResult.setText(settings.getServerIp());
            }

            binding.syTextBranchResult.setText(Integer.toString(settings.getBranch()));

            if (settings.getDate().equals("")) {
                canTestConnection = false;
                binding.syTextDateResult.setText("-");
            } else {
                binding.syTextDateResult.setText(settings.getDate());
            }
        } else {
            canTestConnection = false;
            binding.syTextDeviceNumberResult.setText("-");
            binding.syTextServerResult.setText("-");
            binding.syTextBranchResult.setText("-");
            binding.syTextDateResult.setText("-");
        }

        if (canTestConnection) {
            binding.syButtonCheckConnection.setEnabled(true);
        }

        Connector connector = new Connector();
        Settings finalSettings = settings;

        binding.syButtonCheckConnection.setOnClickListener(v -> {
            binding.syImageCheckConnectionOk.setVisibility(View.INVISIBLE);
            binding.syImageCheckConnectionFail.setVisibility(View.INVISIBLE);
            binding.syProgressCheckConnection.setVisibility(View.VISIBLE);
            connector.testConnection(finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syButtonSyncData.setEnabled(true);
                    binding.syImageCheckConnectionOk.setVisibility(View.VISIBLE);
                    binding.syProgressCheckConnection.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFail() {
                    binding.syImageCheckConnectionFail.setVisibility(View.VISIBLE);
                    binding.syProgressCheckConnection.setVisibility(View.INVISIBLE);
                }
            });
        });

        binding.syButtonSyncData.setOnClickListener(v -> {
            StringConverter stringConverter = new StringConverter();
            String message = stringConverter.generateString(blockDao, itemDao, finalSettings);
            binding.syImageSyncDataOk.setVisibility(View.INVISIBLE);
            binding.syImageSyncDataFail.setVisibility(View.INVISIBLE);
            binding.syProgressSyncData.setVisibility(View.VISIBLE);
            connector.sendString(message, finalSettings, this.getActivity(), new ConnectorCallback() {
                @Override
                public void onSuccess() {
                    binding.syButtonSyncData.setEnabled(false);
                    binding.syButtonDeleteLocal.setEnabled(true);
                    binding.syButtonCheckConnection.setEnabled(false);
                    binding.syImageSyncDataOk.setVisibility(View.VISIBLE);
                    binding.syProgressSyncData.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onFail() {
                    binding.syImageSyncDataFail.setVisibility(View.VISIBLE);
                    binding.syProgressSyncData.setVisibility(View.INVISIBLE);
                }
            });
        });

        binding.syButtonDeleteLocal.setOnClickListener(v -> {
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