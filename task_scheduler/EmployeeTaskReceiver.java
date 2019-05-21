package task_scheduler;

import people.employees.Manager;
import tasks.task_super_objects.Task;

public interface EmployeeTaskReceiver {
	<T extends Task> void accept(T t, Manager manager);
}
