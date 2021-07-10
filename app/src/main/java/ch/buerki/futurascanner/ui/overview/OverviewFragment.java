package ch.buerki.futurascanner.ui.overview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.databinding.FragmentOverviewBinding;
import ch.buerki.futurascanner.ui.block.BlockActivity;
import ch.buerki.futurascanner.ui.overview.helpers.BlockAdapter;
import ch.buerki.futurascanner.ui.scan.ScanActivity;

public class OverviewFragment extends Fragment {

    private FragmentOverviewBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOverviewBinding.inflate(inflater, container, false);

        binding.oListBlocks.setLayoutManager(new LinearLayoutManager(getContext()));
        BlockAdapter blockAdapter = new BlockAdapter(new BlockAdapter.LocationDiff(), v -> {
            Intent intent = new Intent(getContext(), ScanActivity.class);
            intent.putExtra("blockId", Integer.parseInt(v.getTag().toString()));
            startActivity(intent);
        }, v -> {
            Intent intent = new Intent(getContext(), BlockActivity.class);
            intent.putExtra("blockId", Integer.parseInt(v.getTag().toString()));
            startActivity(intent);
        });
        binding.oListBlocks.setAdapter(blockAdapter);

        AppDataBase.getDatabase(getContext()).blockDao().getAllAsLiveData().observe(getViewLifecycleOwner(), blockAdapter::submitList);

        binding.oButtonAdd.setOnClickListener(v -> startActivity(new Intent(getContext(), BlockActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}