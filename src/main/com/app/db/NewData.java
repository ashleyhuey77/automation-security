package com.app.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.vault.EncryptOptions;
import com.mongodb.client.vault.ClientEncryption;
import com.utils.CDeco;
import com.utils.CredentialsType;
import lombok.extern.slf4j.Slf4j;
import org.bson.*;

import static com.utils.Constants.DETERMINISTIC;

@Slf4j
public class NewData implements Data {

    private static ClientEncryption encryption;
    private static MongoCollection<Document> collection;

    public NewData(MongoCollection<Document> collection, ClientEncryption encryption) {

        this.collection = collection;
        this.encryption = encryption;
    }

    @Override
    public void set(String name, String pwd, CredentialsType type) throws Exception {
        BsonBinary un = encryption.encrypt(new BsonString(name), new EncryptOptions(DETERMINISTIC).keyAltName(type.name()));
        BsonBinary pass = encryption.encrypt(new BsonString(pwd), new EncryptOptions(DETERMINISTIC).keyAltName(type.name()));
        BsonBinary ty = encryption.encrypt(new BsonString(type.name()), new EncryptOptions(DETERMINISTIC).keyAltName(type.name()));
        Document doc = new Document("name", un)
                .append("password", pass)
                .append("type", ty);
        collection.insertOne(doc);
        log.info("{}{}=> New document was added.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
    }

}
