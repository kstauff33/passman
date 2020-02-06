package life.kylestauffer.passman.ui.passwordlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import life.kylestauffer.passman.data.Password;
import life.kylestauffer.passman.data.PasswordRepository;

public class PasswordListViewModel extends ViewModel {
	private static final String TAG = PasswordListViewModel.class.getSimpleName();

	public final MutableLiveData<String> snackbarText = new MutableLiveData<>();

	public final MutableLiveData<String> searchText = new MutableLiveData<>();

	public final MutableLiveData<Boolean> searchOpen = new MutableLiveData<>(false);

	private final PasswordRepository repository;

	private LiveData<List<Password>> passwords;

	public PasswordListViewModel(PasswordRepository repository) {
		this.repository = repository;
	}

	LiveData<List<Password>> getPasswords() {
		if (passwords == null) {
			passwords = repository.getPasswords();
		}
		return passwords;
	}

	public void clearSearch() {
		searchText.setValue("");
		searchOpen.setValue(false);
	}

	public void savePassword(Password password) {
		repository.savePassword(password);
	}

	public void delete(Password password) {
		repository.deletePassword(password);
	}

	public void editPassword(Password model) {

	}
}
