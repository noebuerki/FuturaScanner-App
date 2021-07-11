package ch.buerki.futurascanner.ui.block;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class BlockActivity extends AppCompatActivity implements TextWatcher {

    private ItemDao itemDao;
    private BlockDao blockDao;
    private Block currentBlock;

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
            currentBlock = blockDao.getById(blockId);

            TextView fieldNumber = ((TextView) findViewById(R.id.b_field_number));
            fieldNumber.setText(Integer.toString(currentBlock.getNumber()));
            fieldNumber.addTextChangedListener(this);

            TextView targetQuantity = ((TextView) findViewById(R.id.b_field_expected_quantity));
            targetQuantity.setText(Integer.toString(currentBlock.getTargetQuantity()));
            targetQuantity.addTextChangedListener(this);

            Button saveButton = findViewById(R.id.b_button_save);
            saveButton.setText("Aktualisieren");

            saveButton.setOnClickListener(v -> {
                String blockNumberString = ((TextView) findViewById(R.id.b_field_number)).getText().toString();
                String targetQuantityString = ((TextView) findViewById(R.id.b_field_expected_quantity)).getText().toString();

                if (!blockNumberString.equals("") && !targetQuantityString.equals("")) {
                    int blockNumber = Integer.parseInt(blockNumberString);
                    int targetQuantityInteger = Integer.parseInt(targetQuantityString);

                    if (blockNumber >= 1 && targetQuantityInteger >= 1) {
                        if (blockNumber != currentBlock.getNumber() || targetQuantityInteger != currentBlock.getTargetQuantity()) {
                            Block dataBaseBlock = blockDao.getByNumber(blockNumber);

                            if (dataBaseBlock == null || currentBlock.getNumber() == blockNumber) {
                                currentBlock.setNumber(blockNumber);
                                currentBlock.setTargetQuantity(targetQuantityInteger);

                                AppDataBase.databaseWriteExecutor.execute(() -> blockDao.update(currentBlock));

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "Blocknummer bereits vergeben!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Keine veränderten Werte!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Werte müssen grösser gleich 1 sein!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Werte dürfen nicht Leer sein!", Toast.LENGTH_SHORT).show();
                }
            });

            findViewById(R.id.b_button_delete).setOnClickListener(v -> AppDataBase.databaseWriteExecutor.execute(() -> {
                blockDao.delete(currentBlock);
                itemDao.deleteByBlockNumber(currentBlock.getNumber());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }));

        } else {
            Button saveButton = findViewById(R.id.b_button_save);

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
            findViewById(R.id.b_button_delete).setVisibility(View.GONE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String blockNumberString = ((TextView) findViewById(R.id.b_field_number)).getText().toString();
        boolean isBlockNumberValid = false;
        boolean isBlockNumberTaken = true;
        boolean hasBlockNumberChanged = false;
        if (!blockNumberString.equals("")) {
            int blockNumber = Integer.parseInt(blockNumberString);
            isBlockNumberValid = blockNumber >= 1;
            isBlockNumberTaken = blockDao.getByNumber(blockNumber) != null;
            hasBlockNumberChanged = currentBlock.getNumber() != blockNumber;
        }

        String targetQuantityString = ((TextView) findViewById(R.id.b_field_expected_quantity)).getText().toString();
        boolean isTargetQuantityValid = false;
        boolean hasTargetQuantityChanged = false;
        if (!targetQuantityString.equals("")) {
            int targetQuantityInteger = Integer.parseInt(targetQuantityString);
            isTargetQuantityValid = targetQuantityInteger >= 1;
            hasTargetQuantityChanged = currentBlock.getTargetQuantity() != targetQuantityInteger;
        }

        Button saveButton = findViewById(R.id.b_button_save);
        if (isBlockNumberValid && isTargetQuantityValid) {
            if (((!hasBlockNumberChanged && hasTargetQuantityChanged) || (hasBlockNumberChanged && !isBlockNumberTaken)) || hasTargetQuantityChanged) {
                saveButton.setEnabled(true);
                return;
            }
        }
        saveButton.setEnabled(false);
    }


    @Override
    public void afterTextChanged(Editable s) {

    }
}