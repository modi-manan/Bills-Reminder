package org.manan.model;

/**
 * Class representing a Bill
 * @author Manan Modi
 */
public class Bill {

    private String cardName;
    private String totalDue;
    private String minimumDue;
    private String date;

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(String totalDue) {
        this.totalDue = totalDue;
    }

    public String getMinimumDue() {
        return minimumDue;
    }

    public void setMinimumDue(String minimumDue) {
        this.minimumDue = minimumDue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
