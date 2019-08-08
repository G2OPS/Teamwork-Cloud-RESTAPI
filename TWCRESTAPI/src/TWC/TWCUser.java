package TWC;

import java.util.ArrayList;

/**
 * Class representing a user from TWC
 * @author Jay J. James Jr
 * @since 8/7/2019
 *
 */
public class TWCUser {
	
	/**
	 * userName - Name assigned to user upon registration
	 * external - boolean value that verifies if a user is with IME or not
	 * realm - Organization user is registering from
	 * lastLoginDate - most recent login for a user
	 * enabled - does the user have access to the server?
	 * usergroups - List of groups assigned to a user
	 * roleAssignments - List of role ID's assigned to a user
	 * phone - user phone number
	 * mobile - user mobile number
	 * department - user department
	 * email - user email
	 * 
	 */

    private String userName;
    private boolean external;
    private String realm;
    private String lastLoginDate;
    private boolean enabled;
    private ArrayList<String> userGroups;
    private ArrayList<String> roleAssignments;
    private boolean resourceDetailsPreferredManagement;
    private String phone;
    private String mobile;
    private String name;
    private String Department;
    private String email;

    public TWCUser() {
        userGroups = new ArrayList<>();
        roleAssignments = new ArrayList<>();

    }
    
    /*
     * Setters and getters
     */

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getLastLoginDate()
    { return this.lastLoginDate; }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(ArrayList<String> userGroups) {
        this.userGroups = userGroups;
    }

    public ArrayList<String> getRoleAssignments() {
        return roleAssignments;
    }

    public void setRoleAssignments(ArrayList<String> roleAssignments) {
        this.roleAssignments = roleAssignments;
    }

    public boolean isExternal() {
        return external;
    }

    public boolean isResourceDetailsPreferredManagement() {
        return resourceDetailsPreferredManagement;
    }

    public void setResourceDetailsPreferredManagement(boolean resourceDetailsPreferredManagement) {
        this.resourceDetailsPreferredManagement = resourceDetailsPreferredManagement;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void dislplay(){
        System.out.println("UserName: " + getUserName());
        System.out.println("External: " + isExternal());
        System.out.println("Last Login Date: " + getLastLoginDate());
        System.out.println("Enabled: " + isEnabled());
        System.out.println("User Groups:");
        for(String user: this.getUserGroups()){
            System.out.println(user);
        }
        System.out.println("Role Assignments:");
        for(String role: getRoleAssignments()){
            System.out.println(role);
        }
        System.out.println("ResourceDetailsPreferredManagement: " + isResourceDetailsPreferredManagement());
        System.out.println("Phone: " + getPhone());
        System.out.println("Mobile: " + getMobile());
        System.out.println("Name: " + getName());
        System.out.println("Department: " + getDepartment());
        System.out.println("Email: " + getEmail());
        System.out.println();
    }


}
