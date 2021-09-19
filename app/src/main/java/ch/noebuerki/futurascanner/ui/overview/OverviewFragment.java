package ch.noebuerki.futurascanner.ui.overview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.databinding.FragmentOverviewBinding;
import ch.noebuerki.futurascanner.ui.block.BlockActivity;
import ch.noebuerki.futurascanner.ui.overview.helpers.BlockAdapter;
import ch.noebuerki.futurascanner.ui.scan.ScanActivity;

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
        }, AppDataBase.getDatabase(getContext()).itemDao());
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