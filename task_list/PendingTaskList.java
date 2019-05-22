package task_list;

import tasks.task_super_objects.Task;
import utils.logger.Log;

/**
 * 
 * @author Steve Brown
 *
 */
public interface PendingTaskList {

	// Add a task to the pending task map.
	void addTask(TaskPendingType reason, Task t);

	// Write all pending tasks to the log file.
	void logPendingTasks(Log log);
	
	// Write all pending tasks of the specified type to the log file.
	void logPendingTasks(Log log, TaskPendingType type);
}
