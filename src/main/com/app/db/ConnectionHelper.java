package com.app.db;
import com.app.SecurityHelper;
import com.app.file.*;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.utils.CDeco;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class ConnectionHelper {

    private static final MongoNamespace VAULT_NS = new MongoNamespace("encryption", "__keyVault");
    private static final MongoNamespace ENCRYPTED_NS = new MongoNamespace("loki", "auth");
    private static final String LOCAL = "local";
    private final Map<String, Map<String, Object>> kmsProviders;
    private final ClientEncryption encryption;
    private MongoClient client;

    public ConnectionHelper(byte[] masterKey) {
        setTLSClientSettings();
        ConsoleDecoration.printSection("INITIALIZATION");
        this.kmsProviders = generateKmsProviders(masterKey);
        this.encryption = createEncryptionClient();
        this.client = createMongoClient();
    }

    private void setTLSClientSettings() {
        System.getProperties().setProperty("jdk.tls.client.protocols", "TLSv1.2");
    }

    private MongoClient createMongoClient() {
        AutoEncryptionSettings aes = AutoEncryptionSettings.builder()
                .keyVaultNamespace(VAULT_NS.getFullName())
                .kmsProviders(kmsProviders)
                .bypassAutoEncryption(true)
                .build();
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(connectionString())
                .autoEncryptionSettings(aes)
                .build();
        log.info("{}{}=> Creating MongoDB client with automatic decryption.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
        return MongoClients.create(mcs);
    }

    private ClientEncryption createEncryptionClient() {
        MongoClientSettings kvmcs = MongoClientSettings.builder().applyConnectionString(connectionString()).build();
        ClientEncryptionSettings ces = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(kvmcs)
                .keyVaultNamespace(VAULT_NS.getFullName())
                .kmsProviders(kmsProviders)
                .build();
        log.info("{}{}=> Creating encryption client.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
        return ClientEncryptions.create(ces);
    }

    public MongoClient resetMongoClient() {
        client.close();
        try {
            // sleep to make sure we are not reusing the Data Encryption Key Cache.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client = createMongoClient();
    }

    public MongoCollection<Document> getVaultCollection() {
        return client.getDatabase(VAULT_NS.getDatabaseName()).getCollection(VAULT_NS.getCollectionName());
    }

    public ClientEncryption getEncryptionClient() {
        return encryption;
    }

    public MongoClient getMongoClient() {
        return client;
    }

    public void cleanCluster() {
        log.info("{}{}=> Cleaning entire cluster.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
        client.getDatabase(VAULT_NS.getDatabaseName()).drop();
        client.getDatabase(ENCRYPTED_NS.getDatabaseName()).drop();
    }

    private Map<String, Map<String, Object>> generateKmsProviders(byte[] masterKey) {
        log.info("{}{}=> Creating local Key Management System using the master key.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
        return new HashMap<String, Map<String, Object>>() {{
            put(LOCAL, new HashMap<String, Object>() {{
                put("key", masterKey);
            }});
        }};
    }

    public void closeConnections() {
        encryption.close();
        client.close();
    }

    private ConnectionString getEncryptedConnectionString() throws Exception {
        ConnectionString result = null;
        try {
            FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
            String fileName = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
            FileCredentials credentials = FileCredentials.get(fed.decrypt(fileName, FileCredentialsType.DATABASE));
            return new ConnectionString("mongodb+srv://" + SecurityHelper.decrypt(credentials.name) + ":" + SecurityHelper.decrypt(credentials.password) + "@" + SecurityHelper.decrypt(credentials.uri));
        } catch (Exception e) {
            throw e;
        }
    }

    private ConnectionString connectionString() {
        try {
            return getEncryptedConnectionString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
