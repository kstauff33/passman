package life.kylestauffer.passman.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import life.kylestauffer.passman.data.PasswordRepository;
import life.kylestauffer.passman.ui.addpassword.AddPasswordViewModel;
import life.kylestauffer.passman.ui.passwordlist.PasswordListViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private static final String TAG = ViewModelFactory.class.getSimpleName();

    private final PasswordRepository passwordRepository;

    public ViewModelFactory(PasswordRepository passwordRepository) {
        this.passwordRepository = passwordRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Log.d(TAG, "Create: " + modelClass.getSimpleName());
        if (modelClass.isAssignableFrom(AddPasswordViewModel.class)) {
            return (T) new AddPasswordViewModel(passwordRepository);
        }
        if (modelClass.isAssignableFrom(PasswordListViewModel.class)) {
            return (T) new PasswordListViewModel(passwordRepository);
        }
        throw new IllegalStateException("Could not created viewmodel");
    }

    public interface Provider {
        @NonNull
        ViewModelFactory getFactory();
    }
}
