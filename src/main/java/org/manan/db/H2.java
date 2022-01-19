package org.manan.db;

import org.h2.jdbcx.JdbcDataSource;
import org.manan.exceptions.BillsReminderException;
import org.manan.model.Bill;
import org.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for the DB connection to keep track of already processed bills.
 * @author Manan Modi
 */
public class H2 {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS BILLS(CARDNAME VARCHAR(100), TOTALDUE VARCHAR(10), MINIMUMDUE  VARCHAR(10), DATE VARCHAR(30), PRIMARY KEY(CARDNAME,DATE));";
    static final String INSERT_QUERY = "INSERT INTO BILLS VALUES (?, ?, ?, ?)";
    static final String SELECT_ALL_QUERY = "SELECT * FROM BILLS";
    static final String SELECT_QUERY = "SELECT * FROM BILLS WHERE CARDNAME = ? AND DATE=?;";
    static final String DELETE_QUERY = "DELETE FROM BILLS WHERE CARDNAME = ? AND DATE=?;";
    static final String USER = "user";
    static final String PASS = "admin";
    private JdbcDataSource dataSource = new JdbcDataSource();

    public H2() throws BillsReminderException {
        try {
            dataSource.setURL(DB_URL);
            dataSource.setUser(USER);
            dataSource.setPassword(PASS);
            Class.forName(JDBC_DRIVER);
            Logger.debug("Connecting to database...");
            try (Connection conn = dataSource.getConnection()) {
                Logger.debug("Connection Successful");
                PreparedStatement statement = conn.prepareStatement(TABLE_CREATE);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Logger.error("Connection to Database Failed");
                Logger.error(e);
                throw new BillsReminderException(e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            Logger.error("Connection to Database Failed");
            Logger.error(e);
            throw new BillsReminderException(e.getMessage());
        }
    }

    /**
     * Checks if the given bill exists in DB
     * @param bill
     * @return
     * @throws BillsReminderException
     */
    public Optional<Bill> get(Bill bill) throws BillsReminderException{
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
            statement.setString(1, bill.getCardName());
            statement.setString(2, bill.getDate());
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    Logger.debug("Found entry for given query...");
                    return Optional.of(bill);
                }
            }
        } catch (SQLException e) {
            Logger.error(e);
            throw new BillsReminderException(e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Deletes the give n entry from DB
     * @param bill
     * @throws BillsReminderException
     */
    public void delete(Bill bill) throws BillsReminderException{
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setString(1, bill.getCardName());
            statement.setString(2, bill.getDate());
            statement.executeUpdate();
        } catch (SQLException e) {
            Logger.error(e);
            throw new BillsReminderException(e.getMessage());
        }
    }

    /**
     * Get all bills
     * @return
     * @throws BillsReminderException
     */
    public List<Bill> getAll() throws BillsReminderException {
        List<Bill> bills = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY);
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                Bill bill = new Bill();
                bill.setCardName(resultSet.getString(1));
                bill.setTotalDue(resultSet.getString(2));
                bill.setMinimumDue(resultSet.getString(3));
                bill.setDate(resultSet.getString(4));
                bills.add(bill);
            }
        } catch (SQLException e) {
            Logger.error(e);
            throw new BillsReminderException(e.getMessage());
        }
        return bills;
    }

    /**
     * Add a new bill to DB
     * @param bill
     * @throws BillsReminderException
     */
    public void insertBill(Bill bill) throws BillsReminderException {
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            statement.setString(1, bill.getCardName());
            statement.setString(2, bill.getTotalDue());
            statement.setString(3, bill.getMinimumDue());
            statement.setString(4, bill.getDate());
            statement.executeUpdate();
            Logger.debug("Successfully inserted new Bill");
        } catch (SQLException e) {
            Logger.error(e);
            throw new BillsReminderException(e.getMessage());
        }
    }

}
