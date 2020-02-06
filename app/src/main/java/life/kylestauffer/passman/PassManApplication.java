package life.kylestauffer.passman;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.stetho.Stetho;

public class PassManApplication extends Application implements Application.ActivityLifecycleCallbacks {
	private static final String TAG = PassManApplication.class.getSimpleName();

	private static ServiceLocator serviceLocator;

	public static ServiceLocator getServiceLocator() {
		return serviceLocator;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		serviceLocator = new ServiceLocator(this);
		registerActivityLifecycleCallbacks(this);
//		Stetho.initializeWithDefaults(this);
	}


	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
		Log.d("PassManApplication", "onActivityCreated");
		if (ServiceLocator.ServiceLocaterInitialized.class.isAssignableFrom(activity.getClass())) {
			Log.d(TAG, "Init with loader");
			((ServiceLocator.ServiceLocaterInitialized) activity).initFromServiceLoader(serviceLocator);
		}
	}

	@Override
	public void onActivityStarted(@NonNull Activity activity) {

	}

	@Override
	public void onActivityResumed(@NonNull Activity activity) {

	}

	@Override
	public void onActivityPaused(@NonNull Activity activity) {

	}

	@Override
	public void onActivityStopped(@NonNull Activity activity) {

	}

	@Override
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity) {

	}
}
