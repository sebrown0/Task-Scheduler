/**
 * 
 */
package task_list;

/**
 * @author Steve Brown
 *
 */
public enum TaskPending {
	NO_MANAGER ("no manager"), 
	NO_EMPLOYEE ("no employee"), 
	DUPLICATE ("duplicate task"), 
	WRONG_DEPT ("wrong department");
	
	private String reason;
	
	TaskPending(String reason) {
		this.reason = reason;
	}
	
	public String reason() {
		return reason;
	}
}
