/**
 * 
 */
package task_scheduler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import heartbeat.Beatable;
import heartbeat.BeatingHeart;
import observer.GenericSubject;
import observer.Observer;
import observer.ObserverMessage;
import observer.Subject;
import tasks.TaskConsumer;
import tasks.TypeOfTask;
import timer.Timers;

/**
 * @author Steve Brown
 *
 *  Used to schedule and execute tasks.
 *  It is a Singleton object as we only ever want one instance of this per app.
 *  
 *  A task is presented using manageTask(TaskConsumer).
 *  If it is a non-scheduled task it is executed immediately.
 *  If it is a scheduled task it is added to the queue so that it can be executed at the correct time.
 */

public class TaskManager implements Beatable, Observer {
	
	private long currentTime = 0;				// The current time in seconds.
												// A queue containg any scheduled tasks.
	private BlockingQueue<TaskExecutor> scheduledTasks = new LinkedBlockingDeque<>(20);
	private Timers timer = null;				// Application Timer.
	private BeatingHeart beatingHeart = null;	// HeartBeat of the scheduler.
												// Executor for non scheduled tasks.
	private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(20);
												// Running tasks are stored as observers. 
	private Subject runningTasks = new GenericSubject("TaskScheduler");
	private boolean shuttingDown = false;		// Is the scheduler shutting down. 
	private boolean checkingSchedule = false;	// Is the scheduler checking the queue.

	/*
	 *	Singleton so cannot be instantiated. 
	 */
	private TaskManager() {}
		
	/*
	 *  TaskSchedulerHelper can call this to set this object's Timer.
	 */
	private void setTimer(Timers timer) {
		this.timer = timer;	
	}
	
	/*
	 *  TaskSchedulerHelper can call this to set this object's BeatingHeart.
	 */
	private void setBeatingHeart(BeatingHeart beatingHeart) {
		this.beatingHeart = beatingHeart;
	}
			
	/*
	 *  This task has no schedule so run it immediately.
	 */
	private void runAtomicTask(TaskConsumer task) {
		new Thread(task).run();
	}
	
	/*
	 *  This task will run continuously for the life time of the app
	 *  or until TaskScheduler is told to shut down. 
	 */
	private void runRepeatTask(TaskConsumer task) {
		taskExecutor.scheduleWithFixedDelay(task, 1, 1, TimeUnit.SECONDS);
	}
	
	/*
	 *  If a task has a scheduled start time put it on the scheduledTasks queue.
	 *  It will stay there until it's scheduled start time arrives.
	 *  
	 *  TODO - This is a FIFO queue and the task(s) with the earliest start
	 *  time should be at the head of the queue. That is not guaranteed at present.
	 */
	private void putScheduledTask(TaskConsumer task) {
		if(task.getTask().taskSchedule().scheduledStartTime() > 0) { // TODO - Add latest possible start time.
			try {
				scheduledTasks.put(new TaskExecutor(task));
			} catch (InterruptedException e) {
				e.printStackTrace();	// TODO - Log
			}
		}
	}
	
	/*
	 *  Register a running task as an observer so that we can stop 
	 *  it if the scheduler closes down.
	 */
	private void addRunningTask(TaskExecutor task) {
		runningTasks.registerObserver(task);
	}
	
	/*
	 *  Start the heart beat if not already running.
	 *  Register us as an observer.
	 */
	private void checkHeartbeat() {
		if(!beatingHeart.isBeating()) 
			beatingHeart.startBeating(this, this);
	}
	
