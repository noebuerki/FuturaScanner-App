package ch.buerki.futurascanner.ui.overview.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import ch.buerki.futurascanner.R;
import ch.buerki.futurascanner.database.local.dal.ItemDao;
import ch.buerki.futurascanner.database.local.objects.Block;

public class BlockHolder extends RecyclerView.ViewHolder {

    private final View itemView;
    private final TextView title;
    private final TextView details;
    private final ViewGroup parent;
    private final ImageView editIcon;

    private BlockHolder(@NonNull View itemView, ViewGroup parent, View.OnClickListener itemListener, View.OnClickListener editListener) {
        super(itemView);
        this.parent = parent;
        this.itemView = itemView;
        this.itemView.setOnClickListener(itemListener);
        title = itemView.findViewById(R.id.row_block_title);
        details = itemView.findViewById(R.id.row_block_details);
        editIcon = itemView.findViewById(R.id.row_block_edit);
        editIcon.setOnClickListener(editListener);
    }

    public static BlockHolder create(ViewGroup parent, View.OnClickListener itemListener, View.OnClickListener editListener) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.block_row_item, parent, false);
        return new BlockHolder(view, parent, itemListener, editListener);
    }

    public void bind(Block block, ItemDao itemDao) {
        int itemCount = itemDao.getByBlockId(block.getId()).size();
        itemView.setTag(block.getId());
        title.setText("Block " + block.getNumber());
        details.setText(itemCount + "/" + block.getTargetQuantity());
        if (itemCount != block.getTargetQuantity()){
            details.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.red));
        }
        editIcon.setTag(block.getId());
    }
}
