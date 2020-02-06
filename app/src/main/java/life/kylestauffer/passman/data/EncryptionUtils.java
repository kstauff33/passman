package life.kylestauffer.passman.data;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;

public class EncryptionUtils {

    private static final String TAG = EncryptionUtils.class.getSimpleName();

    //	private static final String PUBLIC_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF3TlVZa0MxMlMvVWg5OHp3UVJhZAprakQxMmZ1NS84M0k5a3JyWlNOSU9BUFNvenFWWVJPdmNnS1U4WURhV1NXN3VSZ1YrVkpZWEV3elg3eTgrR2xPCnVWTCtpYXd2WC9waTlxL3g1V1Z3SUt4VXFNT3U2azBLdVZWK3N1dVBTVVlsVU9qbTdwTFJvcTdUejN0UG90dk8KcjRYZm9JVjRZUUZwazJjeGN5S2V4REpsSit1eHhYQlQzVFY5Y2dGRlBIYkd2U0JtQzg4dzBnUnZFdml3dklrYgowVGFaQXhLVFBlZ0F3dm1QUnlhTDRhVTNQUld4N0RRVENqRnU1QjBOWmVMWHB3SVgrd2JEYjFWenBUTk9Db244CkhTYk5xUXZ5R2pHYnRvOXhSRDB4NVdFbWhrUjQ0M2tpM1d2SG5vRXlmZ0VUdExGcTVUMjlMVVl1dkN4cmxQK28KT3dJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
    private static final String PUBLIC_KEY = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUEwRXYydzhQLzNaZ09vWmZEVmhsVgpWSStFd09tM0M5OFd6dGkzd1pnbmY3aWsxRGp4MVNSQVBYM1NQb001WWZUT1RNQytpdXdXUzZOR2o3MHU1aXd4CmNuY0FDY05hekE5dCtvZ0xleE5XcDdpWTd6QVd3OERVMm1GQm02VjBhMUh1cC9RZzBJdUQ0K0JPaXpJSVA3Z1kKeTJGOFVFYlErTnArMmJhSmZYdjhnTmtYaU1JU0xoMzVxRk03dnFwU1BFRTVncXVhaVRvY21lMFd6c1hDWU1mbgpNUlIydkdpMG96d2ZZNjFlU016SXFyNHVMWlVreFM5V3U5aytsQXJSTG5adTlOM0pETGRONUNHSWduZTZWL0hOCkx4N2c5OEovNDY4RldndWhjQWsvYVdGNmdLRlBkL0ViTDVqTmNzL3pLUTliWWhxZEQwMENweGh4TWtQemlpYUkKUHdJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
//	private static final String PUBLIC_KEY = "LS0tLS1CRUdJTiBSU0EgUFJJVkFURSBLRVktLS0tLQpQcm9jLVR5c"
//			+ "GU6IDQsRU5DUllQVEVECkRFSy1JbmZvOiBBRVMtMjU2LUNCQyxCMUFFNDZEN0E2NjUwOEU5OUM0MTNBQ0Q5QTUwRT"
//			+ "A3QQoKMXUwSVB2TGhwRHJ3YUE5cmVlaHlZQ0tQbHNTSjFJVVg1bTVzcVQyMzJRT1ZaRTgxZXBiUU9JUnVtMDhsZ2Z"
//			+ "BeQozc1FRVW9BRTVLYWpKRWVVeWlUYWF6dmFlczhoYjN3QkpsRktPQ3FaeXdQNUJUL3kwUnpZSUlPMnYzMHdmbGp"
//			+ "SCmlmSXNHQURteUxuK3VyOGhZOWsxcWcwdWN1ZUZTek9KQkMzQjRKNkxyUTJMZjBPZ05rWE5mYzgzZHhvTFBPZFAK"
//			+ "Z2NWbGlSdjFnR3lMM3U4aWlYUTdjNGVsMTJjRGRaRDV2bUZYazBOaE5TK2hPZVUzWVZJeFVoYlVkaWJHSDVmbwpPY"
//			+ "Tg3OGlucGU3cUI2ZWxqVmx4L2E2dktybEh3MWRoZjRZWWJyaUljV2xNTkhaZXBuUDYzRVdjTmRzSWZjalM0ClZNQz"
//			+ "VlWDMwT3RLZkdKOFlKekNHRmRuN1ZtYXZ1Q0wzd1NJWmVoZmI5Vkg0NFZZQ1NBV1h5VzhYSWUzTFdPZEMKV2srV1M"
//			+ "raFFQaHhGbFFQeWxnSmFaVlMwYVZBKzZGM3NwNHMxbmhzVnNITmxiQ0RqMGUzU0N0WmwzT0M3QjhlUQpIZmFZaHhX"
//			+ "blM0bjh3V0wzZjdoYkhhc2xCbC82SU5EZ0h3aGwxWStVY3k3eGk0cWUvRkJtbmVIcTUrUFo0WnhoCjVZL1ByK1A0W"
//			+ "EE0dDQrdm1MN05QV1FyOTdVa0RRcGdvcm5WSmFUblBxRDZQcm9VT0FFMnVPMUhPNjhvbW1EdzUKQ2ZoMzNUTS9zRG"
//			+ "JocVZPYjVoOUs2TGNkS3dmU3ZNeC84N2ZreTRCc0Yra1FaZ2FtOUZyVUkzb0RQWjlLanRUTwp3ZVhUaGQraXZyUlh"
//			+ "POUlxTGJxcHRFMWovYXdidXJWSmp4czBGdENtbXJBTWg2b2ZpQ0c2MHd1OEVOdHByODBaClVGem9ENHNQK1g4bWtp"
//			+ "YVlES21pZWgyeCt4MjRZODhBUE83YmRzTWVzWm1aODc4ZnpRL25Mb0NIMFJ6UXYvbmkKNDhObmxSbmcvaGFJaXBte"
//			+ "EQzeW5DZm9reHdBZ3pYVDVReTlXajdjUEpqL0EzK0F3YmN0NXhIZ1VEWHBhYVAzVApqVFVhQ0dnU1N1WmQxMG1Eb3V"
//			+ "uWEtWbEl3SUVDaUppajVGU2tmUXJPWWhwaEpkdzM4REFYOGZzaGdmMzNBMjJMCldlbWYzM200SWtVbzU0anVmSjBhc"
//			+ "ERLZFJWaDI3dGZsMDRYUnAyMUpiNUZycjVTNmtlTGc2U2puZDJmOWJIbFIKaFAxdXVZQXVuUmtGWmowWHVxUW1KNUp"
//			+ "FWnhzbkJ5MGsxY1oxV3IvMGxmUmNIaUhrMFZ3WWRRR3Q3ZVJCUC9HWgpERWZTa2syRXJzcVdiMk0zd2F3WERkOS9UZD"
//			+ "dzcUR4T2lYblEvT0ZIR2RJY0VVZWV6aDFCZ3VSVXRwU1VDRktpCjY3QnpvUEErOG9ybFJ6aDUzZjFVeHZ6U2tzUS9JU"
//			+ "zA0Z2YwUE9EdmZsSDl5SmwxWFJ0YXA3NktkazFxdk1SL0cKVmQ1TXJFd21YTmFaYTh5blU1KzFwTXJDamJRdG5Nd05"
//			+ "NQXlLZCtYblNvWThkRUR1UVRSUHFkeDJBR2pMSkwvbQpjYjFTUCtTVE5Md1pSeklIdWdEa0FudmpTdzdrTnRnSXppM"
//			+ "lRuVzVRcnNkWlJNTkJxZXgrc3lEZDhSeDVMMzlTClgrT0d5enJKRVhoUXRDOUZYeHpQVHhsQUVvNmdyKzdiVHRxdEg4"
//			+ "NU1RNTVORERkekdzdnVmOXo2OFllUDczU3AKU2EzMnJXMzJ1cEpsa2g0SDVjUkxGSmFpeGQ1UlNZZnMxcXVzN3BpSWky"
//			+ "OVFHY0dUYXAzaU05ODl1NUtmTitkdQpNb002UC9tYjE3NmRJbE00aWRVdktaSE9BNUF0azRySVFWdDBBOHVUY2Joa"
//			+ "G5LS1FDZXFTMFR5MHJsVVZmT3Y3Ckc4a0lxTXZ1aWU4UHVZZGRlNzY0SExqUmJOVEU3Qm5oNG9YcFBPQ0N0a3NNSl"
//			+ "NmWFB3M0JnTUxWRnoxemh1RVAKYm15L3ZxTTNmWlZ5WEpMT2JJZENvM0EwRzI5enN1Qks0UytXNFJOMnhDVG01Wj"
//			+ "VlTFoxTmF4a3VwQmU2VFZKRwotLS0tLUVORCBSU0EgUFJJVkFURSBLRVktLS0tLQo=";


