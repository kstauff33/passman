package life.kylestauffer.passman.ui;

import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import life.kylestauffer.passman.BuildConfig;
import life.kylestauffer.passman.R;
import life.kylestauffer.passman.ServiceLocator;

public class MainActivity extends AppCompatActivity implements ServiceLocator.ServiceLocaterInitialized, ViewModelFactory.Provider {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final int OPEN_SETTINGS = 12345;

	private ViewModelFactory viewModelFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (checkForLockScreen()) {
			NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
			navController.navigate(R.id.password_list_fragment);
		}
	}

	private boolean checkForLockScreen() {
		if (true) return true; // TODO Delete this

		KeyguardManager keyguardManager;
		keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
				keyguardManager.isDeviceSecure()) ||
				keyguardManager.isKeyguardSecure()) {
			return true;
		}

		new AlertDialog.Builder(this)
				.setTitle(R.string.lock_title)
				.setMessage(R.string.lock_body)
				.setPositiveButton(R.string.lock_settings, (dialog, which) -> {
					dialog.dismiss();
					startActivityForResult(new Intent(Settings.ACTION_SECURITY_SETTINGS), OPEN_SETTINGS);
				})
				.setNegativeButton(R.string.lock_exit, (dialog, which) -> {
					dialog.dismiss();
					System.exit(0);
				})
				.setCancelable(BuildConfig.DEBUG)
				.show();
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == OPEN_SETTINGS) {
			checkForLockScreen();
		}
	}

	@Override
	public void initFromServiceLoader(ServiceLocator loader) {
		Log.d(TAG, "InitFromServiceLoader");
		viewModelFactory = new ViewModelFactory(loader.getPasswordRepository());
	}

	@NonNull
	@Override
	public ViewModelFactory getFactory() {
		return viewModelFactory;
	}
}
