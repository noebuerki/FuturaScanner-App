package ch.noebuerki.futurascanner.database.local.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ch.noebuerki.futurascanner.database.local.objects.Settings;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM settings")
    List<Settings> getAll();

    @Insert
    void insert(Settings settings);

    @Update
    void update(Settings settings);
}
