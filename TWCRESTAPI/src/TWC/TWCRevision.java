package TWC;

public class TWCRevision {
	
	/**
	 * revision - Array of integers that dynamically resized after each commit
	 * value found at first index of the revision array is the most recent number of
	 * commits
	 * 
	 * commitType - Type of commit made
	 * branchID - ID used to find a branch
	 * resourceID - ID representing this revision object
	 * base - The base URL called to populate this object
	 * author - userName of the TWCUser who made this revision
	 * type - type of revision
	 * description - description of revision
	 * 
	 */
    private int[] revision;
    private String commitType;
    private String branchID;
    private String resourceID;
    private String base;
    private String author;
    private String[] type;
    private int pickedRevision;
    private String description;
    private String context;
    private int directParent;
    private String[] dependencies;
    private String[] rootObjectIDs;
    private String createdDate;
    private String ID;
    private String artifacts;

    public TWCRevision(){
        revision = new int[0];
        dependencies = new String[0];
        rootObjectIDs = new String[0];
    }
    
    /**
     * Setters and Getters
     * @return
     */

    public int[] getRevision() {
        return revision;
    }

    public void setRevision(int[] revisions) {
        this.revision = revisions;
    }

    public String getCommitType() {
        return commitType;
    }

    public void setCommitType(String commitType) {
        this.commitType = commitType;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String[] getType() {
        return type;
    }

    public void setType(String[] type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getDirectParent() {
        return directParent;
    }

    public void setDirectParent(int directParent) {
        this.directParent = directParent;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(String[] dependencies) {
        this.dependencies = dependencies;
    }

    public String[] getRootObjectIDs() {
        return rootObjectIDs;
    }

    public void setRootObjectIDs(String[] rootObjectIDs) {
        this.rootObjectIDs = rootObjectIDs;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(String artifacts) {
        this.artifacts = artifacts;
    }

    public int getPickedRevision() {
        return pickedRevision;
    }

    public void setPickedRevision(int pickedRevision) {
        this.pickedRevision = pickedRevision;
    }
}
