package org.manan.scheduler;

import org.manan.bank.*;
import org.manan.db.H2;
import org.manan.exceptions.BillsReminderException;
import org.manan.mail.MailsAPI;
import org.manan.model.Bill;
import org.manan.tasks.TasksAPI;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The scheduler job that orchestrates whole process.
 * @author Manan Modi
 */
public class DailyScheduler implements Job {

    private final String pdfPath = System.getProperty("java.io.tmpdir") + File.separator + "attachments";
    private TasksAPI tasksAPI;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        H2 h2 = null;
        try {
            h2 = new H2();
        } catch (BillsReminderException e) {
            Logger.error(e, "Trouble connecting to DB");
        }
        try {
            tasksAPI = new TasksAPI();
            tasksAPI.cleanUp();
            List<Card> cards = new ArrayList<>();
            cards.add(new HDFCCard());
            cards.add(new AxisCard());
            cards.add(new ICICICoral());
            cards.add(new ICICIAmazon());
            for (Card card : cards) {
                getBillAndScheduleTask(card, h2);
            }
        } catch (BillsReminderException | GeneralSecurityException | IOException e) {
            //Just logging the error as not to stop the scheduler.
            Logger.error(e);
        } catch (Exception e) {
            Logger.error(e);
            JobExecutionException jobExecutionException =
                    new JobExecutionException(e);
            jobExecutionException.setUnscheduleAllTriggers(true);
            throw jobExecutionException;
        }
    }

    // Method to get the latest bill for given bank and add a task if bill not found in DB
    private void getBillAndScheduleTask(Card card, H2 h2) throws GeneralSecurityException, IOException, BillsReminderException {
        MailsAPI mailsAPI = new MailsAPI();
        List<String> emailIds = mailsAPI.readEmailIds(card.getQ());
        String attachmentPath = mailsAPI.saveAttachment(pdfPath, emailIds.get(0));
        Bill bill = card.readBill(attachmentPath);
        Optional<Bill> billDB = Optional.empty();
        if(h2 != null){
            try{
                billDB = h2.get(bill);
            } catch (BillsReminderException e) {
                Logger.error(e, "Trouble Getting the bill from to DB");
            }
        }
        if(billDB.isPresent()){
            Logger.debug("Bill already processed once");
        }else{
            tasksAPI.addTask(bill);
            if(h2 != null){
                h2.insertBill(bill);
            }
        }
    }
}
