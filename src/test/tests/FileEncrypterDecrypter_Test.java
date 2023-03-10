package tests;

import com.app.SecurityHelper;
import com.app.file.*;
import com.utils.CredentialsType;
import com.utils.EncPathException;
import com.utils.JsonFormatter;
import com.utils.JsonPathException;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.app.Decrypter;
import com.app.Encrypter;
import com.app.data.Credentials;
import com.app.data.DataMapper;

public class FileEncrypterDecrypter_Test {
	
  @Test
  public void verifyEncryptToDB() throws Exception {
	  Encrypter encrypter = new Encrypter("lala", "heehaw", CredentialsType.TEST);
	  encrypter.encryptToDB();
  }
  
  @Test
  public void verifyDecryptFromDB() throws Exception {
	  Credentials content = Decrypter.decryptFromDB(CredentialsType.POST_EDITOR);
      Assert.assertTrue((content != null));
      Assert.assertTrue((DataMapper.getCredentials().name != null));
      Assert.assertTrue((DataMapper.getCredentials().password != null));
  }

    @Test
    public void verifyEncryptFile() throws Exception {
        String uName = SecurityHelper.encrypt("ahuey");
        String uPwd = SecurityHelper.encrypt("blahblah");
        String uri = SecurityHelper.encrypt("someurl");
        String jsonObject = JsonFormatter.getJSONObject(uName, uPwd, uri);
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
        String fileName = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
        fed.encrypt(jsonObject, fileName, FileCredentialsType.DATABASE);
    }

    @Test
    public void verifyDecryptFromFile() throws Exception {
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
        String fileName = CredentialsHelper.get(FileCredentialsType.DATABASE, Extension.ENC);
        FileCredentials credentials = FileCredentials.get(fed.decrypt(fileName, FileCredentialsType.DATABASE));
        Assert.assertTrue((SecurityHelper.decrypt(credentials.name) != null));
        Assert.assertTrue((SecurityHelper.decrypt(credentials.password) != null));
        Assert.assertTrue((SecurityHelper.decrypt(credentials.uri) != null));
    }

    @Test(expectedExceptions = { EncPathException.class })          //Passes
    public void verifyEncPathException() throws Exception {
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
        String fileName = CredentialsHelper.get(FileCredentialsType.ERROR, Extension.JSON);
        FileCredentials credentials = FileCredentials.get(fed.decrypt(fileName, FileCredentialsType.ERROR));
    }

    @Test(expectedExceptions = { JsonPathException.class })          //Passes
    public void verifyJsonPathException() throws Exception {
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter();
        String fileName = CredentialsHelper.get(FileCredentialsType.ERROR, Extension.ENC);
        FileCredentials credentials = FileCredentials.get(fed.decrypt(fileName, FileCredentialsType.ERROR));
    }
  
}
