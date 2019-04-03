/**
 * 
 */
package task_scheduler;

import tasks.task_creators.TaskConsumer;

/**
 * @author Steve Brown
 *
 *  Interface for TaskManager.
 */
public interface Manager {
	void manageTask(TaskConsumer task);
}
