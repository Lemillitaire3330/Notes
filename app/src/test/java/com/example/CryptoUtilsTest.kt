package com.example

import com.example.crypto.CryptoUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoUtilsTest {

    @Test
    fun `encryption and decryption with correct password succeeds`() {
        val originalText = "Mon texte secret très confidentiel 12345 !?#"
        val password = "mon_super_mot_de_passe"

        val cipherText = CryptoUtils.encrypt(originalText, password)

        // Ensure ciphertext is prefixed, non-empty, and completely different from plaintext
        assertTrue(cipherText.startsWith(CryptoUtils.PREFIX_ENC))
        assertNotEquals(originalText, cipherText)

        // Decryption produces original plaintext
        val decrypted = CryptoUtils.decrypt(cipherText, password)
        assertEquals(originalText, decrypted)
    }

    @Test
    fun `decryption with wrong password fails`() {
        val originalText = "Informations bancaires secrètes"
        val correctPassword = "mot_de_passe_correct"
        val wrongPassword = "mot_de_passe_faux"

        val cipherText = CryptoUtils.encrypt(originalText, correctPassword)

        var failed = false
        try {
            CryptoUtils.decrypt(cipherText, wrongPassword)
        } catch (e: Exception) {
            failed = true
        }
        assertTrue("Decryption with wrong password must throw an exception", failed)
    }

    @Test
    fun `two encryptions of same text produce different ciphertexts due to random salt and IV`() {
        val originalText = "Même texte"
        val password = "mon_mdp"

        val cipher1 = CryptoUtils.encrypt(originalText, password)
        val cipher2 = CryptoUtils.encrypt(originalText, password)

        assertNotEquals(cipher1, cipher2)
        assertEquals(originalText, CryptoUtils.decrypt(cipher1, password))
        assertEquals(originalText, CryptoUtils.decrypt(cipher2, password))
    }

    @Test
    fun `password hashing and verification`() {
        val password = "mon_mot_de_passe"
        val hash = CryptoUtils.hashPassword(password)

        assertTrue(hash.startsWith(CryptoUtils.PREFIX_HASH))
        assertNotEquals(password, hash)

        assertTrue(CryptoUtils.verifyPassword(password, hash))
        assertFalse(CryptoUtils.verifyPassword("mauvais_mot_de_passe", hash))
    }
}
