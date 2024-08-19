package ai.softeer.caecae.global.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {
    /**
     * SHA256 Hash function
     *
     * @param text 평문
     * @return SHA256 Hex String
     */
    public static String SHA256(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());

        return bytesToHex(md.digest());
    }

    /**
     * bytes array를 Hex String으로 변환시켜주는 메소드
     *
     * @param bytes
     * @return Hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
