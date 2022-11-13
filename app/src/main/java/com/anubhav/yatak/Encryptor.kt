package com.anubhav.yatak

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import java.io.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Encryptor {
    private lateinit var noteIVfinal : String

    fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    fun encrypt(context: Context, strToEncrypt: String , key:String ): NoteData {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val byteK = key.decodeHex()
        val aesKey: SecretKey = SecretKeySpec(byteK, 0, byteK.size, "AES")

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val cipherText = cipher.doFinal(plainText)
        val noteIV = saveInitializationVector(context, cipher.iv)

        val sb = StringBuilder()
        for (b in cipherText) {
            sb.append(b.toChar())
        }
        val noteData = NoteData(sb.toString(), noteIV)
        return noteData
    }

    fun decrypt(context:Context, dataToDecrypt: ByteArray , key:String, noteIV: String): String {
        val byteK = key.decodeHex()

        val aesKey: SecretKey = SecretKeySpec(byteK, 0, byteK.size, "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        noteIVfinal = noteIV
        val ivSpec = IvParameterSpec(getSavedInitializationVector(context))
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec)
        val cipherText = cipher.doFinal(dataToDecrypt)

        val sb = StringBuilder()
        for (b in cipherText) {
            sb.append(b.toChar())
        }
        return sb.toString()
    }

    fun saveInitializationVector(context: Context, initializationVector: ByteArray) : String {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(initializationVector)
        val strToSave = String(android.util.Base64.encode(baos.toByteArray(), android.util.Base64.DEFAULT))
        return strToSave
    }

    fun getSavedInitializationVector(context: Context) : ByteArray {
        val bytes = android.util.Base64.decode(noteIVfinal, android.util.Base64.DEFAULT)
        val ois = ObjectInputStream(ByteArrayInputStream(bytes))
        val initializationVector = ois.readObject() as ByteArray
        return initializationVector
    }
}