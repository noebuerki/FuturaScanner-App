package ch.noebuerki.futurascanner.ui.block;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import ch.noebuerki.futurascanner.MainActivity;
import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.AppDataBase;
import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.objects.Block;
import ch.noebuerki.futurascanner.databinding.ActivityBlockBinding;

public class BlockActivity extends AppCompatActivity {

    private ItemDao itemDao;
    private BlockDao blockDao;
    private boolean isUpdateView = false;
    private Block currentBlock = new Block(0, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBlockBinding binding = ActivityBlockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isUpdateView = getIntent().hasExtra("blockId");

        blockDao = AppDataBase.getDatabase(getApplicationContext()).blockDao();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean hasBlockNumberChanged;
                if (isUpdateView) {
                    hasBlockNumberChanged = !Integer.toString(currentBlock.getNumber()).equals(binding.blockNumberField.getText().toString()) && !binding.blockNumberField.getText().toString().equals("");
                } else {
                    hasBlockNumberChanged = !"".equals(binding.blockNumberField.getText().toString());
                }

                boolean hasTargetQuantityChanged;
                if (isUpdateView) {
                    hasTargetQuantityChanged = !Integer.toString(currentBlock.getTargetQuantity()).equals(binding.targetQuantityField.getText().toString()) && !binding.targetQuantityField.getText().toString().equals("");
                } else {
                    hasTargetQuantityChanged = !"".equals(binding.targetQuantityField.getText().toString());
                }

                if (isUpdateView) {
                    binding.saveButton.setEnabled(hasBlockNumberChanged || hasTargetQuantityChanged);
                } else {
                    binding.saveButton.setEnabled(hasBlockNumberChanged && hasTargetQuantityChanged);
                }
            }
        };

        binding.blockNumberField.addTextChangedListener(textWatcher);
        binding.targetQuantityField.addTextChangedListener(textWatcher);

        binding.saveButton.setOnClickListener(v -> {
            EditText blockNumberField = binding.blockNumberField;
            String blockNumber = blockNumberField.getText().toString();
            boolean isBlockNumberValid = false;
            if (Pattern.matches("^[1-9]{1}[0-9]{0,}$", blockNumber)) {
                Block blockFromDatabase = blockDao.getByNumber(Integer.parseInt(blockNumber));
                if (blockFromDatabase == null || blockFromDatabase.getId() == currentBlock.getId()) {
                    currentBlock.setNumber(Integer.parseInt(blockNumber));
                    blockNumberField.setError(null);
                    isBlockNumberValid = true;
                } else {
                    blockNumberField.setError(getString(R.string.block_number_taken));
                }
            } else {
                blockNumberField.setError(getString(R.string.numeric_value_required));
            }

            EditText targetQuantityField = binding.targetQuantityField;
            String targetQuantity = targetQuantityField.getText().toString();
            boolean isTargetQuantityValid = false;
            if (Pattern.matches("^[1-9]{1}[0-9]{0,}$", targetQuantity)) {
                currentBlock.setTargetQuantity(Integer.parseInt(targetQuantity));
                targetQuantityField.setError(null);
                isTargetQuantityValid = true;
            } else {
                targetQuantityField.setError(getString(R.string.numeric_value_required));
            }

            if (isBlockNumberValid && isTargetQuantityValid) {
                if (isUpdateView) {
                    AppDataBase.databaseWriteExecutor.execute(() -> blockDao.update(currentBlock));
                } else {
                    AppDataBase.databaseWriteExecutor.execute(() -> blockDao.insert(currentBlock));
                }
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        binding.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(BlockActivity.this, R.style.AlertDialogTextColor);
            builder
                    .setTitle(getString(R.string.delete))
                    .setMessage(getString(R.string.confirm_delete_of_block))
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        AppDataBase.databaseWriteExecutor.execute(() -> {
                            blockDao.delete(currentBlock);
                            itemDao.deleteByBlockId(currentBlock.getId());
                        });
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });

        if (isUpdateView) {
            setTitle(getString(R.string.edit_block));
            int blockId = getIntent().getIntExtra("blockId", 0);

            itemDao = AppDataBase.getDatabase(getApplicationContext()).itemDao();
            currentBlock = blockDao.getById(blockId);

            binding.blockNumberField.setText(Integer.toString(currentBlock.getNumber()));
            binding.targetQuantityField.setText(Integer.toString(currentBlock.getTargetQuantity()));

            binding.saveButton.setText(getString(R.string.update));
        } else {
            binding.deleteButton.setVisibility(View.GONE);
        }
    }
}