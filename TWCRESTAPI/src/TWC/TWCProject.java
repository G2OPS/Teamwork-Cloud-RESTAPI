package TWC;

import java.util.ArrayList;

/**
 * Class representing a TWCProject object
 * @author Jay J. James JR
 * @since 8/7/2019
 *
 */

public class TWCProject {
	/*
	 * resourceID - ID used to identify a project
	 * trunkID - ID used to cross reference roles
	 * title - Title of a project
	 * description - Project description
	 * branches - List of branches per project
	 * revision - revision object containing all commits for a project
	 * categoryID - ID used to identify project categories
	 */
    private String resourceID;
    private String trunkID;
    private String title;
    private String description;
    private ArrayList<TWCBranch> branches;
    private TWCRevision revision;
    private String categoryID;
    
    /**
     * Setters and getters
     * @return
     */

    public ArrayList<TWCBranch> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<TWCBranch> branches) {
        this.branches = branches;
    }

    public TWCProject() {
        branches = new ArrayList<>();

    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getTrunkID() {
        return trunkID;
    }

    public void setTrunkID(String trunkID) {
        this.trunkID = trunkID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }


    public TWCRevision getRevision() {
        return revision;
    }

    public void setRevision(TWCRevision revisions) {
        this.revision = revisions;
    }

    public void display(){
        System.out.println("Title: " + title);
        System.out.println("Resource ID: " + resourceID);
        System.out.println("Trunk ID: " + trunkID);
        System.out.println("Description: " + description);
        System.out.println("Category ID: " + categoryID);

    }
}
