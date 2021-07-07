package ch.buerki.futurascanner.database.local.dal;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.buerki.futurascanner.database.local.objects.Block;

@Dao
public interface BlockDao {

    @Query("SELECT * FROM block ORDER BY number")
    List<Block> getAll();

    @Query("SELECT * FROM block ORDER BY number")
    LiveData<List<Block>> getAllAsLiveData();

    @Query("SELECT * FROM block WHERE id = :id")
    Block getById(int id);

    @Query("SELECT * FROM Block WHERE number = :number")
    Block getByNumber(int number);

    @Query("SELECT id FROM block")
    List<Integer> getIds();

    @Insert
    void insert(Block block);

    @Update
    void update(Block block);

    @Delete
    void delete(Block block);

    @Query("DELETE FROM block")
    void deleteAll();
}
