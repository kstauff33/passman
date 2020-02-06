package life.kylestauffer.passman;

import android.app.Application;

import androidx.room.Room;

import life.kylestauffer.passman.data.PasswordDao;
import life.kylestauffer.passman.data.PasswordDatabase;
import life.kylestauffer.passman.data.PasswordRepository;

public class ServiceLocator {

    private final Application application;

    private PasswordDao passwordDao;

    private PasswordRepository passwordRepository;

    public ServiceLocator(Application application) {
        this.application = application;
    }

    public PasswordDao getPasswordDao() {
        if (passwordDao == null) {
            synchronized (this) {
                if (passwordDao == null) {
                    passwordDao = Room.databaseBuilder(application, PasswordDatabase.class, "passwords")
                            .fallbackToDestructiveMigration()
                            .build()
                            .passwordDao();
                }
            }
        }
        return passwordDao;
    }

    public PasswordRepository getPasswordRepository() {
        if (passwordRepository == null) {
            synchronized (this) {
                if (passwordRepository == null) {
                    passwordRepository = new PasswordRepository(getPasswordDao());
                }
            }
        }
        return passwordRepository;
    }


    public interface ServiceLocaterInitialized {
        void initFromServiceLoader(ServiceLocator loader);
    }
}
