package task_list;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 */
public interface ExecutableTaskList extends TaskList {
	void addExecutableTask(Task task);
}
