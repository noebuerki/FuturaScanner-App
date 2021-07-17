package ch.buerki.futurascanner.ui.scan.helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.objects.Item;
import ch.buerki.futurascanner.ui.scan.helpers.scanner.BeepPlayer;

public class FragmentEANDialog extends DialogFragment {

    private final int blockId;
    private final ItemDao itemDao;
    private EditText editText;
    private final BeepPlayer beepPlayer;

    public FragmentEANDialog(int blockId, ItemDao itemDao, BeepPlayer beepPlayer) {
        this.blockId = blockId;
        this.itemDao = itemDao;
        this.beepPlayer = beepPlayer;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("EAN hinzufügen").setMessage("EAN eingeben");

        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.fragment_ean_dialog, null);
        editText = constraintLayout.findViewById(R.id.ean_field_barcode);
        builder.setView(constraintLayout);

        builder.setPositiveButton("Hinzufügen", null);

        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCancel(dialog);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                System.out.println("HEHE");
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ean = editText.getText().toString();
                        if (ean.length() == 12 || ean.length() == 13) {
                            itemDao.insert(new Item(blockId, ean));
                            beepPlayer.playShort();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        return alertDialog;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
