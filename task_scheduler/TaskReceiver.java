package task_scheduler;

import tasks.task_super_objects.Task;

public interface TaskReceiver {
	<T extends Task> void accept(T t);
}
