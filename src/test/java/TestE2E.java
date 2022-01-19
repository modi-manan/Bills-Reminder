import org.junit.Before;
import org.junit.Test;
import org.manan.bank.*;
import org.manan.exceptions.BillsReminderException;
import org.manan.mail.MailsAPI;
import org.manan.model.Bill;
import org.manan.tasks.TasksAPI;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Level;

public class TestE2E {

    @Before
    public void setLogLevels(){
        String[] loggers = { "org.apache.pdfbox.pdmodel.font.PDType0Font", "org.apache.pdfbox.pdmodel.font.PDType1Font" };
        for (String ln : loggers) {
            java.util.logging.Logger.getLogger(ln).setLevel(Level.SEVERE);
        }

    }

    @Test
    public void TestHDFC() throws GeneralSecurityException, IOException, BillsReminderException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
        Card card = new HDFCCard();
        MailsAPI mailsAPI = new MailsAPI();
        List<String> emailIds = mailsAPI.readEmailIds(card.getQ());
        String attachmentPath = mailsAPI.saveAttachment(tempPath, emailIds.get(0));
        Bill bill = card.readBill(attachmentPath);
        TasksAPI tasksAPI = new TasksAPI();
        tasksAPI.addTask(bill);
    }

    @Test
    public void TestAxis() throws GeneralSecurityException, IOException, BillsReminderException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
        Card card = new AxisCard();
        MailsAPI mailsAPI = new MailsAPI();
        List<String> emailIds = mailsAPI.readEmailIds(card.getQ());
        String attachmentPath = mailsAPI.saveAttachment(tempPath, emailIds.get(0));
        Bill bill = card.readBill(attachmentPath);
        TasksAPI tasksAPI = new TasksAPI();
        tasksAPI.addTask(bill);
    }

    @Test
    public void TestICICICoral() throws GeneralSecurityException, IOException, BillsReminderException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
        Card card = new ICICICoral();
        MailsAPI mailsAPI = new MailsAPI();
        List<String> emailIds = mailsAPI.readEmailIds(card.getQ());
        String attachmentPath = mailsAPI.saveAttachment(tempPath, emailIds.get(0));
        Bill bill = card.readBill(attachmentPath);
        TasksAPI tasksAPI = new TasksAPI();
        tasksAPI.addTask(bill);
    }

    @Test
    public void TestICICIAmazon() throws GeneralSecurityException, IOException, BillsReminderException {
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
        Card card = new ICICIAmazon();
        MailsAPI mailsAPI = new MailsAPI();
        List<String> emailIds = mailsAPI.readEmailIds(card.getQ());
        String attachmentPath = mailsAPI.saveAttachment(tempPath, emailIds.get(0));
        Bill bill = card.readBill(attachmentPath);
        TasksAPI tasksAPI = new TasksAPI();
        tasksAPI.addTask(bill);
    }
}
