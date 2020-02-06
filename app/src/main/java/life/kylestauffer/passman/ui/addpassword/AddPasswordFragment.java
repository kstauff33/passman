package life.kylestauffer.passman.ui.addpassword;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import life.kylestauffer.passman.R;
import life.kylestauffer.passman.databinding.AddPasswordFragmentBinding;
import life.kylestauffer.passman.ui.ViewModelFactory;

public class AddPasswordFragment extends Fragment {
    private static final String TAG = AddPasswordFragment.class.getSimpleName();

    private AddPasswordViewModel viewModel;

    public static AddPasswordFragment newInstance() {
        return new AddPasswordFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        if (getActivity() == null) return null;

        viewModel = ((ViewModelFactory.Provider) getActivity()).getFactory().create(AddPasswordViewModel.class);
        View inflate = inflater.inflate(R.layout.add_password_fragment, container, false);
        AddPasswordFragmentBinding binding = AddPasswordFragmentBinding.bind(inflate);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) return;

        View coordinator = view.findViewById(R.id.coordinator);
        FloatingActionButton fab = view.findViewById(R.id.fab);

        BottomAppBar bottomAppBar = view.findViewById(R.id.bar);
        bottomAppBar.setNavigationOnClickListener(v -> goBack());

        viewModel.start(AddPasswordFragmentArgs.fromBundle(getArguments()).getPasswordId());
        viewModel.events.observe(getActivity(), event -> {
            switch (event) {
                case FAILED:
                    viewModel.snackbarText.postValue("Couldn't save password");
                    break;
                case ADDED:
                case EDITED:
                    goBack();
                    break;
            }
        });
        viewModel.snackbarText.observe(getActivity(), text -> {
            Snackbar.make(coordinator, text, Snackbar.LENGTH_SHORT)
                    .setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bottomAppBar)
                    .show();
        });
    }

    private void goBack() {
        if (getActivity() != null) {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
        }
    }
}
