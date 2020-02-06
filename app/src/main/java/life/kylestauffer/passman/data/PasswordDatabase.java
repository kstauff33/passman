package life.kylestauffer.passman.data;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Password.class}, version = 4, exportSchema = false)
public abstract class PasswordDatabase extends RoomDatabase {

    public abstract PasswordDao passwordDao();

}
