package org.manan.bank;

import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import org.manan.utils.Utils;
import org.tinylog.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class AxisCard extends Card {
    public String Q;
    private final String CARD_NAME = "Axis Bank Flipkart";
    private static final String SEARCH_STRING_1 = "Payment Due Date";
    private static final String SEARCH_STRING_2 = "Total Payment Due";
    private final String PASSWORD;

    public AxisCard() throws BillsReminderException {
        Q = "subject:Axis Bank Credit Card Statement has:attachment";
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
            if(line.contains(SEARCH_STRING_1)){
                generateBill(bill, scanner.nextLine());
            }
            if(line.contains(SEARCH_STRING_2)){
                Logger.debug("Generated bill object");
                return addTotalAmountDue(bill, scanner.nextLine());
            }
        }
        return bill;
    }

    private Bill addTotalAmountDue(Bill bill, String statement) {
        String[] values = statement.split(" ");
        bill.setTotalDue(values[8]);
        return bill;
    }

    private void generateBill(Bill bill, String statement){
        //statement = statement.replace(',', ' ');
        String[] values = statement.split(" ");
        bill.setCardName(CARD_NAME);
        bill.setMinimumDue(values[8]);
        String[] dates = values[3].split("/");
        Calendar calendar = getCalendar(dates[2], dates[1], dates[0]);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        String date = new SimpleDateFormat("yyyy-MM-dd'T'").format(calendar.getTime()) + "00:00:00.000Z";
        bill.setDate(date);
    }
}
