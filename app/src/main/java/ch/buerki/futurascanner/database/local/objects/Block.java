package ch.buerki.futurascanner.database.local.objects;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Block {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "number")
    private int number;

    @ColumnInfo(name = "quantity")
    private int targetQuantity;

    @Ignore
    public Block(int id, int number, int targetQuantity) {
        this(number, targetQuantity);
        this.id = id;
    }

    public Block(int number, int targetQuantity) {
        this.number = number;
        this.targetQuantity = targetQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(int targetQuantity) {
        this.targetQuantity = targetQuantity;
    }
}
