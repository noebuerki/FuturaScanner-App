package ch.buerki.futurascanner.ui.block;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.buerki.futurascanner.MainActivity;
import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.AppDataBase;
import ch.buerki.futurascanner.database.local.dal.BlockDao;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.objects.Block;

public class BlockActivity extends AppCompatActivity {

    private ItemDao itemDao;
    private BlockDao blockDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        blockDao = AppDataBase.getDatabase(getApplicationContext()).blockDao();

        Intent intent = getIntent();
        if (intent.hasExtra("blockId")) {
            setTitle("Block bearbeiten");
            int blockId = getIntent().getIntExtra("blockId", 0);

            itemDao = AppDataBase.getDatabase(getApplicationContext()).itemDao();
            Block currentBlock = blockDao.getById(blockId);

            ((TextView) findViewById(R.id.b_field_number)).setText(Integer.toString(currentBlock.getNumber()));
            ((TextView) findViewById(R.id.b_field_expected_quantity)).setText(Integer.toString(currentBlock.getTargetQuantity()));

            Button saveButton = findViewById(R.id.b_save_button);
            saveButton.setText("Aktualisieren");

            saveButton.setOnClickListener(v -> {
                String blockNumberString = ((TextView) findViewById(R.id.b_field_number)).getText().toString();
                String targetQuantityString = ((TextView) findViewById(R.id.b_field_expected_quantity)).getText().toString();

                if (!blockNumberString.equals("") && !targetQuantityString.equals("")) {
                    int blockNumber = Integer.parseInt(blockNumberString);
                    int targetQuantity = Integer.parseInt(targetQuantityString);

                    if (blockNumber >= 1 && targetQuantity >= 1) {
                        Block dataBaseBlock = blockDao.getByNumber(blockNumber);

                        if (dataBaseBlock == null || currentBlock.getNumber() == blockNumber) {
                            currentBlock.setNumber(blockNumber);
                            currentBlock.setTargetQuantity(targetQuantity);

                            AppDataBase.databaseWriteExecutor.execute(() -> blockDao.update(currentBlock));

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Blocknummer bereits vergeben", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Werte müssen grösser gleich 1 sein", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Werte dürfen nicht Leer sein", Toast.LENGTH_SHORT).show();
                }
            });

            findViewById(R.id.b_delete_button).setOnClickListener(v -> AppDataBase.databaseWriteExecutor.execute(() -> {
                blockDao.delete(currentBlock);
                itemDao.deleteByBlockNumber(currentBlock.getNumber());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }));

        } else {
            Button saveButton = findViewById(R.id.b_save_button);

            saveButton.setOnClickListener(v -> {
                String blockNumberString = ((TextView) findViewById(R.id.b_field_number)).getText().toString();
                String targetQuantityString = ((TextView) findViewById(R.id.b_field_expected_quantity)).getText().toString();

                if (!blockNumberString.equals("") && !targetQuantityString.equals("")) {
                    int blockNumber = Integer.parseInt(blockNumberString);
                    int targetQuantity = Integer.parseInt(targetQuantityString);

                    if (blockNumber >= 1 && targetQuantity >= 1) {
                        Block dataBaseBlock = blockDao.getByNumber(blockNumber);

                        if (dataBaseBlock == null) {
                            Block block = new Block(blockNumber, targetQuantity);

                            AppDataBase.databaseWriteExecutor.execute(() -> blockDao.insert(block));

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Blocknummer bereits vergeben", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Werte müssen grösser gleich 1 sein", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Werte dürfen nicht Leer sein", Toast.LENGTH_SHORT).show();
                }
            });
            findViewById(R.id.b_delete_button).setVisibility(View.GONE);
        }
    }
}