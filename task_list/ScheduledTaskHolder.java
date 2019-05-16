package task_list;

import java.util.Collections;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 */
public class ScheduledTaskHolder extends TaskHolder implements ScheduledTaskList{

	@Override
	public void sort() {
		Collections.sort(taskList, null);
	}
	
	@Override
	public void addTask(Task t) {
		incrementNumOfTasks();
		taskList.add(t);
		sort();
	}
}
