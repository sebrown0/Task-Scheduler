package task_list;

import java.util.Collections;

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
}
