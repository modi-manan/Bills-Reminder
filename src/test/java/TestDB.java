import org.junit.Before;
import org.junit.Test;
import org.manan.db.H2;
import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/*
* H2 should be running before these tests
* */
public class TestDB {

    private Bill getTemplateBill(){
        Bill bill = new Bill();
        bill.setCardName("Axis");
        bill.setMinimumDue("1");
        bill.setTotalDue("0");
        bill.setDate(new SimpleDateFormat("yyyy-MM-dd'T'").format(Calendar.getInstance().getTime()) + "00:00:00.000Z");
        return bill;
    }

    @Before


    @Test
    public void testInsertAndDelete() throws BillsReminderException {
        H2 h2 = new H2();
        Bill bill = getTemplateBill();
        h2.insertBill(bill);
        Optional<Bill> billTest= h2.get(bill);
        assertTrue(billTest.isPresent());
        h2.delete(bill);
        billTest= h2.get(bill);
        assertFalse(billTest.isPresent());
    }



    @Test
    public void testGetAll() throws BillsReminderException {
        H2 h2 = new H2();
        Bill bill = getTemplateBill();
        h2.insertBill(bill);
        List<Bill> bills = h2.getAll();
        assertNotEquals(0, bills.size());
        h2.delete(bill);
    }
}
