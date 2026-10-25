import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PasswordCheckerTest {

    PasswordChecker checker;

    @BeforeEach
    void setUp() {
        checker = new PasswordChecker();
        checker.setMinLength(8);
        checker.setMaxLength(12);
    }

    // ---------- Setters de longitud ----------

    @Test // L1
    void setMinLength_negativo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> checker.setMinLength(-1));
        assertEquals(PasswordChecker.ERR_INVALID_MIN_LENGTH, ex.getMessage());
    }

    @Test // L2 (frontera: el JavaDoc dice "inferior a cero", luego 0 es válido)
    void setMinLength_cero_esValido() {
        assertDoesNotThrow(() -> checker.setMinLength(0));
        assertEquals(0, checker.getMinLength());
    }

    @Test // L3
    void setMinLength_uno_seGuarda() {
        checker.setMinLength(1);
        assertEquals(1, checker.getMinLength());
    }

    @Test // L4
    void setMaxLength_menorQueMin_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> checker.setMaxLength(7));
        assertEquals(PasswordChecker.ERR_INVALID_MAX_LENGTH, ex.getMessage());
    }

    @Test // L5
    void setMaxLength_igualQueMin_esValido() {
        assertDoesNotThrow(() -> checker.setMaxLength(8));
        assertEquals(8, checker.getMaxLength());
    }

    @Test // L6
    void setMaxLength_mayorQueMin_seGuarda() {
        checker.setMaxLength(9);
        assertEquals(9, checker.getMaxLength());
    }

    // ---------- Comprobaciones booleanas ----------

    @Test // B1
    void checksPorDefecto_estanDesactivados() {
        PasswordChecker nuevo = new PasswordChecker();
        assertFalse(nuevo.getCheckDigits());
        assertFalse(nuevo.getCheckUppers());
        assertFalse(nuevo.getCheckLowers());
    }

    @Test // B2
    void setChecks_true_seGuardan() {
        checker.setCheckDigits(true);
        checker.setCheckUppers(true);
        checker.setCheckLowers(true);
        assertTrue(checker.getCheckDigits());
        assertTrue(checker.getCheckUppers());
        assertTrue(checker.getCheckLowers());
    }

    @Test // B3
    void setChecks_false_seGuardan() {
        checker.setCheckDigits(true);
        checker.setCheckDigits(false);
        assertFalse(checker.getCheckDigits());
    }

    // ---------- check(): resultado correcto ----------

    @Test // C1
    void check_passwordCorrecto_devuelveOk() {
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcde123", "abcde123"));
    }

    // ---------- check(): en blanco ----------

    @Test // C2a
    void check_vacio_devuelveErrBlank() {
        assertEquals(PasswordChecker.ERR_BLANK, checker.check("", ""));
    }

    @Test // C2b
    void check_soloEspacios_devuelveErrBlank() {
        assertEquals(PasswordChecker.ERR_BLANK, checker.check("   ", "   "));
    }

    // ---------- check(): longitud (min=8, max=12) ----------

    @Test // C3
    void check_longitud7_devuelveErrLength() {
        assertEquals(PasswordChecker.ERR_LENGTH, checker.check("abcde12", "abcde12"));
    }

    @Test // C4
    void check_longitud8_devuelveOk() {
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcde123", "abcde123"));
    }

    @Test // C4b
    void check_longitud9_devuelveOk() {
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcde1234", "abcde1234"));
    }

    @Test // C5
    void check_longitud12_devuelveOk() {
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcdefgh1234", "abcdefgh1234"));
    }

    @Test // C6
    void check_longitud13_devuelveErrLength() {
        assertEquals(PasswordChecker.ERR_LENGTH, checker.check("abcdefgh12345", "abcdefgh12345"));
    }

    // ---------- check(): dígitos ----------

    @Test // C7
    void check_sinDigitos_comprobacionActiva_devuelveErrDigits() {
        checker.setCheckDigits(true);
        assertEquals(PasswordChecker.ERR_DIGITS, checker.check("abcdefgh", "abcdefgh"));
    }

    @Test // C8
    void check_sinDigitos_comprobacionInactiva_devuelveOk() {
        checker.setCheckDigits(false);
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcdefgh", "abcdefgh"));
    }

    // ---------- check(): mayúsculas ----------

    @Test // C9a
    void check_sinMayusculas_comprobacionActiva_devuelveErrUppers() {
        checker.setCheckUppers(true);
        assertEquals(PasswordChecker.ERR_UPPERS, checker.check("abcde123", "abcde123"));
    }

    @Test // C9b
    void check_sinMayusculas_comprobacionInactiva_devuelveOk() {
        checker.setCheckUppers(false);
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("abcde123", "abcde123"));
    }

    @Test // C9c
    void check_conMayusculas_comprobacionActiva_devuelveOk() {
        checker.setCheckUppers(true);
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("Abcde123", "Abcde123"));
    }

    // ---------- check(): minúsculas ----------

    @Test // C10a
    void check_sinMinusculas_comprobacionActiva_devuelveErrLowers() {
        checker.setCheckLowers(true);
        assertEquals(PasswordChecker.ERR_LOWERS, checker.check("ABCDE123", "ABCDE123"));
    }

    @Test // C10b
    void check_sinMinusculas_comprobacionInactiva_devuelveOk() {
        checker.setCheckLowers(false);
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("ABCDE123", "ABCDE123"));
    }

    @Test // C10c
    void check_conMinusculas_comprobacionActiva_devuelveOk() {
        checker.setCheckLowers(true);
        assertEquals(PasswordChecker.PASSWORD_OK, checker.check("aBCDE123", "aBCDE123"));
    }

    // ---------- check(): repetición ----------

    @Test // C11
    void check_passwordsDistintas_devuelveErrNoMatch() {
        assertEquals(PasswordChecker.ERR_NO_MATCH, checker.check("abcde123", "abcde124"));
    }

    // ---------- Valores por defecto (no documentados) ----------

    @Test // D1: solo para anotar los valores en el PDF
    void longitudesPorDefecto_informativo() {
        PasswordChecker nuevo = new PasswordChecker();
        System.out.println("Min por defecto: " + nuevo.getMinLength());
        System.out.println("Max por defecto: " + nuevo.getMaxLength());
    }
}