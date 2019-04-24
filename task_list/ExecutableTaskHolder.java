package task_list;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 */
public class ExecutableTaskHolder extends TaskHolder implements ExecutableTaskList{

	@Override
	public void addExecutableTask(Task task) {
		taskList.add(task);
	}
}
