package life.kylestauffer.passman.ui.passwordlist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import life.kylestauffer.passman.R;
import life.kylestauffer.passman.ThreadUtils;
import life.kylestauffer.passman.data.Password;

public class PasswordListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = PasswordListAdapter.class.getSimpleName();

    private static final int PASSWORD_ROW = 0;

    private static final int SPACER = 1;

    private static final int TITLE = 2;

    private final PasswordListViewModel viewModel;

    private String filter;

    private List<Password> passwords = new ArrayList<>();

    public PasswordListAdapter(PasswordListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case PASSWORD_ROW:
                return new PasswordListViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false), viewModel);
            case SPACER:
                return new SpacerRow(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.spacer_row, parent, false));
            case TITLE:
                return new TitleRow(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.title_row, parent, false));
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == PASSWORD_ROW) {
            ((PasswordListViewHolder) holder).setModel(getPasswords().get(position - 1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TITLE;
        } else if (position <= getPasswords().size()) {
            return PASSWORD_ROW;
        } else {
            return SPACER;
        }
    }

    @Override
    public int getItemCount() {
        return getPasswords().size() + 2;
    }

    private List<Password> getPasswords() {
        if (filter == null || filter.length() == 0) {
            return passwords;
        }
        Log.i(TAG, "Filter: " + filter);
        List<Password> filtered = new ArrayList<>();
        for (Password password : passwords) {
            if (password.getService().toLowerCase().contains(filter.toLowerCase())) {
                filtered.add(password);
            }
        }
        Log.i(TAG, "Filtered: " + filtered);
        return filtered;
    }

    public void setPasswords(List<Password> passwords) {
        this.passwords.clear();
        this.passwords.addAll(passwords);

        ThreadUtils.postOnUiThread(() -> {
            Log.d(TAG, "notify dataset changed");
            notifyDataSetChanged();
        });
    }

    public void filter(String input) {
        filter = input;
        notifyDataSetChanged();
    }
}
