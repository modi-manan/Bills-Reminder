import org.junit.Test;
import org.manan.exceptions.BillsReminderException;
import org.manan.mail.MailsAPI;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class TestGmail {
    @Test
    public void testLabels() throws GeneralSecurityException, IOException {
        MailsAPI mailsAPI = new MailsAPI();
        List<String> ids = mailsAPI.readEmailIds("subject:HDFC Diners Credit Card Statement has:attachment ");
        System.out.println(ids.size());
        System.out.println(mailsAPI.readEmailBody(ids.get(0)));
    }
    @Test
    public void testSaveAttachment() throws GeneralSecurityException, IOException, BillsReminderException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
        boolean temp = new File(tempPath).mkdir();
        MailsAPI mailsAPI = new MailsAPI();
        List<String> ids = mailsAPI.readEmailIds("subject:ICICI Amazon Credit Card Statement has:attachment ");
        System.out.println(ids.size());
        mailsAPI.saveAttachment(tempPath, ids.get(1));
        System.out.println(tempPath);
    }
}
