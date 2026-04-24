package com.example.musicbuddy.ui.components

/**
 * Validators - Oggetto con funzioni di validazione per i campi del form
 */
object Validators {

    /**
     * Valida un indirizzo email
     * Controlla: presenza di @, presenza di ., lunghezza minima
     */
    fun isValidEmail(email: String): Boolean {
        return email.contains("@") &&
                email.contains(".") &&
                email.length > 5 &&
                email.indexOf("@") < email.lastIndexOf(".")
    }

    /**
     * Valida un numero di telefono
     * Controlla: lunghezza minima 10 cifre, solo numeri
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.length >= 10 &&
                phone.all { it.isDigit() }
    }

    /**
     * Valida una password
     * Controlla: lunghezza minima 6 caratteri
     *
     * Puoi aggiungere requisiti più forti:
     * - Almeno una maiuscola
     * - Almeno un numero
     * - Almeno un carattere speciale
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida un nome o cognome
     * Controlla: lunghezza minima 2 caratteri, solo lettere e spazi
     */
    fun isValidName(name: String): Boolean {
        return name.length >= 2 &&
                name.all { it.isLetter() || it.isWhitespace() }
    }

    /**
     * Validazione password forte (opzionale)
     * Requisiti:
     * - Minimo 8 caratteri
     * - Almeno una maiuscola
     * - Almeno un numero
     * - Almeno un carattere speciale
     */
    fun isValidPasswordStrong(password: String): Boolean {
        val hasMinLength = password.length >= 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return hasMinLength && hasUpperCase && hasNumber && hasSpecialChar
    }

    /**
     * verica se la pssword di conferma matcha con la password
     */
    fun verifyConfirmPassword(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}