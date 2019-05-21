package task_scheduler;

import tasks.task_super_objects.Task;

public interface DepartmentTaskReceiver {
	<T extends Task> void accept(T t);
}
