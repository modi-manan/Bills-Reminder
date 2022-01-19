package org.manan.bank;

import org.manan.exceptions.BillsReminderException;

public class ICICIAmazon extends ICICICoral{

    public ICICIAmazon() throws BillsReminderException {
        CARD_NAME = "ICICI Amazon";
        Q = "subject:ICICI Amazon Credit Card Statement has:attachment";
    }
}
