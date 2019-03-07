package task_manager;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.TaskConsumer;

public class TaskManager {

	private BlockingQueue<TaskConsumer> taskList = new ArrayBlockingQueue<>(10);
	private boolean processorRunning = false;
	
	ScheduledExecutorService scheduledExecutorProcess = Executors.newScheduledThreadPool(2);
	
	private ProcessTask taskProcessor = new ProcessTask(taskList);
	private AddTask addTask = new AddTask();
	
	private TaskManager() {}
	
	private void checkProcessor() {
		System.out.println("Checking processor");
		if(!processorRunning) {
			System.out.println("Started processor");
			processorRunning = true;
//			new Thread(taskProcessor).start();			
			scheduledExecutorProcess.schedule(taskProcessor, 1, TimeUnit.SECONDS);
		}
	}
	
	public void giveTask(TaskConsumer aTask) {
		
		System.out.println("Task manager received task");
		checkProcessor();
		try {
			taskList.put(aTask);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static class AddTask implements Runnable {

		@Override
		public void run() {		
		}
	}
	
	private static class ProcessTask implements Runnable {

		BlockingQueue<TaskConsumer> taskList;
		
		public ProcessTask(BlockingQueue<TaskConsumer> taskList) {
			super();
			this.taskList = taskList;
		}

		@Override
		public void run() {
			TaskConsumer task;
//			while(!taskList.isEmpty()) {
//			while(true) {
			if(!taskList.isEmpty()) {
				try {
					task = taskList.take();
					task.getTask().getDDDD().giveTask(task);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static class TaskManagerHelper {
		private static final TaskManager instance = new TaskManager();
	}
	
	public static TaskManager getInstance() {
		return TaskManagerHelper.instance;
	}
}
