package task_strategy;

import tasks.task_super_objects.AtomicTask;
import tasks.task_super_objects.ManagementTask;
import tasks.task_super_objects.ScheduledTask;

public interface TaskListVisitor {
	void allocateTask(AtomicTask task);
	void allocateTask(ScheduledTask task);
	void allocateTask(ManagementTask task);
}
