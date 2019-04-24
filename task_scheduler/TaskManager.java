/**
 * 
 */
package task_scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import heartbeat.Beatable;
import heartbeat.BeatingHeart;
import observer.GenericSubject;
import observer.Observer;
import observer.ObserverMessage;
import observer.Subject;
import task_list.ExecutableTaskHolder;
import task_list.ExecutableTaskList;
import task_list.ScheduledTaskHolder;
import task_list.ScheduledTaskList;
import task_strategy.TaskAllocator;
import tasks.task_super_objects.ScheduledTask;
import tasks.task_super_objects.Task;
import timer.Timers;
import utils.Log;

/**
 * @author Steve Brown
 *
 *  Used to schedule and execute tasks.
 *  
 *  A task is presented using manageTask(TaskConsumer).
 *  If it is a non-scheduled task it is executed immediately.
 *  If it is a scheduled task it is added to the queue so that it can be executed at the correct time.
 */

public class TaskManager implements Beatable, Observer, Manager {
	
	private Timers timer = null;				// Application Timer.
	private BeatingHeart beatingHeart = null;	// HeartBeat of the scheduler.
												// Executor for non scheduled tasks.
	private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(20);
												// Running tasks are stored as observers. 
	private Subject runningTasks = new GenericSubject("TaskManager");
	private boolean shuttingDown = false;		// Is the scheduler shutting down. 
	private boolean alreadyCheckingSchedule = false;	// Is the scheduler checking the queue.
	private Log log;

	private TaskAllocator taskAllocator = new TaskAllocator();
	private ScheduledTaskList scheduledTasks = new ScheduledTaskHolder();
	private ExecutableTaskList executableTasks = new ExecutableTaskHolder();
	
	// GIVE THIS A DealerDAO!!!! ??????????
	
	public TaskManager(Timers timer, BeatingHeart beatingHeart, Log log) {
		this.timer = timer;
		this.beatingHeart = beatingHeart;
		this.log = log;
		
		this.taskAllocator.setSchedulableTasks(scheduledTasks);
		this.taskAllocator.setExecutableTasks(executableTasks);
				
		checkHeartbeat();
	}
			
	/*
	 *  Start the heart beat if not already running.
	 *  Register us as an observer.
	 */
	private void checkHeartbeat() {
		if(!beatingHeart.isBeating() && !shuttingDown) 
			beatingHeart.startBeating(this, this);
	}
		
	/*
	 * 	Does the scheduler's work every beat if:
	 *  	a) scheduler is not shutting down, and 
	 *  	b) the timer is running.
	 */
	@Override
	public void beat() {
		incrementHeartBeat();
		if(notShuttingDown()) {		
			executeAtomicTasks(); 
			if(!alreadyCheckingSchedule) 	
				checkSchedule();
		}
	}
	
	private void incrementHeartBeat() {
		beatingHeart.anotherBeat();	
	}
	
	private boolean notShuttingDown() {
		return (!shuttingDown && timerRunning());
	}
	
	private boolean timerRunning() {
		return timer.timerRunning();
	}
			
	private void executeAtomicTasks() {
		if(executableTasks.isNotEmpty()) //While????
			new Thread(executableTasks.getAndRemoveNextTask()).run();
	}
		
	private void checkSchedule() {	
		alreadyCheckingSchedule = true;	
		if(scheduledTasks.isNotEmpty() && !shuttingDown) //while
			checkTasksStartTime();
		alreadyCheckingSchedule = false;
	}

	private void checkTasksStartTime() {
		ScheduledTask nextTask = (ScheduledTask) scheduledTasks.peekAtNextTask();
		int startTime = nextTask.getStartTime();
		
		if(startTime == timer.currentTime()) 
			executeScheduledTask(scheduledTasks.getAndRemoveNextTask());
	}

	private void executeScheduledTask(Task t) {
		t.executeTask();
	}
	
	/*
	 *  Called if this object's heart beat is stopped, i.e. we're shutting down.
	 */
	@Override
	public void updateObserver(ObserverMessage msg) {
		// Get message from HO. Only interested in a shutdown message.
		if(msg == ObserverMessage.STOPPING && shuttingDown == false) {
			pause(500); // Pause to see if any shutdown tasks are added. TODO - Remove?
			shutDownTaskManager(1);
		}
	}
	
	/*
	 *  Make this object sleep for the specified milliseconds.
	 *  Allows tasks to still be managed even if we're shutting down.
	 */
	private void pause(long p) {
		try {
			Thread.sleep(p);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 	Notify observers that we're shutting down so that they
	 *  can stop executing their tasks.
	 */
	private void stopRunningTasks() {
		runningTasks.notifyObservers(ObserverMessage.STOPPING);
	}
	
	/*
	 *  Shut down the executor service and heartbeat.
	 *  If there are already scheduled tasks there will be 
	 *  an attempt to execute them before we're shut down.   
	 */
	private void shutDownTaskManager(int attemptedShutdown) {
		shuttingDown = true;
		stopRunningTasks();						// Stop any tasks already running.
		
		if(!scheduledTasks.isNotEmpty() || attemptedShutdown > 50) {
			// System.out.println(attemptToShutdown + " attempt to shut down the TaskManager."); // TODO - R/Log
			beatingHeart.stopBeating();
	
			taskExecutor.shutdown();			
			try {								// TODO - Configure time period
				if(!taskExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
					taskExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				System.out.println("Error shutting down TS"); // TODO - Log
				taskExecutor.shutdownNow();	
			}
		}else {
			pause(250);							// Wait and then try again.
			shutDownTaskManager(++attemptedShutdown);
		}
	}

	public <T extends Task> void giveTask(T t) {
		t.accept(taskAllocator);
	}
}
	
	/*
	 *  Register a running task as an observer so that we can stop 
	 *  it if the scheduler closes down.
	 */
//	private void addRunningTask(ScheduledExecutor task) {
//		runningTasks.registerObserver(task);
//	}
	
	/*
	 *  If a task has a scheduled start time put it on the scheduledTasks queue.
	 *  It will stay there until it's scheduled start time arrives.
	 *  
	 *  TODO - This is a FIFO queue and the task(s) with the earliest start
	 *  time should be at the head of the queue. That is not guaranteed at present.
	 */
//	private void putScheduledTask(ScheduledTask task) {
//		int startTime = task.getTasksSchedule().scheduledStartTime();
//		
//		if(startTime > 0 && !shuttingDown) { // TODO - Add latest possible start time.
//			try {
//				scheduledTasks.put(new ScheduledExecutor(task));
//			} catch (InterruptedException e) {
//				e.printStackTrace();	// TODO - Log
//			}
//		}
//	}

//	private void printAtomicList() {
//	System.out.println("\nPRINTNG ATOMIC LIST");
//	List<Task> tasks = executableTasks.getTaskList();
//	for (Task task : tasks) 
//		System.out.println(task.getTasksDetails().getMsg());
//}

//private void printScheduledList() {
//	System.out.println("\nPRINTNG SCHEDULED LIST");
//	List<Task> tasks = scheduledTasks.getTaskList();
//	for (Task task : tasks) 
//		System.out.println(task.getTasksDetails().getMsg());
//}	
	

	/*
	 *  This task will run continuously for the life time of the app
	 *  or until TaskScheduler is told to shut down. 
	 */
//	private void runRepeatTask(TaskConsumer_OLD task) {
//		taskExecutor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);
//	}
