package org.manan.tasks;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.Tasks;
import org.manan.authorization.Credentials;
import org.manan.model.Bill;
import org.tinylog.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Responsible for handling all the Google Tasks API calls
 * @author Manan Modi
 */
public class TasksAPI {
    private static final String APPLICATION_NAME = "GMail API Statements";
    private static final String BILLS = "Bills";
    private static final String COMPLETED = "completed";
    private Tasks service = null;
    private String billsTaskListId = null;

    public TasksAPI() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Tasks.Builder(HTTP_TRANSPORT, Credentials.GSON_FACTORY, Credentials.getInstance().getCredential())
                .setApplicationName(APPLICATION_NAME)
                .build();
        billsTaskListId = getBillsTaskListId();
        if(billsTaskListId == null){
            billsTaskListId = createBillTaskList().getId();
        }
    }

    /**
     * Adds a new task to google tasks if one isn't present for the given bill.
     * @param bill
     * @return
     * @throws IOException
     */
    public Task addTask(Bill bill) throws IOException {
        List<Task> tasks = service.tasks().list(billsTaskListId).setShowCompleted(true).setShowHidden(true).execute().getItems();
        Task newTask = generateTask(bill);
        if(tasks == null){
            Logger.debug("Added new Task");
            return insertTask(newTask);
        }
        for(Task task : tasks){
            if(task.getTitle().contains(bill.getCardName())){
                if(newTask.getDue().equals(task.getDue())){
                    Logger.debug("Task already added. Returning the same");
                    return task;
                }else{
                    return insertTask(newTask);
                }
            }
        }
        return insertTask(newTask);
    }

    /**
     * Cleans up all the completed tasks.
     */
    public void cleanUp() {
        try {
            List<Task> tasks = service.tasks().list(billsTaskListId).setShowCompleted(true).setShowHidden(true).execute().getItems();
            if(tasks != null){
                for(Task task : tasks){
                    if(task.getStatus().equals(COMPLETED)){
                        deleteTask(task);
                    }
                }
            }
        } catch (IOException e) {
            //Not a necessary functionality. Just logging the error.
            Logger.error(e,"Trouble cleaning up the old Tasks");
        }
    }

    /**
     * Deletes given task
     * @param task
     * @throws IOException
     */
    public void deleteTask(Task task) throws IOException {
        service.tasks().delete(billsTaskListId, task.getId()).execute();
    }

    private Task insertTask(Task task) throws IOException {
        return service.tasks().insert(billsTaskListId, task).execute();
    }

    private TaskList createBillTaskList() throws IOException {
        TaskList taskList = new TaskList();
        taskList.setTitle(BILLS);
        return service.tasklists().insert(taskList).execute();
    }

    private Task generateTask(Bill bill){
        Task task = new Task();
        task.setTitle(bill.getTotalDue() + " due for " + bill.getCardName());
        DateTime date = DateTime.parseRfc3339(bill.getDate());
        task.setDue(date);
        task.setParent(billsTaskListId);
        return task;
    }

    private String getBillsTaskListId() throws IOException {
        List<TaskList> taskLists = service.tasklists().list().execute().getItems();
        for(TaskList taskList: taskLists){
            if(taskList.getTitle().equals(BILLS)){
                return taskList.getId();
            }
        }
        return null;
    }
}
