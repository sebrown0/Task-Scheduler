package task_strategy;

import tasks.task_super_objects.AtomicTask;
import tasks.task_super_objects.ManagementTask;
import tasks.task_super_objects.ScheduledTask;

public interface TaskListVisitor {
	void addTask(AtomicTask task);
	void addTask(ScheduledTask task);
	void addTask(ManagementTask task);
}
