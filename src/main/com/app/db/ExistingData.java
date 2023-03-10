package com.app.db;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.vault.ClientEncryption;
import com.utils.CDeco;
import com.utils.CredentialsType;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinary;
import org.bson.BsonString;
import org.bson.Document;
import com.mongodb.client.model.vault.EncryptOptions;

@Slf4j
public class ExistingData implements Data {

    private static MongoCollection<Document> collection;
    private static ClientEncryption encryption;
    private static final String DETERMINISTIC = "AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic";

    public ExistingData(MongoCollection<Document> collection, ClientEncryption encryption) {
        this.collection = collection;
        this.encryption = encryption;
    }

    @Override
    public void set(String name, String pwd, CredentialsType type) throws Exception {
        BsonBinary search = encryption.encrypt(new BsonString(type.name()), new EncryptOptions(DETERMINISTIC).keyAltName(type.name()));
        Document updateData = new Document("name", name).append("password", pwd);
        Document setData = new Document().append("$set",updateData);
        collection.updateOne(Filters.eq("type", search), setData);
        log.info("{}{}=> Existing document was updated.{}", CDeco.CYAN_TEXT.value, CDeco.BLACK_BACKGROUND.value, CDeco.RESET.value);
    }
}
