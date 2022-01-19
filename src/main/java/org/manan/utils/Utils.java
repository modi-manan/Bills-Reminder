package org.manan.utils;

import com.google.gson.Gson;
import org.manan.exceptions.BillsReminderException;
import org.tinylog.Logger;
import org.manan.model.Passwords;

import java.io.*;

/**
 * Singleton class responsible  for miscellaneous stuff such as getting passwords and
 */
public class Utils {
    private static Utils utils;
    private final String PASSWORDS_FILE_PATH = "/passwords.config.json";
    private final String ICICICORAL = "ICICI Coral";
    private final String ICICIAMAZON = "ICICI Amazon";
    private final String HDFC = "HDFC Diners Club Privilege";
    private final String AXIS = "Axis Bank Flipkart";
    private Passwords passwords;
    private Utils() throws BillsReminderException {
        Gson gson = new Gson();
        try(Reader reader = new InputStreamReader(Utils.class.getResourceAsStream(PASSWORDS_FILE_PATH))){
            passwords = gson.fromJson(reader, Passwords.class);
        }catch (IOException e){
            Logger.error(e, "Failed to read the passwords file");
            throw new BillsReminderException("Failed to read the passwords file");
        }
    }

    public static Utils getInstance() throws BillsReminderException {
        if(utils == null){
            utils = new Utils();
        }
        return utils;
    }

    public String getPassword(String bankName){
        switch (bankName){
            case ICICIAMAZON:
            case ICICICORAL:
                return passwords.getIcici();
            case AXIS:
                return passwords.getAxis();
            case HDFC:
                return passwords.getHdfc();
        }
        return "";
    }

    public static void createDirectory(String path){
        File directory = new File(path);
        if (! directory.exists()){
            directory.mkdirs();
        }
    }
}
