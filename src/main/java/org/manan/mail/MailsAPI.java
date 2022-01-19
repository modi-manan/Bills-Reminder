package org.manan.mail;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.manan.authorization.Credentials;
import org.manan.exceptions.BillsReminderException;
import org.manan.utils.Utils;
import org.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Responsible for handling all the Google Mails API calls
 * @author Manan Modi
 */
public class MailsAPI {
    private static final String APPLICATION_NAME = "GMail API Statements";
    private static final String user = "me";
    private Gmail service = null;
    public MailsAPI() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, Credentials.GSON_FACTORY, Credentials.getInstance().getCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Read email body given email id
     * @param id
     * @return
     * @throws IOException
     */
    public String readEmailBody(String id) throws IOException {
        Message message = service.users().messages().get(user, id).execute();
        return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData()));
    }

    /**
     * Save the pdf file as attachment to the given path
     * @param path
     * @param messageId
     * @return
     * @throws IOException
     * @throws BillsReminderException
     */
    public String saveAttachment(String path, String messageId) throws IOException, BillsReminderException {
        Message message = service.users().messages().get(user, messageId).execute();
        List<MessagePart> parts = message.getPayload().getParts();
        String attachmentId = "";
        String fileName = "";
        for(MessagePart part : parts){
            if (!part.getFilename().equals("")){
                attachmentId = part.getBody().getAttachmentId();
                fileName = part.getFilename();
                break;
            }
        }
        if (attachmentId.equals("") || fileName.equals("")){
            Logger.error("attachmentId : " + attachmentId + " fileName : " + fileName);
            throw new BillsReminderException("Empty filename or attachment id");
        }
        Logger.debug("Extracted FileName : " + fileName);
        MessagePartBody attachment = service.users().messages().attachments().get(user, messageId, attachmentId).execute();
        byte[] fileData = Base64.decodeBase64(attachment.getData());
        Utils.createDirectory(path);
        String attachmentPath = path + File.separator + fileName;
        FileOutputStream fileStream = new FileOutputStream(attachmentPath);
        fileStream.write(fileData);
        fileStream.close();
        Logger.debug("Successfully saved the attachment");
        return attachmentPath;
        //System.out.println(fileData);
    }

    /**
     * Read email ids for given search string
     * @param q
     * @return
     * @throws IOException
     */
    public List<String> readEmailIds(String q) throws IOException {
        ListMessagesResponse list = service.users().messages().list(user).setQ(q).execute();
        List<Message> messages = list.getMessages();
        List<String> result = messages.stream().map(Message::getId).collect(Collectors.toList());
        Logger.debug("Read " + result.size() + " EmailIds");
        return result;
    }
}
