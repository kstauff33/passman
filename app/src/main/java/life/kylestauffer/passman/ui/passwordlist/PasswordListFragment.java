package life.kylestauffer.passman.ui.passwordlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import life.kylestauffer.passman.R;
import life.kylestauffer.passman.databinding.PasswordListFragmentBinding;
import life.kylestauffer.passman.ui.ViewModelFactory;

public class PasswordListFragment extends Fragment {
    private static final String TAG = PasswordListFragment.class.getSimpleName();

    private PasswordListAdapter passwordListAdapter;

    private PasswordListViewModel viewModel;

    private InputMethodManager inputmanager;

    public static PasswordListFragment newInstance() {
        return new PasswordListFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getContext() == null || getActivity() == null) return null;
        inputmanager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        viewModel = ((ViewModelFactory.Provider) getActivity()).getFactory()
                .create(PasswordListViewModel.class);

        View view = inflater.inflate(R.layout.password_list_fragment, container, false);
        PasswordListFragmentBinding binding = PasswordListFragmentBinding.bind(view);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null || getContext() == null) return;

        BottomAppBar bottomAppBar = view.findViewById(R.id.bar);
        CoordinatorLayout coordinatorLayout = view.findViewById(R.id.coordinator);
        EditText searchInput = view.findViewById(R.id.search_input);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        RecyclerView recyclerView = view.findViewById(R.id.content_main);

        viewModel.getPasswords().observe(getActivity(), passwords -> {
            passwordListAdapter.setPasswords(passwords);
        });

        viewModel.snackbarText.observe(getActivity(), text -> {
            Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT)
                    .setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bottomAppBar)
                    .show();
        });

        viewModel.searchText.observe(getActivity(), searchText -> {
            passwordListAdapter.filter(searchText);
        });

        viewModel.searchOpen.observe(getViewLifecycleOwner(), isOpen -> {
            if (isOpen && searchInput.requestFocus()) {
                inputmanager.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            } else {
                inputmanager.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                searchInput.clearFocus();
            }
        });

        fab.setOnClickListener(v -> openAddEditPassword());

        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search_button) {
                viewModel.searchOpen.setValue(!viewModel.searchOpen.getValue());
                return true;
            }
            return false;
        });

        // setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        SpaceItemDecoration decoration = new SpaceItemDecoration(32);
        recyclerView.addItemDecoration(decoration);
        passwordListAdapter = new PasswordListAdapter(viewModel);
        recyclerView.setAdapter(passwordListAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recycler, int dx, int dy) {
                if (dy > 20)
                    fab.hide();
                else if (dy < -5)
                    fab.show();
            }
        });
    }

    public void openAddEditPassword() {
        if (getActivity() == null) return;

        PasswordListFragmentDirections.AddEditPassword action =
                PasswordListFragmentDirections.addEditPassword();
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(action);
    }
}
