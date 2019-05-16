package task_list;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 */
public interface PendingTaskList {
	
	// Add a task to the pending task map.
	void addTask(TaskPending reason, Task t);
	
	// Write all pending tasks to the log file.
	void logPendingTasks();
}
