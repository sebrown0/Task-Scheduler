package task_strategy;

import task_list.ExecutableTaskList;
import task_list.ScheduledTaskList;

public interface TaskAllocatorStrategy {
	
	void setSchedulableTasks(ScheduledTaskList scheduledTasks); 

	void setExecutableTasks(ExecutableTaskList executableTasks);
}
