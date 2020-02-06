package life.kylestauffer.passman.ui.passwordlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;

import life.kylestauffer.passman.R;
import life.kylestauffer.passman.data.Password;

public class PasswordListViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = PasswordListViewHolder.class.getSimpleName();

    private final PasswordListViewModel viewModel;

    private TextView username;

    private TextView password;

    private TextView nickname;

    private ImageView menu;

    private ImageView copy;

    private ImageView favorite;

    private ImageView viewOrLock;

    private boolean passwordVisible = false;

    public PasswordListViewHolder(@NonNull View itemView, PasswordListViewModel viewModel) {
        super(itemView);
        this.viewModel = viewModel;
        username = itemView.findViewById(R.id.username);
        password = itemView.findViewById(R.id.password);
        nickname = itemView.findViewById(R.id.nickname);
        menu = itemView.findViewById(R.id.options);
        copy = itemView.findViewById(R.id.copy);
        favorite = itemView.findViewById(R.id.favorite);
        viewOrLock = itemView.findViewById(R.id.password_icon);
    }

    public void setModel(final Password model) {
        char[] star = new char[model.getPassword().length()];
        Arrays.fill(star, '*');
        String asterisks = new String(star);

        passwordVisible = false;
        username.setText(model.getUsername());
        password.setText(asterisks);
        nickname.setText(model.getService());
        if (model.isFavorite()) {
            favorite.setImageDrawable(favorite.getContext()
                    .getDrawable(R.drawable.ic_favorite_black_24dp));
        } else {
            favorite.setImageDrawable(favorite.getContext()
                    .getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }

        viewOrLock.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                viewOrLock.setImageDrawable(viewOrLock.getContext().getResources().getDrawable(R.drawable.ic_lock_black_24dp));
                password.setText(model.getPassword());
            } else {
                viewOrLock.setImageDrawable(viewOrLock.getContext().getResources().getDrawable(R.drawable.ic_remove_red_eye_black_24dp));
                password.setText(asterisks);
            }
        });

        // favorite button
        favorite.setOnClickListener(v -> {
            model.setFavorite(!model.isFavorite());
            viewModel.savePassword(model);
            viewModel.snackbarText.setValue(model.getService() + (model.isFavorite() ? " liked" : " unliked"));
        });

        // menu button
        menu.setOnClickListener(v -> showPopUp(model));

        // copy button
        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) itemView.getContext()
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(model.getService(), model.getPassword());
            clipboard.setPrimaryClip(clip);
            Log.d(TAG, "Password copied");
            viewModel.snackbarText.setValue(model.getService() + " password copied");
        });
    }

    private void showPopUp(final Password model) {
        if (menu.getContext() == null) return;
        PopupMenu popup = new PopupMenu(menu.getContext(), menu);
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_password:
                    viewModel.delete(model);
                    viewModel.snackbarText.setValue(model.getService() + " deleted");
                    break;

                case R.id.edit_password:
                    viewModel.editPassword(model);
            }
            return true;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.password_actions, popup.getMenu());
        popup.show();
    }

}
