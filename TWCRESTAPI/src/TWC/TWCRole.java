package TWC;

public class TWCRole {

	/**
	 * Name - Name of role
	 * Description - Description of role
	 * ID - used to identify a role
	 * predefined - boolean representing whether or not the
	 * role was made by a user or predefined by TWC
	 * containsGlobalOperationsOnly ?
	 * 
	 */
    private String name;
    private String description;
    private String ID;
    private boolean predefined;
    private boolean containsGlobalOperationsOnly;

    public TWCRole() {

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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public boolean isPredefined() {
        return predefined;
    }

    public void setPredefined(boolean predefined) {
        this.predefined = predefined;
    }

    public boolean isContainsGlobalOperationsOnly() {
        return containsGlobalOperationsOnly;
    }

    public void setContainsGlobalOperationsOnly(boolean containsGlobalOperationsOnly) {
        this.containsGlobalOperationsOnly = containsGlobalOperationsOnly;
    }

    public void display(){
        System.out.println("Name: " + name);
        System.out.println("Description: " + description);
        System.out.println("ID: " + ID);
        System.out.println("Predefined: " + predefined);
        System.out.println("Contains Global Operations Only: " + containsGlobalOperationsOnly);
    }


}
