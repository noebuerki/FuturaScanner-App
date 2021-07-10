package ch.buerki.futurascanner.ui.overview.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.objects.Block;

public class BlockHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView details;
    private final ImageView editIcon;
    private final View itemView;

    private BlockHolder(@NonNull View itemView, View.OnClickListener itemListener, View.OnClickListener editListener) {
        super(itemView);
        this.itemView = itemView;
        this.itemView.setOnClickListener(itemListener);
        title = itemView.findViewById(R.id.row_block_title);
        details = itemView.findViewById(R.id.row_block_details);
        editIcon = itemView.findViewById(R.id.row_block_edit);
        editIcon.setOnClickListener(editListener);
    }

    public static BlockHolder create(ViewGroup parent, View.OnClickListener itemListener, View.OnClickListener editListener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_row_item, parent, false);
        return new BlockHolder(view, itemListener, editListener);
    }

    public void bind(Block block) {
        itemView.setTag(block.getId());
        title.setText("Block " + block.getNumber());
        details.setText(Integer.toString(block.getTargetQuantity()));
        editIcon.setTag(block.getId());
    }
}
