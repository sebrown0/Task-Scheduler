package task_list;

import java.util.HashMap;
import java.util.Map;

import people.employees.Employee;
import tasks.task_super_objects.Task;

/**
 * 
 * @author Steve Brown
 *
 *  Holds the tasks that the department manager has assigned to a department employee.
 *  When the employee reports back to the manager that they have completed the task 
 *  it is removed from the map.
 *  
 *  Use a hash map so that the employee can only have one task at a time.
 */
public class AssignedTaskHolder implements ManagementTaskList{

	Map<Employee, Task> assignedTasks = new HashMap<>();
	
	@Override
	public boolean alreadyAssignedTask(Task task) {
		return assignedTasks.containsValue(task);
	}

	@Override
	public void removeAssignedTask(Employee e) {
		assignedTasks.remove(e);
	}

	@Override
	public void addTask(Employee e, Task t) {
		assignedTasks.put(e, t);
	}
}
