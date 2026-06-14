package com.example.storage

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import kotlin.io.encoding.Base64

class CryptoManager {
    private val keyStore = KeyStore.getInstance(KEYSTORE_TYPE).apply {
        load(null)
    }

    fun encryptBase64(data: String): String {
        return Base64.encode(encrypt(data.toByteArray(Charsets.UTF_8)))
    }

    fun decryptBase64(data: String): String {
        return decrypt(Base64.decode(data)).toString(Charsets.UTF_8)
    }

    private fun encrypt(data: ByteArray): ByteArray {
        val encryptCypherInstance = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
        }
        val encryptData = encryptCypherInstance.doFinal(data)
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).apply {
            writeInt(encryptCypherInstance.iv.size)
            write(encryptCypherInstance.iv)
            writeInt(encryptData.size)
            write(encryptData)
        }

        return byteStream.toByteArray()
    }

    private fun decrypt(data: ByteArray): ByteArray {
        DataInputStream(ByteArrayInputStream(data)).use {
            val ivSize = it.readInt()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedDataSize = it.readInt()
            val encryptedData = ByteArray(encryptedDataSize)
            it.read(encryptedData)

            val cipher = Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
            }

            return cipher.doFinal(encryptedData)
        }
    }

    private fun getKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey ?: createAndSaveKey()
    }

    private fun createAndSaveKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM, KEYSTORE_TYPE).apply {
            init(
                KeyGenParameterSpec
                    .Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setKeySize(KEY_LENGTH)
                    .setUserAuthenticationRequired(false)
                    .build()
            )
        }.generateKey()
    }

    private companion object {
        const val KEYSTORE_TYPE = "AndroidKeyStore"
        const val KEY_ALIAS = "SharedMediaPlayer"
        const val KEY_LENGTH = 256

        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }
}
