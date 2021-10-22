package ch.noebuerki.futurascanner.ui.block;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ch.noebuerki.futurascanner.MainActivity;
import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.objects.Block;

public class BlockActivity extends AppCompatActivity {

    private ItemDao itemDao;
    private BlockDao blockDao;
    private Block currentBlock = new Block(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        blockDao = AppDataBase.getDatabase(getApplicationContext()).blockDao();

        Button saveButton = findViewById(R.id.b_button_save);
        ImageView deleteButton = findViewById(R.id.b_button_delete);

        TextView blockNumberField = findViewById(R.id.b_field_number);
        TextView targetQuantityField = findViewById(R.id.b_field_expected_quantity);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String blockNumberString = blockNumberField.getText().toString();
                boolean isBlockNumberValid = false;
                boolean isBlockNumberTaken = true;
                boolean hasBlockNumberChanged = false;
                if (!blockNumberString.equals("")) {
                    int blockNumber = Integer.parseInt(blockNumberString);
                    isBlockNumberValid = blockNumber >= 1;
                    isBlockNumberTaken = blockDao.getByNumber(blockNumber) != null;
                    hasBlockNumberChanged = currentBlock.getNumber() != blockNumber;
                }

                String targetQuantityString = targetQuantityField.getText().toString();
                boolean isTargetQuantityValid = false;
                boolean hasTargetQuantityChanged = false;
                if (!targetQuantityString.equals("")) {
                    int targetQuantityInteger = Integer.parseInt(targetQuantityString);
                    isTargetQuantityValid = targetQuantityInteger >= 1;
                    hasTargetQuantityChanged = currentBlock.getTargetQuantity() != targetQuantityInteger;
                }

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
        };

        blockNumberField.addTextChangedListener(textWatcher);
        targetQuantityField.addTextChangedListener(textWatcher);

        Intent intent = getIntent();

        if (intent.hasExtra("blockId")) {
            setTitle(getString(R.string.edit_block));
            int blockId = getIntent().getIntExtra("blockId", 0);

            itemDao = AppDataBase.getDatabase(getApplicationContext()).itemDao();
            currentBlock = blockDao.getById(blockId);

            blockNumberField.setText(Integer.toString(currentBlock.getNumber()));
            targetQuantityField.setText(Integer.toString(currentBlock.getTargetQuantity()));

            saveButton.setText(getString(R.string.update));

            saveButton.setOnClickListener(v -> {
                currentBlock.setNumber(Integer.parseInt(blockNumberField.getText().toString()));
                currentBlock.setTargetQuantity(Integer.parseInt(targetQuantityField.getText().toString()));

                AppDataBase.databaseWriteExecutor.execute(() -> blockDao.update(currentBlock));
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });

            deleteButton.setOnClickListener(v -> AppDataBase.databaseWriteExecutor.execute(() -> {
                blockDao.delete(currentBlock);
                itemDao.deleteByBlockId(currentBlock.getId());
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }));

        } else {
            saveButton.setOnClickListener(v -> {
                Block block = new Block(
                        Integer.parseInt(blockNumberField.getText().toString()),
                        Integer.parseInt(targetQuantityField.getText().toString()));

                AppDataBase.databaseWriteExecutor.execute(() -> blockDao.insert(block));

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            });
            deleteButton.setVisibility(View.GONE);
        }
    }
}