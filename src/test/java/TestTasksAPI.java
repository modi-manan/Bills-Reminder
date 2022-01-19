
import com.google.api.services.tasks.model.Task;
import org.junit.Test;
import org.manan.model.Bill;
import org.manan.tasks.TasksAPI;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestTasksAPI {
    @Test
    public void testTasks() throws GeneralSecurityException, IOException {
        Bill bill = new Bill();
        bill.setCardName("Axis");
        bill.setMinimumDue("1");
        bill.setTotalDue("0");
        bill.setDate(new SimpleDateFormat("yyyy-MM-dd'T'").format(Calendar.getInstance().getTime()) + "00:00:00.000Z");
        TasksAPI tasksAPI = new TasksAPI();
        Task task = tasksAPI.addTask(bill);
        tasksAPI.deleteTask(task);
    }
}
