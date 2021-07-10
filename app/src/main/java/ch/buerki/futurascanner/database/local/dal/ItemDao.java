package ch.buerki.futurascanner.database.local.dal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.buerki.futurascanner.database.local.objects.Item;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM item")
    List<Item> getAll();

    @Query("SELECT * FROM item WHERE blockId = :blockId")
    LiveData<List<Item>> getByBlockIdAsLiveData(int blockId);

    @Query("SELECT * FROM item WHERE blockId = :blockId")
    List<Item> getByBlockId(int blockId);

    @Insert
    void insert(Item item);

    @Update
    void update(Item... item);

    @Query("DELETE FROM item WHERE itemId = :itemId")
    void deleteById(int itemId);

    @Query("DELETE FROM item WHERE blockId = :blockId")
    void deleteByBlockNumber(int blockId);

    @Query("DELETE FROM item")
    void deleteAll();
}
