/**
 * 
 */
package task_list;

/**
 * @author Steve Brown
 *
 */
public enum TaskPendingType {
	NO_MANAGER ("No Department Manager"), 
	NO_EMPLOYEE ("No Employee Available"), 
	DUPLICATE ("Duplicate"), 
	WRONG_DEPT ("Wrong Department"),
	FAILED ("Failed");
	
	private String reason;
	
	TaskPendingType(String reason) {
		this.reason = reason;
	}
	
	public String reason() {
		return reason;
	}
}
