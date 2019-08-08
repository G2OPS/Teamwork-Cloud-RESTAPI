package TWC;

import java.util.ArrayList;
/*
 * Class representing a User Group for TWC
 * 
 */
public class TWCUserGroup {
	/**
	 * name - Name of user group
	 * description - Description of user group
	 * realm - Denotes which organization the User Group is a part of
	 * userName - List of user currently part of this group.
	 * ID - Identifier for a user group
	 * roleAssignments - List of roles assigned to this group.
	 */
    private String name;
    private String description;
    private String realm;
    private ArrayList<String> userNames;
    private String ID;
    private ArrayList<String> roleAssignments;

    public TWCUserGroup(){
        userNames = new ArrayList<>();
        roleAssignments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public ArrayList<String> getRoleAssignments() {
        return roleAssignments;
    }

    public void setRoleAssignments(ArrayList<String> roleAssignments) {
        this.roleAssignments = roleAssignments;
    }
}
