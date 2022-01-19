package org.manan.bank;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import org.manan.utils.Utils;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;


public class ICICICoral extends Card {
    static {
        System.setProperty("org.apache.commons.logging.Log",
                "org.apache.commons.logging.impl.NoOpLog");
    }
    public String Q;
    protected String CARD_NAME;
    private static final String SEARCH_STRING = "SPENDS OVERVIEW";
    private final String PASSWORD;

    public ICICICoral() throws BillsReminderException {
        CARD_NAME = "ICICI Coral";
        Q = "subject:ICICI Coral Credit Card Statement has:attachment";
        PASSWORD = Utils.getInstance().getPassword(CARD_NAME);
    }


    @Override
    public String getQ() {
        return Q;
    }

    @Override
    public Bill readBill(String path) throws BillsReminderException {
        CircularFifoQueue<String> queue = new CircularFifoQueue<>(2);
        Bill bill = new Bill();
        String text = readPDF(path, PASSWORD);
        Scanner scanner = new Scanner(text);
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.contains(SEARCH_STRING)) {
                for (int i = 0; i < 5; i++) {
                    scanner.nextLine();
                }
                bill.setCardName(CARD_NAME);
                Logger.debug("Generated bill object");
                return generateBill(bill, queue.get(0), queue.get(1), scanner.nextLine());
            }else {
                queue.add(line);
            }
        }
        return bill;
    }



    private Bill generateBill(Bill bill, String minimumAmount, String totalAmount, String dueDate) {
        bill.setTotalDue(totalAmount.replace("`", ""));
        bill.setMinimumDue(minimumAmount.replace("`", ""));
        String month = dueDate.split(" ")[0];
        String year = dueDate.split(" ")[2];
        String day = dueDate.split(" ")[1].replace(",", "");
        int monthNumber = DateTimeFormatter.ofPattern("MMMM").withLocale(Locale.ENGLISH).parse(month).get(ChronoField.MONTH_OF_YEAR);
        Calendar calendar = getCalendar(year, String.valueOf(monthNumber), day);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = new SimpleDateFormat("yyyy-MM-dd'T'").format(calendar.getTime()) + "00:00:00.000Z";
        bill.setDate(date);
        return bill;
    }
}
