package ch.buerki.futurascanner.ui.scan.helpers;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import ch.buerki.futurascanner.database.local.objects.Item;

public class ItemAdapter extends ListAdapter<Item, ItemHolder> {

    private final View.OnClickListener deleteListener;

    public ItemAdapter(@NonNull DiffUtil.ItemCallback<Item> diffCallback, View.OnClickListener deleteListener) {
        super(diffCallback);
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ItemHolder.create(parent, deleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        Item current = getItem(position);
        holder.bind(current);
    }

    public static class LocationDiff extends DiffUtil.ItemCallback<Item> {

        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {

            boolean idTheSame = oldItem.getId() == newItem.getId();
            boolean barcodeTheSame = oldItem.getBarcode().equals(newItem.getBarcode());
            boolean blockTheSame = oldItem.getBlockId() == newItem.getBlockId();
            boolean positionTheSame = oldItem.getPosition() == newItem.getPosition();

            return idTheSame && barcodeTheSame && blockTheSame && positionTheSame;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {

            boolean idTheSame = oldItem.getId() == newItem.getId();
            boolean barcodeTheSame = oldItem.getBarcode().equals(newItem.getBarcode());
            boolean blockTheSame = oldItem.getBlockId() == newItem.getBlockId();
            boolean positionTheSame = oldItem.getPosition() == newItem.getPosition();

            return idTheSame && barcodeTheSame && blockTheSame && positionTheSame;
        }
    }
}
