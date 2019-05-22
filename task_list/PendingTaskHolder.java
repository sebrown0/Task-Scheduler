package task_list;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import tasks.task_super_objects.Task;
import utils.logger.Log;
import utils.logger.Loggable;

/**
 * 
 * @author Steve Brown
 *
 *  Holds the tasks that the department manager 
 *  has not been able to assign or tasks that have failed.
 *  
 */
public class PendingTaskHolder implements PendingTaskList, Loggable{

	Multimap<TaskPendingType, Task> pendingTasks = ArrayListMultimap.create();
	
	@Override
	public void addTask(TaskPendingType reason, Task t) {
		pendingTasks.put(reason, t);
	}

	public Multimap<TaskPendingType, Task> getMapOfPendingType(TaskPendingType type){
		return Multimaps.filterKeys(pendingTasks, v -> v.reason().compareToIgnoreCase(type.reason()) == 0);
	}
	
	@Override
	public void logPendingTasks(Log log) {
		writePendingMapToLog(log, pendingTasks, "All");
	}

	@Override
	public void logPendingTasks(Log log, TaskPendingType type) {
		Multimap<TaskPendingType, Task> pending = getMapOfPendingType(type);
		writePendingMapToLog(log, pending, type.reason());
	}
	
	private void writePendingMapToLog(Log log, Multimap<TaskPendingType, Task> pending, String type) {
		log.logEntry(this, String.format("----------------- %s Pending Tasks-----------------", type));
		for (Task t : pending.values()) 
			log.logEntry(this, t.objectID());
	}
}
