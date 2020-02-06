package life.kylestauffer.passman.ui.addpassword;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import life.kylestauffer.passman.ThreadUtils;
import life.kylestauffer.passman.data.Password;
import life.kylestauffer.passman.data.PasswordRepository;

public class AddPasswordViewModel extends ViewModel {
    private static final String TAG = AddPasswordViewModel.class.getSimpleName();

    public final MutableLiveData<String> snackbarText = new MutableLiveData<>();

    public final MutableLiveData<Boolean> dataLoading = new MutableLiveData<>(false);

    public final MutableLiveData<Boolean> isNewPassword = new MutableLiveData<>(true);

    public final MutableLiveData<Event> events = new MutableLiveData<>();

    public final PasswordFormData passwordData = new PasswordFormData();

    private final PasswordRepository repository;

    public AddPasswordViewModel(PasswordRepository repository) {
        this.repository = repository;
    }

    public void start(Integer passwordId) {
        Log.d(TAG, "Start viewmodel");
        if (passwordId == null || passwordId == -1) {
            isNewPassword.setValue(true);
            return;
        }

        isNewPassword.setValue(false);

        if (dataLoading.getValue() != null && dataLoading.getValue()) {
            return;
        }

        dataLoading.setValue(true);
        Log.d(TAG, "Getting from db");
        ThreadUtils.runOffUiThread(() -> {
            LiveData<Password> pw = repository.getPasswordSync(passwordId);
            ThreadUtils.postOnUiThread(() -> {
                pw.observeForever(password -> {
                    dataLoading.postValue(false);
                    passwordData.initFromPassword(password);
                });
            });
        });
    }

    public void save() {
        Log.d(TAG, "Save password data");
        if (!passwordData.isValid()) {
            events.postValue(Event.FAILED);
            return;
        }

        ThreadUtils.runOffUiThread(() -> {
            repository.savePasswordSync(passwordData.toPassword());
            events.postValue(isNewPassword.getValue() != null && isNewPassword.getValue() ?
                    Event.ADDED : Event.EDITED);
        });
    }

    public enum Event {
        ADDED, EDITED, FAILED
    }

    public static class PasswordFormData {
        public MutableLiveData<String> usernameInput = new MutableLiveData<>();

        public MutableLiveData<String> passwordInput = new MutableLiveData<>();

        public MutableLiveData<String> serviceInput = new MutableLiveData<>();

        public Password toPassword() {
            Password password = new Password();
            password.setPassword(passwordInput.getValue());
            password.setUsername(usernameInput.getValue());
            password.setService(serviceInput.getValue());
            return password;
        }

        public void initFromPassword(Password password) {
            if (password != null) {
                usernameInput.postValue(password.getUsername());
                passwordInput.postValue(password.getPassword());
                serviceInput.postValue(password.getService());
            }
        }

        public boolean isValid() {
            return toPassword().isValid();
        }
    }

}
