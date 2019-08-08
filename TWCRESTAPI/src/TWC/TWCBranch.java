package TWC;

import java.util.ArrayList;

/***
 * Class representing a branch in Teamwork Cloud
 */
public class TWCBranch {

    // Arraylist of project ID's to cross reference if needed
    private ArrayList<String> projects;
    // String identifier for a branch
    private String id;

    // Constructor for a TWCBranch
    public TWCBranch(){
        // List needs to be initialized before use
        projects = new ArrayList<>();
    }

    /***
     * @return An array list of project ID's to cross reference if needed
     */
    public ArrayList<String> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<String> projects) {
        this.projects = projects;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
