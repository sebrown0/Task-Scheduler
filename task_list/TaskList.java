package task_list;

import java.util.List;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 */
public interface TaskList extends AddTask{
		
	int numberOfTasks();
	
	boolean isNotEmpty();
	
	Task getAndRemoveNextTask();
	
	Task peekAtNextTask();
	
	List<Task> getTaskList();
}
