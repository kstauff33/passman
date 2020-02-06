package life.kylestauffer.passman.data;

import androidx.lifecycle.LiveData;

import java.util.List;

import life.kylestauffer.passman.ThreadUtils;

public class PasswordRepository {

	private final PasswordDao passwordDao;

	public PasswordRepository(PasswordDao passwordDao) {
		this.passwordDao = passwordDao;
	}

	public LiveData<List<Password>> getPasswords() {
		return passwordDao.getPasswords();
	}

	public LiveData<Password> getPasswordSync(int id) {
		return passwordDao.getPassword(id);
	}

	public void savePassword(final Password password) {
		ThreadUtils.runOffUiThread(() -> passwordDao.save(password));
	}

	public void savePasswordSync(final Password password) {
		passwordDao.save(password);
	}

	public void deletePassword(final Password password) {
		ThreadUtils.runOffUiThread(() -> passwordDao.delete(password));
	}

}
