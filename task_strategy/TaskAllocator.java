package task_strategy;

import task_list.ExecutableTaskList;
import task_list.ScheduledTaskList;
import tasks.task_super_objects.AtomicTask;
import tasks.task_super_objects.ManagementTask;
import tasks.task_super_objects.ScheduledTask;

/**
 * 
 * @author Steve Brown
 *
 * Uses the Visitor pattern to add a Task to the required TaskList.
 * Uses the Strategy pattern to implement a TaskList.
 * 
 */
public class TaskAllocator implements TaskListVisitor, TaskAllocatorStrategy {
	
	private ScheduledTaskList scheduledTasks;
	private ExecutableTaskList executableTasks;

	@Override
	public void setSchedulableTasks(ScheduledTaskList scheduledTasks) {
		this.scheduledTasks = scheduledTasks;
	}

	@Override
	public void setExecutableTasks(ExecutableTaskList executableTasks) {
		this.executableTasks = executableTasks;
	}
	
	@Override
	public void allocateTask(AtomicTask task) {
		executableTasks.addTask(task);
	}

	@Override
	public void allocateTask(ScheduledTask task) {
		scheduledTasks.addTask(task);
	}

	@Override
	public void allocateTask(ManagementTask task) {
		executableTasks.addTask(task);
	}
}
