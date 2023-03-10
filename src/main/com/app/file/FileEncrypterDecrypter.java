package com.app.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utils.EncPathException;
import com.utils.ErrorCode;
import com.utils.JsonPathException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.util.Base64;

@Slf4j
public class FileEncrypterDecrypter {
    private Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

    public FileEncrypterDecrypter() throws Exception {
    }

    public void encrypt(String content, String fileName, FileCredentialsType type) throws Exception {
        File path = createDirectory(fileName);
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        storeSecretKey(encodedKey, type);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
        try (FileOutputStream fileOut = new FileOutputStream(path, false);
             CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)) {
            fileOut.write(iv);
            cipherOut.write(content.getBytes());
        }
    }

    private void storeSecretKey(String value, FileCredentialsType type) throws Exception {
        FileContents fileContents = new FileContents(value);
        ObjectMapper mapper = new ObjectMapper();
        String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileContents);
        this.createDirectory(CredentialsHelper.get(type, Extension.JSON));
        FileWriter fileWriter = new FileWriter(System.getProperty("user.dir") + "/src/main/resources/" + CredentialsHelper.get(type, Extension.JSON), false);
        BufferedWriter streamWriter = new BufferedWriter(fileWriter);
        streamWriter.write(prettyJson);
        streamWriter.close();
        fileWriter.close();
    }

    private Key getSecretKey(FileCredentialsType type) throws Exception {
        Key key = null;
        try {
            key = Key.get(CredentialsHelper.get(type, Extension.JSON));
        } catch (Exception e) {
            if ((key == null)) {
                throw new JsonPathException("The encrypted key returned null. Json file " + CredentialsHelper.get(type, Extension.JSON) + " does not exist or the path provided was incorrect.", ErrorCode.JSON_FILE);
            }
        }
        return key;
    }

    private File createDirectory(String path) throws Exception {
        ClassLoader test = this.getClass().getClassLoader();
        File file = new File(System.getProperty("user.dir") + "/src/main/resources/" + path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return file;
    }

    public String decrypt(String fileName, FileCredentialsType type) throws Exception {
        String content = null;
        byte[] fileIv = new byte[16];
        InputStream fileIn = null;
        CipherInputStream cipherIn = null;
        String s1 = null;
        try {
            fileIn = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileIn));
            Key encryptedKey = getSecretKey(type);
            byte[] decodedKey = Base64.getDecoder().decode(encryptedKey.key);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey, new IvParameterSpec(fileIv));
            cipherIn = new CipherInputStream(this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(fileName), cipher);
            InputStreamReader inputReader = new InputStreamReader(cipherIn);
            BufferedReader reader2 = new BufferedReader(inputReader);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader2.readLine()) != null) {
                sb.append(line);
            }
            s1 = sb.toString();
            content = s1.substring(s1.indexOf("{"));
            s1.trim();
        } catch (Exception e) {
            if (e instanceof JsonPathException) {
                throw e;
            } else if ((fileIn == null)
                    || (cipherIn == null)) {
                throw new EncPathException("The resource InputStream object returned null. Encrypted file " + fileName + " does not exist or the path provided was incorrect.", ErrorCode.ENC_FILE);
            } else if (s1 == null) {
                throw new EncPathException("The content inside the .enc file was empty. Make sure encrypted file " + fileName + " contains some form of encrypted content.", ErrorCode.ENC_FILE);
            }
        }
        return content;
    }
}
