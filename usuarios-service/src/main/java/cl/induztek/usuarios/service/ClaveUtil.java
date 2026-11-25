package cl.induztek.usuarios.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class ClaveUtil {

    public static final int LARGO_MINIMO = 6;
    private static final int ITERACIONES = 65536;
    private static final int BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private ClaveUtil() {
    }

    public static String hashear(String clave) {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return HexFormat.of().formatHex(sal) + ":" + HexFormat.of().formatHex(derivar(clave, sal));
    }

    public static boolean coincide(String clave, String almacenada) {
        if (clave == null || almacenada == null) {
            return false;
        }
        String[] partes = almacenada.split(":");
        if (partes.length != 2) {
            return false;
        }
        try {
            byte[] sal = HexFormat.of().parseHex(partes[0]);
            byte[] esperado = HexFormat.of().parseHex(partes[1]);
            return MessageDigest.isEqual(esperado, derivar(clave, sal));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String clave, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la clave", e);
        }
    }
}
