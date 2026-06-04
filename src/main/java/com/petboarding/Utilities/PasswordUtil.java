package com.petboarding.Utilities;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class PasswordUtil {

    private static final SecureRandom random = new SecureRandom();
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256;

    /*
        Generate salt, unique salt saved per user
     */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /*
        Hash password using PBKDF2WithHmacSHA256 and generated salt
     */
    public static String hashPassword(String password, String salt) {

        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String hash = hashPassword(password, salt);
        return hash.equals(hashedPassword);
    }

    /*
        ------------------New Password Validation-------------------------------
     */

    public static String getPasswordValidationMessage(String password) {
        String requirementsMsg = "Password Requirements:\n" +
                "- At least 8 characters\n" +
                "- At least one uppercase letter(A-Z)\n" +
                "- At least one lowercase letter(a-z)\n" +
                "- At least one number (0-9)\n" +
                "- At least one special character (!@#$%^&*(),.?\":{}|<>)\n\n";

        if (password.length() < 8) {
            return requirementsMsg += "Password must be at least 8 characters long";
        }
        if (!password.matches(".*[A-Z].*")) {
            return requirementsMsg += "Password must contain at least one uppercase letter";
        }
        if (!password.matches(".*[a-z].*")) {
            return requirementsMsg += "Password must contain at least one lowercase letter";
        }
        if (!password.matches(".*\\d.*")) {
            return requirementsMsg += "Password must contain at least one number";
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return requirementsMsg += "Password must contain at least one special character";
        }

        return null; // valid
    }

}
