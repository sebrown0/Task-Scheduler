/**
 * 
 */
package task_scheduler;

import heartbeat.Beatable;
import heartbeat.BeatingHeart;
import heartbeat.SlowHeartbeat;
import observer.Observer;
import observer.ObserverMessage;
import tasks.TaskConsumer;
import tasks.TaskRunner;
import tasks.TaskSchedule;

/**
 * @author Steve Brown
 *  
 *  Executes (starts) the task assigned to it.
 *  If we receive an updateObserver message to stop then the task's heart beat is stopped.
 */
public class TaskExecutor implements  Observer, Beatable { 

	private TaskRunner task;						// The task to execute.
	private TaskSchedule taskSchedule; 				// The task's schedule.
	private BeatingHeart taskBeat;					// The regularity of the task's execution.  
	
	/*
	 *  New TaskExecutor with task. Get it's schedule and assign to class level schedule.
	 */
	public TaskExecutor(TaskConsumer task) {
		this.task = task.getTask();
		this.taskSchedule = task.getTask().taskSchedule();		
	}

	/* 
	 *  Return the tasks start time in seconds.
	 */
	public int taskStartTime() {
		return taskSchedule.scheduledStartTime();
	}
	
	/*
	 *  Start this TaskExecutor's Heart, which will cause the task to be executed every HB.
	 */
	public void startTask() {
		// System.out.println("TaskExecutor starting task for duration -> " + taskSchedule.scheduledDuration());	// TODO - Log/R
		taskBeat = new SlowHeartbeat("TExec");		// Default of a SlowHeartbeat until code updated.
		taskBeat.startBeating(this, taskSchedule.scheduledDuration(), this);
	}
	
	/*
	 *  Stop executing the task.
	 */
	public void stopTask() {
		taskBeat.stopBeating();
	}
	
	/*
	 *  HeadOffice has sent us a shut down message.
	 */
	@Override
	public void updateObserver(ObserverMessage msg) {
		if(msg == ObserverMessage.STOPPING) 
			stopTask();
	}

	/*
	 *  Execute the task while the heart is beating.
	 */
	@Override
	public void beat() {
		taskBeat.anotherBeat();
		task.executeTask();
	}
}
