package ch.buerki.futurascanner.ui.scan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.database.local.dal.BlockDao;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.objects.Block;
import ch.buerki.futurascanner.database.local.objects.Item;
import ch.buerki.futurascanner.scanner.Analyzer;
import ch.buerki.futurascanner.scanner.BeepPlayer;
import ch.buerki.futurascanner.scanner.PreviewHandler;
import ch.buerki.futurascanner.ui.scan.helpers.ItemAdapter;

public class ScanActivity extends AppCompatActivity {

    private final Analyzer analyzer;
    private BeepPlayer beepPlayer;
    private PreviewHandler previewHandler;

    public ScanActivity() {
        analyzer = new Analyzer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        beepPlayer = new BeepPlayer(getApplicationContext());

        previewHandler = new PreviewHandler(findViewById(R.id.preview), this);

        BlockDao blockDao = AppDataBase.getDatabase(getApplicationContext()).blockDao();
        ItemDao itemDao = AppDataBase.getDatabase(getApplicationContext()).itemDao();

        Block currentBlock = blockDao.getById(getIntent().getIntExtra("blockId", 0));
        ((TextView) findViewById(R.id.s_text_block)).setText("Block " + currentBlock.getNumber());

        RecyclerView recyclerView = findViewById(R.id.s_item_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ItemAdapter itemAdapter = new ItemAdapter(new ItemAdapter.LocationDiff(), v -> AppDataBase.databaseWriteExecutor.execute(() -> itemDao.deleteById(Integer.parseInt(v.getTag().toString()))));
        recyclerView.setAdapter(itemAdapter);

        ImageView torchToggle = findViewById(R.id.torch_toggle);
        torchToggle.setOnClickListener(v -> {
            if (previewHandler.toggleTorch()) {
                torchToggle.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_flash_on_36));
            } else {
                torchToggle.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_flash_off_36));
            }
        });

        findViewById(R.id.s_button).setOnClickListener(v -> {
            try {
                analyzer.analyze(previewHandler.getLatestFrame(), barcode -> {
                    AppDataBase.databaseWriteExecutor.execute(() -> itemDao.insert(new Item(currentBlock.getId(), Objects.requireNonNull(barcode.getRawValue()))));
                    beepPlayer.playShort();
                });
            } catch (Exception e) {
                e.printStackTrace();
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
                ((TextView) findViewById(R.id.s_text_counter)).setText(items.size() + " / " + currentBlock.getTargetQuantity());
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
                    recyclerView.smoothScrollToPosition(items.size() - 1);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewHandler.quit();
        beepPlayer.quit();
    }
}