	/*
	 *  Given a task. If it's a scheduled task then add it to the queue.
	 *  If it's non scheduled task run it straight away.  
	 */
	public void manageTask(TaskConsumer task) {
		TypeOfTask typeOfTask = task.getTask().typeOfTask();
			
		checkHeartbeat();			// Start the HB on demand.
					
		switch (typeOfTask) {
		case ATOMIC:
			runAtomicTask(task);
			break;
			
		case REPEAT:
			runRepeatTask(task);
			break;
			
		case REPEATABLE:
			putScheduledTask(task);
			break;
			
		case SCHEDULED:
			putScheduledTask(task);
			break;
			
		case SCHEDULED_REPEATABLE:
			putScheduledTask(task);
			break;

		default:
			System.out.println("No task Scheduled.");  	// TODO - Log
			break;
		}
	//		System.out.println("Scheduled a task for " + task.getTask().tasksScheduledTime().formattedTime()); // TODO - Log
	}
	
	/*
	 *  Shut down the executor service and heartbeat.
	 *  
	 *  TODO - Do something with any outstanding scheduled task in the queue.
	 */
	private void shutDownScheduler() {
		// System.out.println("Told to stop TS HB"); 	// TODO - Log
		
		shuttingDown = true;
		taskExecutor.shutdown();			// Stop any non-scheduled repeating tasks.
		stopRunningTasks();					// Stop any scheduled, running tasks.
		beatingHeart.stopBeating();

		try {								// TODO - Configure time period
			if(!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
				taskExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			System.out.println("Error shutting down TS"); // TODO - Log
			taskExecutor.shutdownNow();	
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
	 *  Keeps checking the schedule queue to see if any tasks should be executed for the current time.
	 */
	private void checkSchedule() {

		TaskExecutor scheduledTask;		// Task at the head of the queue.
		boolean stillChecking = true;	// Method level flag.
		checkingSchedule = true;		// Set the class level flag so that we're not called again by beat() while we're still checking.
	
		// Keep checking the queue while there's a possibility of there being a task to execute at this time.
		while(stillChecking && ((scheduledTask = scheduledTasks.peek()) != null)) {					
			try {
				if(scheduledTask.taskStartTime() == currentTime) {
					scheduledTask = scheduledTasks.take();
					scheduledTask.startTask();
					addRunningTask(scheduledTask);	// Add the task to the running list.
				}else {
					stillChecking = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace(); 			// TODO - Log
			}
		}
		checkingSchedule = false;
	}
	
	/*
	 * 	Does the scheduler's work every beat if:
	 *  	a) scheduler is not shutting down, and 
	 *  	b) the timer is running.
	 */
	@Override
	public void beat() {

		beatingHeart.anotherBeat();					// Increment the beat count.
		currentTime = timer.currentTime();			// Get the current time for this object.
		
		if(!shuttingDown && timer.timerRunning()) {	// Do work in here..
			// Keep this 'if statement' separate in case we do other work in here. 
			if(!checkingSchedule) {		
				checkSchedule();
			}
		}
//		System.out.println("Tasks scheduled time = " + taskScheduledTime + "   Scheduler's time -> " + time.currentTime()); // TODO - Log/R
	}
	
	/*
	 *  Called if this object's heart beat is stopped.
	 */
	@Override
	public void updateObserver(ObserverMessage msg) {
		// Get message from HO. Only interested in a shutdown message.
		if(msg == ObserverMessage.STOPPING && shuttingDown == false) {
			shutDownScheduler();
			// System.out.println("Observer Message (" + msg + ")"); // TODO - R/Log
		}
	}
	
	/*
	 *  Helper to get a single instance of a TaskScheduler.
	 *  
	 *   Inputs:
	 *   	1. Timers: the applications overall timer.
	 *   	2. BeatingHeart: the heart beat for the TaskScheduler.   
	 */
	public static class  TaskSchedulerHelper{
		private static final TaskManager INSTANCE = new TaskManager();
		
		/*
		 *  Return the instance of TaskScheduler with its Timer and HeartBeat set.
		 */
		public static TaskManager instanceOfTaskScheduler(Timers timer, BeatingHeart beatingHeart) {
			INSTANCE.setTimer(timer);
			INSTANCE.setBeatingHeart(beatingHeart);
			return INSTANCE;
		}
	}
}
	