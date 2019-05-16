/**
 * 
 */
package task_scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
import tasks.task_super_objects.AtomicTask;
import tasks.task_super_objects.ManagementTask;
import tasks.task_super_objects.ScheduledTask;
import tasks.task_super_objects.Task;
import timer.Timer;
import utils.logger.Log;
import utils.logger.Loggable;

/**
 * @author Steve Brown
 *
 *  Used to schedule and execute tasks.
 *  
 *  A task is presented using manageTask(TaskConsumer).
 *  If it is a non-scheduled task it is executed immediately.
 *  If it is a scheduled task it is added to the queue so that it can be executed at the correct time.
 */

public class TaskManager implements Beatable, Observer, Manager, Loggable {
	
	private Timer appTimer = null;				// Application Timer.
	private BeatingHeart beatingHeart = null;	// HeartBeat of the scheduler.
												// Executor for non scheduled tasks.
	private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(20);
												// Running tasks are stored as observers. 
	private Subject runningTasks = new GenericSubject("TaskManager");
	private boolean shuttingDown = false;		// Is the scheduler shutting down. 
	private TaskAllocator taskAllocator = new TaskAllocator();
	private ScheduledTaskList scheduledTasks = new ScheduledTaskHolder();
	private ExecutableTaskList executableTasks = new ExecutableTaskHolder();
	private AtomicBoolean alreadyRunningExecutableTasks = new AtomicBoolean(false);
	private AtomicBoolean alreadyCheckingSchedule =  new AtomicBoolean(false);
	private Log log;
	
	public TaskManager(Timer timer, BeatingHeart beatingHeart, Log log) {
		this.appTimer = timer;
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
		if(notShuttingDown() && notAlreadyExecutingAtomicTasks()) {		
			checkAtomicTasks(); 
			if(!alreadyCheckingSchedule.get()) 	
				checkSchedule();
		}
	}
	
	private void incrementHeartBeat() {
		beatingHeart.anotherBeat();	
	}
	
	private boolean notShuttingDown() {
		return (!shuttingDown && timerRunning());
	}
	
	private boolean notAlreadyExecutingAtomicTasks() {
		return !alreadyRunningExecutableTasks.get();
	}
	
	private boolean timerRunning() {
		return appTimer.timerRunning();
	}
			
	private void checkAtomicTasks() {
		alreadyRunningExecutableTasks.set(true);
		while(executableTasks.isNotEmpty()) 
			executeAtomicTasks();	
		alreadyRunningExecutableTasks.set(false);
	}
	
	private void executeAtomicTasks() {
		Task task = executableTasks.getAndRemoveNextTask();
		if(task instanceof ManagementTask) {
			task.getTasksDepartment().getDeptManager().performTask(task);
		}else if(task instanceof AtomicTask){
			if(task.getTasksDepartment().deptHasManager()) {
				task.getTasksDepartment().assignTaskToDeptManager(task);
			}else {
				// DO SOMETHING WITH THE TASK
			}
		}else {
			// DO SOMETHING WITH THE TASK
		}
	}

	private void checkSchedule() {	
		alreadyCheckingSchedule.set(true);
		if(scheduledTasks.isNotEmpty() && !shuttingDown) //while
			checkTasksStartTime();
		alreadyCheckingSchedule.set(false);
	}

	private void checkTasksStartTime() {
		ScheduledTask nextTask = (ScheduledTask) scheduledTasks.peekAtNextTask();
		int scheduledStartTime = nextTask.getStartTime();
		
		if(scheduledStartTime == now()) 
			giveTaskToDeptManager(scheduledTasks.getAndRemoveNextTask());
	}

	private int now() {
		return appTimer.currentTime();
	}
	
	private void giveTaskToDeptManager(Task task) {
		if(task.getTasksDepartment().deptHasManager()) 
			task.getTasksDepartment().assignTaskToDeptManager(task);
		else
			log.logEntry(this, "No department manager available to receive task: " + task.objectID() );		
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
			beatingHeart.stopBeating();
			taskExecutor.shutdown();			
			try {								// TODO - Configure time period
				if(!taskExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
					taskExecutor.shutdownNow();
				}
			} catch (InterruptedException e) {
				log.logEntry(this, "Error shutting down Task Manager");
				taskExecutor.shutdownNow();	
			}
		}else {
			pause(250);							// Wait and then try again.
			shutDownTaskManager(++attemptedShutdown);
		}
	}

	public <T extends Task> void giveTask(T t) {
		log.logEntry(this, "TM Accepting task ->>>>>>>>>> " + t.objectID());
		t.accept(taskAllocator);
	}
}
