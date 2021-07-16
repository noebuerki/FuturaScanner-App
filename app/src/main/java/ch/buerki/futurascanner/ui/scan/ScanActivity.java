package ch.buerki.futurascanner.ui.scan;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Objects;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.database.local.dal.BlockDao;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.objects.Block;
import ch.buerki.futurascanner.database.local.objects.Item;
import ch.buerki.futurascanner.databinding.ActivityScanBinding;
import ch.buerki.futurascanner.ui.scan.helpers.FragmentEANDialog;
import ch.buerki.futurascanner.ui.scan.helpers.ItemAdapter;
import ch.buerki.futurascanner.ui.scan.helpers.scanner.Analyzer;
import ch.buerki.futurascanner.ui.scan.helpers.scanner.BeepPlayer;
import ch.buerki.futurascanner.ui.scan.helpers.scanner.PreviewHandler;

public class ScanActivity extends AppCompatActivity {

    private Context context;
    private ItemDao itemDao;
    private Analyzer analyzer;
    private Block currentBlock;
    private BeepPlayer beepPlayer;
    private PreviewHandler previewHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityScanBinding binding = ActivityScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        analyzer = new Analyzer();
        beepPlayer = new BeepPlayer(getApplicationContext());
        previewHandler = new PreviewHandler(findViewById(R.id.s_preview), this);

        BlockDao blockDao = AppDataBase.getDatabase(getApplicationContext()).blockDao();
        itemDao = AppDataBase.getDatabase(getApplicationContext()).itemDao();

        currentBlock = blockDao.getById(getIntent().getIntExtra("blockId", 0));
        binding.scTextBlock.setText("Block " + currentBlock.getNumber());

        binding.scListItems.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ItemAdapter itemAdapter = new ItemAdapter(new ItemAdapter.LocationDiff(), v -> AppDataBase.databaseWriteExecutor.execute(() -> itemDao.deleteById(Integer.parseInt(v.getTag().toString()))));
        binding.scListItems.setAdapter(itemAdapter);

        binding.scToggleTorch.setOnClickListener(v -> {
            if (previewHandler.toggleTorch()) {
                binding.scToggleTorch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_flash_on_36));
            } else {
                binding.scToggleTorch.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_flash_off_36));
            }
        });

        binding.scButtonScan.setOnClickListener(v -> {
            try {
                analyzer.analyze(previewHandler.getLatestFrame(), barcode -> {
                    AppDataBase.databaseWriteExecutor.execute(() -> itemDao.insert(new Item(currentBlock.getId(), Objects.requireNonNull(barcode.getRawValue()))));
                    beepPlayer.playShort();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.scButtonWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new FragmentEANDialog(currentBlock.getId(), itemDao, beepPlayer);
                dialogFragment.show(getSupportFragmentManager(), "AddEanFragment");
            }
        });

        itemDao.getByBlockIdAsLiveData(currentBlock.getId()).observe(this, items -> {
            boolean hasListChanged = false;
            int pos = 1;
            for (Item item : items) {
                if (item.getPosition() != pos) {
                    hasListChanged = true;
                    item.setPosition(pos);
                }
                pos++;
            }
            if (hasListChanged) {
                AppDataBase.databaseWriteExecutor.execute(() -> itemDao.update(items.toArray(new Item[0])));
            } else {
                binding.scTextCounter.setText(items.size() + " / " + currentBlock.getTargetQuantity());
                if (items.size() == currentBlock.getTargetQuantity()) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            beepPlayer.playLong();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
                itemAdapter.submitList(items);
                if (items.size() > 0) {
                    binding.scListItems.smoothScrollToPosition(items.size() - 1);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                analyzer.analyze(previewHandler.getLatestFrame(), barcode -> {
                    AppDataBase.databaseWriteExecutor.execute(() -> itemDao.insert(new Item(currentBlock.getId(), Objects.requireNonNull(barcode.getRawValue()))));
                    beepPlayer.playShort();
                });
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewHandler.quit();
        beepPlayer.quit();
    }
}