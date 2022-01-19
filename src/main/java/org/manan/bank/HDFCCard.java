package org.manan.bank;

import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import org.manan.utils.Utils;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;


public class HDFCCard extends Card {
    public String Q;
    private final String CARD_NAME = "HDFC Diners Club Privilege";
    private static final String SEARCH_STRING = "Payment Due Date";
    private final String PASSWORD;

    public HDFCCard() throws BillsReminderException {
        Q = "subject:HDFC Bank Credit Card Statement has:attachment ";
        PASSWORD = Utils.getInstance().getPassword(CARD_NAME);
    }


    @Override
    public String getQ() {
        return Q;
    }

    @Override
    public Bill readBill(String path) throws BillsReminderException {
        Bill bill = new Bill();
        String text = readPDF(path, PASSWORD);
        Scanner scanner = new Scanner(text);
        while(scanner.hasNext()){
            String line = scanner.nextLine();
            if(line.contains(SEARCH_STRING)){
                Logger.debug("Generated bill object");
                return generateBill(bill, scanner.nextLine());
            }
        }
        return bill;
    }

    private Bill generateBill(Bill bill, String statement){
        String[] values = statement.split(" ");
        bill.setCardName(CARD_NAME);
        bill.setTotalDue(values[1]);
        bill.setMinimumDue(values[2]);
        String[] dates = values[0].split("/");
        Calendar calendar = getCalendar(dates[2], dates[1], dates[0]);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = new SimpleDateFormat("yyyy-MM-dd'T'").format(calendar.getTime()) + "00:00:00.000Z";
        bill.setDate(date);
        return bill;
    }
}
