package ch.buerki.futurascanner.database.local.objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Item {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    private int id;

    @ColumnInfo(name = "blockId")
    private int blockId;

    @ColumnInfo(name = "position")
    private int position;

    @NonNull
    @ColumnInfo(name = "barcode")
    private String barcode;

    @Ignore
    public Item(int id, int blockId, int position, @NonNull String barcode) {
        this(blockId, barcode);
        this.id = id;
        this.position = position;
    }

    public Item(int blockId, @NonNull String barcode) {
        this.blockId = blockId;
        this.barcode = barcode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(@NonNull String barcode) {
        this.barcode = barcode;
    }
}
