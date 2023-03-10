package com.app.db;

import com.utils.CDeco;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.SecureRandom;
import static com.utils.Constants.MASTER_KEY_FILENAME;
import static com.utils.Constants.SIZE_MASTER_KEY;

@Slf4j
public class MasterKey {

    public byte[] get() {
        return new MasterKey().generate();
    }

    private byte[] generate() {
        final byte[] masterKey = generateNewOrRetrieveMasterKeyFromFile(MASTER_KEY_FILENAME);
        return masterKey;
    }

    private byte[] generateNewOrRetrieveMasterKeyFromFile(String filename) {
        byte[] masterKey = null;
        try {
            masterKey = retrieveMasterKeyFromFile(filename);
            log.info("{}{}An existing Master Key was found in file \"{}\".{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, filename, CDeco.RESET.value);
        } catch (Exception e) {
            masterKey = generateMasterKey();
            saveMasterKeyToFile(filename, masterKey);
            log.info("{}{}A new Master Key has been generated and saved to file \"{}\".{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, filename, CDeco.RESET.value);
        }
        return masterKey;
    }

    private byte[] retrieveMasterKeyFromFile(String filename) throws Exception {
        try (InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(filename);) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[96];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }

    private byte[] generateMasterKey() {
        byte[] masterKey = new byte[SIZE_MASTER_KEY];
        new SecureRandom().nextBytes(masterKey);
        return masterKey;
    }

    private void saveMasterKeyToFile(String filename, byte[] masterKey) {
        try (FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources/" + filename)) {
            fos.write(masterKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
