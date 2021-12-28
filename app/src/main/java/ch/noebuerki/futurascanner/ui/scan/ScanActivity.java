package ch.noebuerki.futurascanner.ui.scan;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Objects;

import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.objects.Block;
import ch.noebuerki.futurascanner.database.local.objects.Item;
import ch.noebuerki.futurascanner.databinding.ActivityScanBinding;
import ch.noebuerki.futurascanner.ui.scan.helpers.FragmentEANDialog;
import ch.noebuerki.futurascanner.ui.scan.helpers.ItemAdapter;
import ch.noebuerki.futurascanner.ui.scan.helpers.scanner.Analyzer;
import ch.noebuerki.futurascanner.ui.scan.helpers.scanner.BeepPlayer;
import ch.noebuerki.futurascanner.ui.scan.helpers.scanner.PreviewHandler;

public class ScanActivity extends AppCompatActivity {

    private long lastScan = 0;
    private ItemDao itemDao;
    private Analyzer analyzer;
    private Block currentBlock;
    private BeepPlayer beepPlayer;
    private PreviewHandler previewHandler;
    private boolean firstScroll = true;

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
        ItemAdapter itemAdapter = new ItemAdapter(new ItemAdapter.LocationDiff(), v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
            builder
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.confirm_delete_of_item))
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> AppDataBase.databaseWriteExecutor.execute(() -> itemDao.deleteById(Integer.parseInt(v.getTag().toString()))))
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });
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

        binding.scButtonWrite.setOnClickListener(v -> {
            DialogFragment dialogFragment = new FragmentEANDialog(currentBlock.getId(), itemDao, beepPlayer);
            dialogFragment.show(getSupportFragmentManager(), "AddEanFragment");
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
                if (items.size() == currentBlock.getTargetQuantity() && !firstScroll) {
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
                    if (firstScroll) {
                        firstScroll = false;
                        binding.scListItems.scrollToPosition(items.size() - 1);
                    } else {
                        binding.scListItems.smoothScrollToPosition(items.size() - 1);
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) && System.nanoTime() - lastScan >= 1000000000) {
            lastScan = System.nanoTime();
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