package cl.induztek.usuarios.service;

public final class RutUtil {

    private RutUtil() {
    }

    private static String limpiar(String rut) {
        return rut == null ? "" : rut.trim().replace(".", "").replace("-", "").toUpperCase();
    }

    public static boolean esValido(String rut) {
        String limpio = limpiar(rut);
        if (limpio.length() < 2) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        int numero;
        try {
            numero = Integer.parseInt(cuerpo);
        } catch (NumberFormatException e) {
            return false;
        }
        return calcularDv(numero) == dv;
    }

    static char calcularDv(int cuerpo) {
        int suma = 0;
        int multiplicador = 2;
        int resto = cuerpo;
        while (resto > 0) {
            suma += (resto % 10) * multiplicador;
            resto /= 10;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int dv = 11 - (suma % 11);
        if (dv == 11) {
            return '0';
        }
        if (dv == 10) {
            return 'K';
        }
        return (char) ('0' + dv);
    }

    public static String formatear(String rut) {
        String limpio = limpiar(rut);
        if (limpio.length() < 2) {
            return rut;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dv = limpio.charAt(limpio.length() - 1);
        StringBuilder sb = new StringBuilder();
        int contador = 0;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            sb.append(cuerpo.charAt(i));
            contador++;
            if (contador % 3 == 0 && i > 0) {
                sb.append('.');
            }
        }
        return sb.reverse() + "-" + dv;
    }
}
