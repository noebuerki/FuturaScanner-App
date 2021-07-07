package ch.buerki.futurascanner.ui.overview.helpers;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import ch.buerki.futurascanner.database.local.objects.Block;

public class BlockAdapter extends ListAdapter<Block, BlockHolder> {

    private final View.OnClickListener itemListener;
    private final View.OnClickListener editListener;

    public BlockAdapter(@NonNull DiffUtil.ItemCallback<Block> diffCallback, View.OnClickListener itemListener, View.OnClickListener editListener) {
        super(diffCallback);
        this.itemListener = itemListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public BlockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BlockHolder.create(parent, itemListener, editListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockHolder holder, int position) {
        Block current = getItem(position);
        holder.bind(current);
    }

    public static class LocationDiff extends DiffUtil.ItemCallback<Block> {

        @Override
        public boolean areItemsTheSame(@NonNull Block oldItem, @NonNull Block newItem) {

            boolean idTheSame = oldItem.getId() == newItem.getId();
            boolean numberTheSame = oldItem.getNumber() == newItem.getNumber();
            boolean quantityTheSame = oldItem.getTargetQuantity() == newItem.getTargetQuantity();

            return idTheSame && numberTheSame && quantityTheSame;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Block oldItem, @NonNull Block newItem) {

            boolean idTheSame = oldItem.getId() == newItem.getId();
            boolean numberTheSame = oldItem.getNumber() == newItem.getNumber();
            boolean quantityTheSame = oldItem.getTargetQuantity() == newItem.getTargetQuantity();

            return idTheSame && numberTheSame && quantityTheSame;
        }
    }
}
