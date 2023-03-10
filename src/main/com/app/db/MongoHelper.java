package com.app.db;

import com.app.data.Credentials;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.vault.DataKeyOptions;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.utils.CDeco;
import com.utils.CredentialsType;
import com.utils.ErrorCode;
import com.utils.JsonPathException;
import lombok.extern.slf4j.Slf4j;
import org.bson.*;
import org.bson.json.JsonWriterSettings;
import static com.app.db.ConsoleDecoration.printSection;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Indexes.ascending;
import static com.utils.Constants.DETERMINISTIC;
import static java.util.Collections.singletonList;

@Slf4j
public class MongoHelper {

    private static final MongoNamespace ENCRYPTED_NS = new MongoNamespace("loki", "auth");
    private static final String LOCAL = "local";
    private static final JsonWriterSettings INDENT = JsonWriterSettings.builder().indent(true).build();
    private static int count;

    public static void setValue(String name, String pwd, CredentialsType type) throws Exception {
        try {
            printSection("MASTER KEY");
            byte[] masterKey = new MasterKey().get();

            ConnectionHelper connectionHelper = new ConnectionHelper(masterKey);

            ClientEncryption encryption = connectionHelper.getEncryptionClient();
            MongoClient client = connectionHelper.getMongoClient();
            MongoCollection<Document> vaultColl = connectionHelper.getVaultCollection();
            MongoCollection<Document> collection = client.getDatabase(ENCRYPTED_NS.getDatabaseName())
                    .getCollection(ENCRYPTED_NS.getCollectionName());

            printSection("CREATE KEY ALT NAMES UNIQUE INDEX");
                vaultColl.createIndex(ascending("keyAltNames"),
                        new IndexOptions().unique(true).partialFilterExpression(exists("keyAltNames")));
            log.info("{}{}=> Key alt dupe filter created.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
            log.info("{}{}=> Key alt names will not be duplicated.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);

            printSection("CREATE DATA ENCRYPTION KEYS");
            try {
                BsonBinary keyId = encryption.createDataKey(LOCAL, keyAltName(type.name()));
                log.info("{}{}=> New data encryption key with alt name {} was created.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, type.name(), CDeco.RESET.value);
            } catch (Exception e) {
                count = 1;
                log.info("{}{}=> Alternate name already exists.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
                log.info("{}{}=> New data encryption key was not created for {}{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, type.name(), CDeco.RESET.value);
            }

            printSection("DATABASE");

            Data data;
            if (count > 0) {
                data = new ExistingData(collection, encryption);
            } else {
                data = new NewData(collection, encryption);
            }
            data.set(name, pwd, type);
            connectionHelper.closeConnections();
        } catch (Exception e) {
            throw e;
        }
    }

    public static Credentials getValues(CredentialsType type) throws Exception {
        Credentials creds = new Credentials();
        try {
            printSection("MASTER KEY");
            byte[] masterKey = new MasterKey().get();

            ConnectionHelper connectionHelper = new ConnectionHelper(masterKey);

            ClientEncryption encryption = connectionHelper.getEncryptionClient();
            MongoClient client = connectionHelper.getMongoClient();
            MongoCollection<Document> vaultColl = connectionHelper.getVaultCollection();
            MongoCollection<Document> collection = client.getDatabase(ENCRYPTED_NS.getDatabaseName())
                    .getCollection(ENCRYPTED_NS.getCollectionName());

            printSection("CREATE KEY ALT NAMES UNIQUE INDEX");
            vaultColl.createIndex(ascending("keyAltNames"),
                    new IndexOptions().unique(true).partialFilterExpression(exists("keyAltNames")));
            log.info("{}{}=> Key alt dupe filter created.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
            log.info("{}{}=> Key alt names will not be duplicated.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);

            printSection("CREATE DATA ENCRYPTION KEYS");
            try {
                BsonBinary keyId = encryption.createDataKey(LOCAL, keyAltName(type.name()));
                throw new Exception("An existing data key was not found for type " + type.name() + ". Check database for existing data.");
            } catch (Exception e) {
                if (e.getMessage().contains("An existing data key was not found for type")) {
                    throw e;
                }
                count = 1;
               log.info("{}{}=> Alternate name already exists.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
                log.info("{}{}=> New data encryption key was not created for {}{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, type.name(), CDeco.RESET.value);
            }

            printSection("GET DATA");
            BsonBinary search = encryption.encrypt(new BsonString(type.name()), new EncryptOptions(DETERMINISTIC).keyAltName(type.name()));
            String doc = collection.find(eq("type", search)).first().toJson(INDENT);
            if (doc == null) {
                throw new JsonPathException("The database returned a null json response. Verify that the data exists in the database.", ErrorCode.JSON_FILE);
            }
            creds = Credentials.get(doc);
            connectionHelper.closeConnections();
        } catch (Exception e) {
            throw e;
        }
        return creds;
    }

    private static DataKeyOptions keyAltName(String altName) {
        return new DataKeyOptions().keyAltNames(singletonList(altName));
    }

}
