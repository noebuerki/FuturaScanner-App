package ch.noebuerki.futurascanner.database.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.noebuerki.futurascanner.database.local.dal.BlockDao;
import ch.noebuerki.futurascanner.database.local.dal.ItemDao;
import ch.noebuerki.futurascanner.database.local.dal.SettingsDao;
import ch.noebuerki.futurascanner.database.local.objects.Block;
import ch.noebuerki.futurascanner.database.local.objects.Item;
import ch.noebuerki.futurascanner.database.local.objects.Settings;

@Database(
        entities = {
                Item.class,
                Block.class,
                Settings.class
        },
        version = 1
)
public abstract class AppDataBase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile AppDataBase INSTANCE;


    public static AppDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "appdatabase").allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ItemDao itemDao();

    public abstract BlockDao blockDao();

    public abstract SettingsDao settingsDao();
}

