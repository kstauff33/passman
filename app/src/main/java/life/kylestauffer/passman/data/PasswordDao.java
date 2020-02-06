package life.kylestauffer.passman.data;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PasswordDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	void save(Password password);

	@Query("SELECT * FROM password WHERE id = :id")
	LiveData<Password> getPassword(int id);

	@Query("SELECT * FROM password ORDER BY isFavorite DESC, service COLLATE NOCASE ASC")
	LiveData<List<Password>> getPasswords();

	@Delete
	void delete(Password password);
}
