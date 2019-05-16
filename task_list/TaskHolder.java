package task_list;

import java.util.ArrayList;
import java.util.List;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 *  Wrapper around a List for Task objects.
 */
public abstract class TaskHolder implements TaskList, AddTask {

	protected List<Task> taskList = new ArrayList<>();
	private int numberOfTasks = 0;
	
	@Override
	public Task getAndRemoveNextTask() {
		numberOfTasks--;
		return taskList.remove(0);
	}
	
	@Override
	public int numberOfTasks() {
		return numberOfTasks;
	}
	
	@Override
	public Task peekAtNextTask() {
		return taskList.get(0);
	}
	
	@Override
	public boolean isNotEmpty() {
		return (numberOfTasks > 0) ? true : false;
	}

	@Override
	public List<Task> getTaskList(){
		return taskList;
	}
	
	@Override
	public void addTask(Task t) {
		numberOfTasks++;
		taskList.add(t);
	}
	
}
