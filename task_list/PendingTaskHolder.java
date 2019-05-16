package task_list;

import java.util.function.BiConsumer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 *  Holds the tasks that the department manager has not been able to assign.
 *  
 */
public class PendingTaskHolder implements PendingTaskList{

	Multimap<TaskPending, Task> pendingTasks = ArrayListMultimap.create();

	@FunctionalInterface
	public interface LogPendingTasks{
		void logPendingTasks(TaskPending p, Task t);
	}
	
	@Override
	public void addTask(TaskPending reason, Task t) {
		pendingTasks.put(reason, t);
	}

	@Override
	public void logPendingTasks() {
		System.out.println("--------------------- LOGGING PENDING TASKS ---------------------");
		BiConsumer<TaskPending, Task> logPending = (p,t) -> {
			System.out.println(p);
			System.out.println(t.objectID());
//			for (PendingType pending : pendingTasks.asMap().keySet()) {
//				
//			}
		};
		
//		pendingTasks.forEach(logPending);
	}
}
