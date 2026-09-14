package com.example.crypto

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object Base64Helper {
    private const val CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    fun encode(bytes: ByteArray): String {
        val sb = StringBuilder((bytes.size * 4 + 2) / 3)
        var i = 0
        while (i < bytes.size) {
            val b0 = bytes[i].toInt() and 0xFF
            val b1 = if (i + 1 < bytes.size) bytes[i + 1].toInt() and 0xFF else -1
            val b2 = if (i + 2 < bytes.size) bytes[i + 2].toInt() and 0xFF else -1

            sb.append(CHARS[b0 ushr 2])
            if (b1 != -1) {
                sb.append(CHARS[((b0 and 0x03) shl 4) or (b1 ushr 4)])
                if (b2 != -1) {
                    sb.append(CHARS[((b1 and 0x0F) shl 2) or (b2 ushr 6)])
                    sb.append(CHARS[b2 and 0x3F])
                } else {
                    sb.append(CHARS[(b1 and 0x0F) shl 2])
                    sb.append('=')
                }
            } else {
                sb.append(CHARS[(b0 and 0x03) shl 4])
                sb.append("==")
            }
            i += 3
        }
        return sb.toString()
    }

    fun decode(str: String): ByteArray {
        val clean = str.filter { it in CHARS || it == '=' }
        val len = clean.length
        if (len == 0) return ByteArray(0)
        var padding = 0
        if (clean.endsWith("==")) padding = 2
        else if (clean.endsWith("=")) padding = 1

        val outLen = (len * 3 / 4) - padding
        val out = ByteArray(outLen)
        var outIdx = 0
        var i = 0
        while (i < len) {
            val c0 = CHARS.indexOf(clean[i])
            val c1 = CHARS.indexOf(clean[i + 1])
            val c2 = if (clean[i + 2] != '=') CHARS.indexOf(clean[i + 2]) else 0
            val c3 = if (clean[i + 3] != '=') CHARS.indexOf(clean[i + 3]) else 0

            val b0 = (c0 shl 2) or (c1 ushr 4)
            if (outIdx < outLen) out[outIdx++] = b0.toByte()
            if (clean[i + 2] != '=' && outIdx < outLen) {
                val b1 = ((c1 and 0x0F) shl 4) or (c2 ushr 2)
                out[outIdx++] = b1.toByte()
            }
            if (clean[i + 3] != '=' && outIdx < outLen) {
                val b2 = ((c2 and 0x03) shl 6) or c3
                out[outIdx++] = b2.toByte()
            }
            i += 4
        }
        return out
    }
}

/**
 * High-security offline cryptographic helper providing AES-256-GCM encryption
 * and PBKDF2 password derivation with random salt and initialization vectors (IV).
 */
object CryptoUtils {
    const val PREFIX_ENC = "ENC:v1:"
    const val PREFIX_HASH = "HASH:v1:"

    private const val ITERATIONS = 10_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16
    private const val IV_LENGTH_BYTES = 12 // Standard recommended length for AES-GCM
    private const val TAG_LENGTH_BITS = 128 // 128-bit authentication tag

    private val secureRandom = SecureRandom()

    private fun getSecretKeyFactory(): SecretKeyFactory {
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        } catch (e: Exception) {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        }
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKeySpec {
        val factory = getSecretKeyFactory()
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plaintext string into AES-256-GCM ciphertext.
     * Generates a fresh cryptographically secure random salt and IV on each invocation.
     */
    fun encrypt(plainText: String, password: String): String {
        if (plainText.isEmpty()) return ""
        val salt = ByteArray(SALT_LENGTH_BYTES).apply { secureRandom.nextBytes(this) }
        val iv = ByteArray(IV_LENGTH_BYTES).apply { secureRandom.nextBytes(this) }
        val keySpec = deriveKey(password, salt)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, GCMParameterSpec(TAG_LENGTH_BITS, iv))
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // Combined payload: salt (16 bytes) + iv (12 bytes) + cipherBytes with auth tag
        val combined = ByteArray(salt.size + iv.size + cipherBytes.size)
        System.arraycopy(salt, 0, combined, 0, salt.size)
        System.arraycopy(iv, 0, combined, salt.size, iv.size)
        System.arraycopy(cipherBytes, 0, combined, salt.size + iv.size, cipherBytes.size)

        return PREFIX_ENC + Base64Helper.encode(combined)
    }

