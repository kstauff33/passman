package life.kylestauffer.passman.data;

import android.text.TextUtils;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Password {
    private static final String TAG = Password.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;

    @Ignore
    private String password;

    private String localEncryptedPassword;

    private String remoteEncryptedPassword;

    private String service;

    private String iv;

    private boolean isFavorite;

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        if (this.iv == null) {
            this.iv = iv;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (password == null && localEncryptedPassword != null) {
            try {
                password = EncryptionUtils.decryptWithLocalKey(localEncryptedPassword, iv);
            } catch (EncryptionUtils.EncryptionException e) {
                Log.e(TAG, "Failed to decrypt password!");
            }
        }
        return password;
    }

    public void setPassword(String password) {
        Log.d(TAG, "Set Password: " + password);
        this.password = password;
        try {
            EncryptionUtils.EncryptedPayload encryptedPayload = EncryptionUtils.encryptWithLocalKey(password);
            this.localEncryptedPassword = encryptedPayload.getEncrypted();
            this.iv = encryptedPayload.getIv();
            this.remoteEncryptedPassword = EncryptionUtils.encryptWithRemoteKey(password);
            Log.d(TAG, "Encrypted password: " + localEncryptedPassword);
        } catch (EncryptionUtils.EncryptionException e) {
            Log.e(TAG, "Failed to encrypt password!");
        }
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getRemoteEncryptedPassword() {
        return remoteEncryptedPassword;
    }

    public void setRemoteEncryptedPassword(String remoteEncryptedPassword) {
        this.remoteEncryptedPassword = remoteEncryptedPassword;
    }

    public String getLocalEncryptedPassword() {
        return localEncryptedPassword;
    }

    public void setLocalEncryptedPassword(String localEncryptedPassword) {
        this.localEncryptedPassword = localEncryptedPassword;
    }

    public boolean isValid() {
        return !(TextUtils.isEmpty(username) || TextUtils.isEmpty(getPassword()) || TextUtils.isEmpty(service));
    }
}
