package TWC;

import java.util.ArrayList;

import org.json.JSONObject;
/**
 * Class representing a Workspace in TWC
 * @author jayjj
 *
 */
public class TWCWorkspace {
	/**
	 * workspaceID - Identifier for a given workspace
	 * projects - List of project IDs
	 * description - Description of a workspace
	 * title - Workspace title
	 */
    private String workspaceID;
    private ArrayList<String> projects;
    private String description;
    private String title;

    
    /**
     * Setters and Getters
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TWCWorkspace(String workspaceID){
        this.workspaceID = workspaceID;
        this.projects = new ArrayList<>();

    }

    public String getWorkspaceID() {
        return workspaceID;
    }

    public void setWorkspaceID(String workspaceID) {
        this.workspaceID = workspaceID;
    }


    public ArrayList<String> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<String> projects) {
        this.projects = projects;
    }

    public void display(){
        System.out.println("Workspace Title: " + getTitle());
        System.out.println("Workspace ID: " + getWorkspaceID());
        System.out.println("Projects: ");
        for(String projectID: this.getProjects()){
            System.out.println(projectID);
        }
    }


}