    /**
     * Decrypts AES-256-GCM ciphertext using the provided password.
     * Throws an exception if the password is wrong or ciphertext is corrupted/tampered with.
     */
    fun decrypt(encryptedText: String, password: String): String {
        if (encryptedText.isEmpty()) return ""
        if (!encryptedText.startsWith(PREFIX_ENC)) {
            // Legacy / unencrypted string fallback
            return encryptedText
        }
        val encodedPayload = encryptedText.removePrefix(PREFIX_ENC)
        val combined = Base64Helper.decode(encodedPayload)
        if (combined.size < SALT_LENGTH_BYTES + IV_LENGTH_BYTES) {
            throw IllegalArgumentException("Invalid encrypted payload length")
        }

        val salt = ByteArray(SALT_LENGTH_BYTES)
        val iv = ByteArray(IV_LENGTH_BYTES)
        val cipherBytes = ByteArray(combined.size - SALT_LENGTH_BYTES - IV_LENGTH_BYTES)

        System.arraycopy(combined, 0, salt, 0, SALT_LENGTH_BYTES)
        System.arraycopy(combined, SALT_LENGTH_BYTES, iv, 0, IV_LENGTH_BYTES)
        System.arraycopy(combined, SALT_LENGTH_BYTES + IV_LENGTH_BYTES, cipherBytes, 0, cipherBytes.size)

        val keySpec = deriveKey(password, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, keySpec, GCMParameterSpec(TAG_LENGTH_BITS, iv))
        val decryptedBytes = cipher.doFinal(cipherBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    /**
     * Hashes a password with PBKDF2 and a random salt for secure storage.
     */
    fun hashPassword(password: String): String {
        val salt = ByteArray(SALT_LENGTH_BYTES).apply { secureRandom.nextBytes(this) }
        val factory = getSecretKeyFactory()
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val hash = factory.generateSecret(spec).encoded

        val combined = ByteArray(salt.size + hash.size)
        System.arraycopy(salt, 0, combined, 0, salt.size)
        System.arraycopy(hash, 0, combined, salt.size, hash.size)
        return PREFIX_HASH + Base64Helper.encode(combined)
    }

    /**
     * Verifies the user entered password against the stored password hash.
     */
    fun verifyPassword(enteredPassword: String, storedPasswordOrHash: String): Boolean {
        if (storedPasswordOrHash.isEmpty()) return enteredPassword.isEmpty()
        if (!storedPasswordOrHash.startsWith(PREFIX_HASH)) {
            // Legacy direct string check
            return enteredPassword == storedPasswordOrHash
        }
        return try {
            val encodedPayload = storedPasswordOrHash.removePrefix(PREFIX_HASH)
            val combined = Base64Helper.decode(encodedPayload)
            if (combined.size < SALT_LENGTH_BYTES) return false

            val salt = ByteArray(SALT_LENGTH_BYTES)
            val expectedHash = ByteArray(combined.size - SALT_LENGTH_BYTES)
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH_BYTES)
            System.arraycopy(combined, SALT_LENGTH_BYTES, expectedHash, 0, expectedHash.size)

            val factory = getSecretKeyFactory()
            val spec = PBEKeySpec(enteredPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
            val actualHash = factory.generateSecret(spec).encoded

            MessageDigest.isEqual(expectedHash, actualHash)
        } catch (e: Exception) {
            false
        }
    }
}
