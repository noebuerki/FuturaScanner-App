package ch.noebuerki.futurascanner.ui.scan.helpers;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import ch.noebuerki.futurascanner.R;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.objects.Item;
import ch.noebuerki.futurascanner.ui.scan.helpers.scanner.BeepPlayer;

public class FragmentEANDialog extends DialogFragment {

    private final int blockId;
    private final ItemDao itemDao;
    private final BeepPlayer beepPlayer;
    private EditText editText;

    public FragmentEANDialog(int blockId, ItemDao itemDao, BeepPlayer beepPlayer) {
        this.blockId = blockId;
        this.itemDao = itemDao;
        this.beepPlayer = beepPlayer;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle(getString(R.string.add_ean)).setMessage(getString(R.string.enter_ean));

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.fragment_ean_dialog, null);
        editText = constraintLayout.findViewById(R.id.barcode_field);
        builder.setView(constraintLayout);

        builder.setPositiveButton(getText(R.string.add), null);

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> onCancel(dialog));

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            if ((requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
            }
            ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String ean = editText.getText().toString();
                if (Pattern.matches("^[0-9]{12,13}$", ean)) {
                    itemDao.insert(new Item(blockId, ean));
                    beepPlayer.playShort();
                    dialog.dismiss();
                }
            });
        });
        return alertDialog;
    }
}
