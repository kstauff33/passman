package life.kylestauffer.passman;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

public class ThreadUtils {

    public static void runOffUiThread(@NonNull Runnable runnable) {
        HandlerThread handlerThread = new HandlerThread("handler thread");
        handlerThread.start();
        new Handler(handlerThread.getLooper()).post(runnable);
    }

    public static void postOnUiThread(@NonNull Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
