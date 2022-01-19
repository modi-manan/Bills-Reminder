package org.manan.bank;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.Integer.parseInt;


/**
 * The abstract class representing card.
 * The abstract methods must be implemented for all the cards.
 * @author Manan Modi
 */
public abstract class Card {
    /**
     * @return query string for the card.
     */
    public abstract String getQ();

    /**
     * Logic to read the pdf file from given location and return Bill Object
     * @param path path to the pdf file
     * @return the bill object read from the bill pdf
     * @throws IOException
     * @throws BillsReminderException
     */
    public abstract Bill readBill(String path) throws IOException, BillsReminderException;

    /**
     * Reads the bill pdf into a string.
     * @param path path to the Bill PDF
     * @param password password for the pdf file
     * @return the whole bill read into a string.
     * @throws BillsReminderException
     */
    protected String readPDF(String path, String password) throws BillsReminderException {
        String text;
        try (PDDocument document = PDDocument.load(new File(path), password)){
            text = new PDFTextStripper().getText(document);
            Logger.debug("Read PDF Document Successfully");
        }catch (IOException e){
            Logger.error(e, "Failed to open the BILL file");
            throw new BillsReminderException("Failed to open the BILL file");
        }
        return text;
    }

    /**
     * Generates the Calender Object given the date.
     * @param year
     * @param month
     * @param day
     * @return
     */
    protected Calendar getCalendar(String year, String month, String day){
        Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calender.set(parseInt(year), parseInt(month) - 1, parseInt(day));
        calender.set(Calendar.HOUR_OF_DAY, 0);
        calender.set(Calendar.MINUTE, 0);
        calender.set(Calendar.SECOND, 0);
        calender.set(Calendar.MILLISECOND, 0);
        return calender;
    }
}
