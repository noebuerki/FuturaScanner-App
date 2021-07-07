package ch.buerki.futurascanner.ui.scan.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.objects.Item;

public class ItemHolder extends RecyclerView.ViewHolder {

    private final TextView barcode;
    private final TextView position;
    private final ImageView deleteIcon;

    private ItemHolder(@NonNull View itemView, View.OnClickListener deleteListener) {
        super(itemView);
        barcode = itemView.findViewById(R.id.item_barcode);
        position = itemView.findViewById(R.id.item_position);
        deleteIcon = itemView.findViewById(R.id.item_delete);
        deleteIcon.setOnClickListener(deleteListener);
    }

    public static ItemHolder create(ViewGroup parent, View.OnClickListener deleteListener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_item, parent, false);
        return new ItemHolder(view, deleteListener);
    }

    public void bind(Item item) {
        barcode.setText(item.getBarcode());
        position.setText(Integer.toString(item.getPosition()));
        deleteIcon.setTag(item.getId());
    }
}