    private static final String PASS_MAN = "PASS_MAN";

    public static EncryptedPayload encryptWithLocalKey(@NonNull String raw) throws EncryptionException {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] iv = cipher.getIV();
            byte[] bytes = cipher.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            String encrypted = Base64.encodeToString(bytes, Base64.DEFAULT);
            return new EncryptedPayload(encrypted, Base64.encodeToString(iv, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(TAG, "Local key encryption failed", e);
            throw new EncryptionException();
        }
    }

    public static String decryptWithLocalKey(@NonNull String raw, @NonNull String iv) throws EncryptionException {
        byte[] plaintext = Base64.decode(raw, Base64.DEFAULT);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new IvParameterSpec(Base64.decode(iv, Base64.DEFAULT)));
            byte[] cipherText = cipher.doFinal(plaintext);
            return new String(cipherText);
        } catch (Exception e) {
            Log.e(TAG, "Local key decryption failed", e);
            throw new EncryptionException();
        }
    }

    public static String encryptWithRemoteKey(@NonNull String raw) throws EncryptionException {
        byte[] plaintext = raw.getBytes();
        try {
            PublicKey key = getKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] cipherText = cipher.doFinal(plaintext);
            byte[] iv = cipher.getIV();
            return new String(cipherText);
        } catch (Exception e) {
            Log.e(TAG, "Remote key encryption failed", e);
            throw new EncryptionException();
        }
    }

    public static PublicKey getKey() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] encodedKey = Base64.decode(PUBLIC_KEY, Base64.DEFAULT);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("X.509", "BC");
        return kf.generatePublic(X509publicKey);
    }

    private static Key getSecretKey() throws EncryptionException {
        try {
            KeyStore androidKeyStore = KeyStore.getInstance("AndroidKeyStore");
            androidKeyStore.load(null);
            Key key = androidKeyStore.getKey(PASS_MAN, null);
            if (key != null) {
                androidKeyStore.setKeyEntry(PASS_MAN, key, null, null);
                return key;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to retrieve from keystore", e);
        }

        final KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(PASS_MAN,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setKeySize(256)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setRandomizedEncryptionRequired(true)
//					.setUserAuthenticationRequired(true)
//					.setUserAuthenticationValidityDurationSeconds(5 * 60)
                    .build();

            keyGenerator.init(keyGenParameterSpec);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get secret key", e);
            throw new EncryptionException("Failed to create key", e);
        }
    }

    public static class EncryptionException extends Exception {
        public EncryptionException() {
            super();
        }

        public EncryptionException(String message) {
            super(message);
        }

        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }

    }

    public static class EncryptedPayload {
        private final String encrypted;

        private final String iv;

        public EncryptedPayload(String encrypted, String iv) {
            this.encrypted = encrypted;
            this.iv = iv;
        }

        public String getIv() {
            return iv;
        }

        public String getEncrypted() {
            return encrypted;
        }

    }


}
