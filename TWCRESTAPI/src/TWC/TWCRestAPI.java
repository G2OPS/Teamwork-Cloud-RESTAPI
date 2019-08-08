package TWC;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 *  @author Jay J. James
 *  @since 8/7/2019
 *  This class will be used to pull and parse JSON objects from The TeamWork Cloud RESTAPI
 *  
 *  DISCLAIMER: Any user can login to make API calls, but to update any user information, 
 *  that user must be assigned administrator privileges. Also, there is a method called at the
 *  start of each method that bypasses any security certificate issues. You may not need it on 
 *  the actual server but it was necessary for the testing server.
 */

public class TWCRestAPI {


    private ArrayList<TWCWorkspace> TWCWorkspaces; // Stores all workspace objects
    private ArrayList<TWCUser> TWCUsers; // Stores all user objects
    private ArrayList<TWCRole> TWCRoles; // Stores all role objects
    private ArrayList<TWCProject> TWCProjects; // Stores all project objects
    private ArrayList<TWCRevision> TWCRevisions; // Stores all revisions/commits from projects
    private ArrayList<TWCUserGroup> TWCUserGroups; // Stores all user group objects
    /*
     * The base url will need to be changed depending on your IP Address
     */
    private static final String baseUrl = "https://192.168.56.101:8111/osmc/"; //
    private HttpsURLConnection con;
    private URL url;
    private BufferedReader reader;
    private StringBuffer responseContent;
    private String line;
    private int responseCode;
    private Scanner scan;


    /***
     * Initialize all the lists in the constructor
     */
    public TWCRestAPI(){
        this.TWCWorkspaces = new ArrayList<>();
        this.TWCUsers = new ArrayList<>();
        this.TWCRoles = new ArrayList<>();
        this.TWCProjects = new ArrayList<>();
        this.TWCRevisions = new ArrayList<>();
        this.TWCUserGroups = new ArrayList<>();
        this.responseContent = new StringBuffer();
        this.scan = new Scanner(System.in);
    }

    /***
     * 
     * @throws Exception
     * Populates our TWCRest API with data from all of our RESTAPI calls
     * 
     */
    public void init() throws Exception{
        initWorkspaces();
        initUsers();
        initProjects();
        initRoles();
        initBranches();
        initRevisions();
        initUserGroups();
    }
    
    /***
     * 
     * This method will populate our list of workspaces with the parse
     * JSON response data used in our API call
     */
    public void initWorkspaces(){
    	
    	/***
    	 * Before establishing a connection to the server, we need to bypass
    	 * the test server certificate. You may not need to do call this on the actual server.
    	 * See the bypass certificate method below
    	 */
    	
    	try {
    		bypassCertificate();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        

        // Getting the workspace ID's so that we can get more detailed information about them
        try{
        	
        	/***
        	 * The Swagger rest API calls require user authentication to be able to 
        	 * access the JSON response. This is done for each API call by setting 
        	 * a default authenticator. See the TWC Authenticator class below.
        	 */
        	
            Authenticator.setDefault(new TWCAuthenticator());
            // Create our instance of the url we need to access
            url = new URL(baseUrl + "workspaces");
            // Establish a connection with that url
            con = (HttpsURLConnection) url.openConnection();
            // Each call needs to specify a request method so the server knows how to handle the JSON response
            con.setRequestMethod("GET");
            // Each call should specify how long we should wait to connect to the server
            con.setConnectTimeout(60000);
            // Specify how long you want to wait to read the JSON response from the server
            con.setReadTimeout(60000);
            // Specify the format in which the JSON response will be return
            con.addRequestProperty("Content-Type","application/json");

            // This will return a response code that will tell us whether or not the response was accepted
            responseCode = con.getResponseCode();
            System.out.println(responseCode);
            // If our request code indicates an error read the error message into the error stream
            if(responseCode > 299){
            		
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();

            // Else we get the normal input stream and begin to parse the response content
            } else{
            	//
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }reader.close();

            }
            /**
             * The JSON response will either be returned as JSONObject or JSONArray
             * To parse efficiently, remember that data enclosed in {} = object and [] = array
             * 
             * This call is going to return all workspace ID's which are contained in
             * the "ldp:contains" index of our JSON object and stores the ID's in a JSON
             * array
             */
            JSONObject TWCWorkspaces = new JSONObject(responseContent.toString());
            JSONArray ldpContains = TWCWorkspaces.getJSONArray("ldp:contains");
            
             /**
              * Populate the workspace with workspace objects containing their ID's to be used 
              * In the next call below that uses those same usernames to display detailed information
              * about the workspace that will be stored in each object.
              */
            for(int i = 0; i < ldpContains.length(); i++){
                TWCWorkspace workspace = new TWCWorkspace(ldpContains.getJSONObject(i).get("@id").toString());
                this.TWCWorkspaces.add(workspace);
            }
            
            /***
             * Populating the TWCWorkspace objects with their detailed information to
             * be cross referenced by other classes.
             * The StringBuffer object needs to be re-initialized in every method so that data stored
             * from a previous API call is not added to the data in the new call which will cause
             * errors when trying to parse the data into a JSONObject.
             */
            
            responseContent = new StringBuffer();
            for(TWCWorkspace workspace: this.TWCWorkspaces){
                url = new URL(baseUrl + "workspaces/" + workspace.getWorkspaceID());
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(60000);
                con.setReadTimeout(60000);
                con.addRequestProperty("Content-Type","application/json");

                responseCode = con.getResponseCode();
                //System.out.println(responseCode);
                if(responseCode > 299){
                    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }


                } else{
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }

                }

                
                reader.close();
                /**
                 * JSON array containing detailed information about a workspace
                 */
                JSONArray workspaceInfo = new JSONArray(responseContent.toString());
                
                /**
                 * Each object stored in the JSON array is a JSONObject
                 * The information we need is stored in the JSONOjbect at the second index of
                 * our JSONArray or workspaceInfo.getJSONOjbect(1)
                 * A JSONObject can access values using keys so to parse values
                 * from the JSONObject we just need to pass in the key we want to access
                 * Depending on the type of data you need, there is a different method you can
                 * call etc.. getString, getInt, getBoolean, ..
                 */
                JSONObject data = workspaceInfo.getJSONObject(1);
                workspace.setDescription(data.getString("dcterms:description"));
                workspace.setTitle(data.getString("dcterms:title"));
                JSONArray kerml = data.getJSONArray("kerml:resources");
                for(int i = 0; i < kerml.length(); i++){
                    workspace.getProjects().add(kerml.getJSONObject(i).getString("@id"));
                }

            }
           

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch(JSONException e) {
        	e.printStackTrace();
        }


    }


    /***
     * 
     * This method will pull and parse user JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */
    public void initUsers(){
    	try {
    		bypassCertificate();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        // Reset our StringBuffer to flush any lingering data.
        responseContent = new StringBuffer();
        Authenticator.setDefault(new test.TWCAuthenticator());
        try{
            url = new URL(baseUrl+"admin/users");

            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.addRequestProperty("Content-Type","application/json");


            int responseCode = con.getResponseCode();
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();

            } else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
            }

            JSONArray TWCUsers = new JSONArray(responseContent.toString());
            for(int i = 0; i < TWCUsers.length(); i++){
                responseContent = new StringBuffer();
                String userName = TWCUsers.getString(i);
                // If you accidentally put a space in a user name you'll have to do this.
//                if(userName.equalsIgnoreCase("Jay James") || userName.equalsIgnoreCase("Jay.James")){
//                    continue;
//                }
                url = new URL(baseUrl +"admin/users/" + userName);
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
                con.addRequestProperty("Content-Type","application/json");
                responseCode = con.getResponseCode();

                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                

                JSONObject TWCUserInfo = new JSONObject(responseContent.toString());
                /**
                 * Unlike the workspaces, users do not contain the same keys because they vary
                 * depending on whether the user is internal or external. Before setting the data
                 * in a user object, we need to see if it even exists on the server. We do this by
                 * checking if the keys are stored in the JSON response
                 */
                ArrayList<String> keys  = new ArrayList<>();
                JSONArray names = TWCUserInfo.names();
                for(int num = 0; num < names.length(); num++){
                    keys.add(names.getString(num));
                }

                /**
                 * Again, we check if the user has a certain key in their JSON response
                 * if they do, set that data in the user object
                 */
                TWCUser user = new TWCUser();
                if(TWCUserInfo.get("userGroups") != null){
                    JSONArray userGroups = TWCUserInfo.getJSONArray("userGroups");
                    for(int j = 0; j < userGroups.length(); j++){
                        user.getUserGroups().add(userGroups.getString(j));
                    }
                }

                if(keys.contains("userName")){
                    user.setUserName(TWCUserInfo.getString("userName"));
                }

                if(keys.contains("external")){
                    user.setExternal(TWCUserInfo.getBoolean("external"));
                }


                if(keys.contains("lastLoginDate")){
                    user.setLastLoginDate(TWCUserInfo.getString("lastLoginDate"));
                }


                if(keys.contains("roleAssignments")){
                    JSONArray roleAssignments = TWCUserInfo.getJSONArray("roleAssignments");
                    if(roleAssignments.length() > 0){
                        for(int l = 0; l < roleAssignments.length(); l++){
                            user.getRoleAssignments().add(roleAssignments.getJSONObject(l).getString("roleID"));
                        }

                    }

                }


                if(keys.contains("otherAttributes")){
                    //user.setResourceDetailsPreferredManagement(TWCUserInfo.getJSONObject("otherAttributes").getBoolean("enabled"));
                    JSONObject otherAttributes = TWCUserInfo.getJSONObject("otherAttributes");
                    JSONArray oaNames = otherAttributes.names();
                    ArrayList<String> otherAttributeKeys = new ArrayList<>();
                    if(oaNames != null){
                        for(int index = 0; index < oaNames.length();index++){
                            otherAttributeKeys.add(oaNames.get(index).toString());
                        }
                    }

                    if(otherAttributeKeys.contains("phone")){

                        user.setPhone(otherAttributes.getString("phone"));
                    }
                    if(otherAttributeKeys.contains("mobile")){
                        user.setMobile(otherAttributes.getString("mobile"));
                    }
                    if(otherAttributeKeys.contains("name")){
                        user.setName(otherAttributes.getString("name"));
                    }
                    if(otherAttributeKeys.contains("department")){
                        user.setDepartment(otherAttributes.getString("department"));
                    }
                    if(otherAttributeKeys.contains("email")){
                        user.setEmail(otherAttributes.getString("email"));
                    }

                }

                if(keys.contains("realm")){
                    user.setRealm(TWCUserInfo.getString("realm"));
                }
                // Add the user to the list containing all users
                this.TWCUsers.add(user);
            }

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    
    /***
     * 
     * This method will pull and parse project JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */
    
    public void initProjects(){
        try{
            bypassCertificate();
        }catch (Exception e){
            e.printStackTrace();
        }


        
        try{
            responseContent = new StringBuffer();
            Authenticator.setDefault(new test.TWCAuthenticator());
            url = new URL(baseUrl + "resources");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.addRequestProperty("Content-Type","application/json");


            responseCode = con.getResponseCode();
            //System.out.println(responseCode);
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }


            } else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }

            }
           
            reader.close();
            JSONArray ProjectJSON = new JSONArray(responseContent.toString());
            for(int i = 0; i < ProjectJSON.length(); i++){
                responseContent = new StringBuffer();
                Authenticator.setDefault(new test.TWCAuthenticator());
                url = new URL(baseUrl + "resources/"+ProjectJSON.getString(i));
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
                con.addRequestProperty("Content-Type","application/json");


                responseCode = con.getResponseCode();
                //System.out.println(responseCode);
                if(responseCode > 299){
                    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }


                } else{
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }

                }
                reader.close();
               
                JSONObject projectDetailsJSON = new JSONObject(responseContent.toString());
                ArrayList<String> projectJSONKeys = new ArrayList<>();
                JSONArray names = projectDetailsJSON.names();
                
                /**
                 * Unlike the workspaces, users do not contain the same keys because they vary
                 * depending on whether the user is internal or external. Before setting the data
                 * in a user object, we need to see if it even exists on the server. We do this by
                 * checking if the keys are stored in the JSON response
                 */
                
                for(int j = 0; j < names.length(); j++){
                    projectJSONKeys.add(names.getString(j));
                }
                TWCProject project = new TWCProject();
                project.setResourceID(ProjectJSON.getString(i));
                if(projectJSONKeys.contains("trunkID")){
                    project.setTrunkID(projectDetailsJSON.getString("trunkID"));
                }
                if(projectJSONKeys.contains("dcterms:description")){
                    project.setDescription(projectDetailsJSON.getString("dcterms:description"));
                }
                if(projectJSONKeys.contains("dcterms:title")){
                    project.setTitle(projectDetailsJSON.getString("dcterms:title"));
                }
                if(projectJSONKeys.contains("categoryID")){
                    project.setCategoryID(projectDetailsJSON.getString("categoryID"));
                }
                // Add project to list of projects
                this.TWCProjects.add(project);

            }


        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    
    /***
     * 
     * This method will pull and parse role JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */

    
    public void initRoles(){
        try{
            bypassCertificate();
        }catch (Exception e){
            e.printStackTrace();
        }


        // Getting the workspace ID's so that we can get more detailed information about them
        try{
            responseContent = new StringBuffer();
            Authenticator.setDefault(new test.TWCAuthenticator());
            url = new URL(baseUrl + "admin/roles");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.addRequestProperty("Content-Type","application/json");


            responseCode = con.getResponseCode();
            //System.out.println(responseCode);
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }


            } else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }

            }
            //System.out.println(responseContent.toString());
            reader.close();
            JSONArray roleJSON = new JSONArray(responseContent.toString());
            
            /**
             * Unlike the workspaces, users do not contain the same keys because they vary
             * depending on whether the user is internal or external. Before setting the data
             * in a user object, we need to see if it even exists on the server. We do this by
             * checking if the keys are stored in the JSON response
             */
            
            for(int i = 0; i < roleJSON.length(); i++){
                TWCRole role = new TWCRole();
                JSONObject roleObject = roleJSON.getJSONObject(i);
                ArrayList<String> keys = new ArrayList<>();
                JSONArray names = roleObject.names();
                for(int j = 0; j < names.length(); j++){
                    keys.add(names.getString(j));
                }
                if(keys.contains("name")){
                    role.setName(roleObject.getString("name"));
                }
                if(keys.contains("description")){
                    role.setDescription(roleObject.getString("description"));
                }
                if(keys.contains("ID")){
                    role.setID(roleObject.getString("ID"));
                }
                if(keys.contains("predefined")){
                    role.setPredefined(roleObject.getBoolean("predefined"));
                }
                if(keys.contains("containsGlobalOperationsOnly")){
                    role.setContainsGlobalOperationsOnly(roleObject.getBoolean("containsGlobalOperationsOnly"));
                }

                this.TWCRoles.add(role);
            }


        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    
    /***
     * 
     * This method will pull and parse branch JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */

    public void initBranches(){
        try{
            bypassCertificate();
        }catch (Exception e){
            e.printStackTrace();
        }

        for(TWCProject project : TWCProjects){
            try{
                responseContent = new StringBuffer();
                Authenticator.setDefault(new test.TWCAuthenticator());
                url = new URL(baseUrl + "resources/" + project.getResourceID() + "/branches");
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(30000);
                con.setReadTimeout(30000);
                con.addRequestProperty("Content-Type","application/json");


                responseCode = con.getResponseCode();
                //System.out.println(responseCode);
                if(responseCode > 299){
                    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }


                } else{
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }

                }
                //System.out.println(responseContent.toString());
                reader.close();
                JSONObject BranchesJSON = new JSONObject(responseContent.toString());
                JSONArray names = BranchesJSON.names();
                ArrayList<String> keys = new ArrayList<>();
                for(int i = 0; i < names.length(); i++){
                    keys.add(names.getString(i));
                }

                TWCBranch branch = new TWCBranch();
                
            
                
                if(keys.contains("ldp:contains")){
                    JSONArray ldpContains = BranchesJSON.getJSONArray("ldp:contains");
                    for(int i = 0; i < ldpContains.length(); i++){
                        branch.getProjects().add(ldpContains.getJSONObject(i).getString("@id"));
                    }
                }
                if(keys.contains("@id")){
                    branch.setId(BranchesJSON.getString("@id"));
                }
                project.getBranches().add(branch);
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
    
    /***
     * 
     * This method will pull and parse revision JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */

    public void initRevisions(){
        try{
            bypassCertificate();
        }catch (Exception e){
            e.printStackTrace();
        }

        for(TWCProject project : TWCProjects){
            try{
                responseContent = new StringBuffer();
                Authenticator.setDefault(new test.TWCAuthenticator());
                url = new URL(baseUrl + "resources/" + project.getResourceID() + "/revisions");
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setConnectTimeout(60000);
                con.setReadTimeout(60000);
                con.addRequestProperty("Content-Type","application/json");


                responseCode = con.getResponseCode();
                //System.out.println(responseCode);
                if(responseCode > 299){
                    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }


                } else{
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }

                }
                reader.close();
                JSONArray RevisionsJSON = new JSONArray(responseContent.toString());
                int[] revisions = new int[RevisionsJSON.length()];
                for(int i = 0; i < RevisionsJSON.length(); i++){
                    revisions[i] = RevisionsJSON.getInt(i);
                }
                TWCRevision revision = new TWCRevision();
                revision.setRevision(revisions);


                int recentRevision = revisions[0];
                responseContent = new StringBuffer();
                url = new URL(baseUrl + "resources/" + project.getResourceID() + "/revisions/" + recentRevision);
                con = (HttpsURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type","application/json");
                int responseCode = con.getResponseCode();
                if(responseCode > 299){
                    reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }


                } else{
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    while((line = reader.readLine()) != null){
                        responseContent.append(line);
                    }

                }
                reader.close();
                JSONArray revisionArray = new JSONArray(responseContent.toString());
                JSONObject object = revisionArray.getJSONObject(0);
                Set<String> keys = new HashSet<>();
                
                for(int i = 0; i < object.names().length(); i++){
                    keys.add(object.names().getString(i));
                }

                if(keys.contains("commitType")){
                    revision.setCommitType(object.getString("commitType"));
                }
                if(keys.contains("branchID")){
                    revision.setBranchID(object.getString("branchID"));
                }
                if(keys.contains("resourceID")){
                    revision.setResourceID(object.getString("resourceID"));
                }
                if(keys.contains("@base")){
                    revision.setBase(object.getString("@base"));
                }
                if(keys.contains("author")){
                    revision.setAuthor(object.getString("author"));
                }
                if(keys.contains("@type")){
                    JSONArray type = object.getJSONArray("@type");
                    String[] arr = new String[type.length()];
                    for(int i = 0; i < type.length(); i++){
                        arr[i] = type.getString(i);
                    }
                }
                if(keys.contains("pickedRevision")){
                    revision.setPickedRevision(object.getInt("pickedRevision"));
                }
                if(keys.contains("description")){
                    revision.setDescription(object.getString("description"));
                }
                if(keys.contains("@context")){
                    revision.setContext(object.getString("@context"));
                }
                if(keys.contains("directParent")){
                    revision.setDirectParent(object.getInt("directParent"));
                }
                if(keys.contains("dependencies")){
                    JSONArray dependencies = object.getJSONArray("dependencies");
                    String[] arr = new String[dependencies.length()];
                    for(int i = 0; i < dependencies.length(); i++){
                        arr[i] = dependencies.getString(i);
                    }
                    revision.setDependencies(arr);
                }
                if(keys.contains("createdDate")){
                    revision.setCreatedDate(object.getString("createdDate"));
                }
                if(keys.contains("ID")){
                    revision.setID(object.getString("ID"));
                }
                if(keys.contains("artifacts")){
                    revision.setArtifacts("artifacts");
                }
                this.TWCRevisions.add(revision);
                project.setRevision(revision);

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

    }
    
    /***
     * 
     * This method will pull and parse usergroups JSON data from the TWCRestAPI
     * Again we need to bypass the certificate in each of these methods, and
     * then set an Authenticator to be able to access the data from the server.
     * 
     * A non administrator can view data but cannot change it. Please login as an Administrator
     * to edit or update user data.
     */


    public void initUserGroups(){
        try{
            bypassCertificate();
            Authenticator.setDefault(new TWCAuthenticator());
        }catch (Exception e){
           e.printStackTrace();
        }
        try{
            url = new URL(baseUrl + "admin/usergroups?includeBody=true");
            con = (HttpsURLConnection) url.openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(3000);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type","application/json");
            responseCode = con.getResponseCode();
            responseContent = new StringBuffer();
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line = reader.readLine()) != null){
                    responseContent.append(line);
                }
                reader.close();
            }
            JSONArray userGroups = new JSONArray(responseContent.toString());
            Set<String> keys = new HashSet<>();
            JSONArray names = userGroups.getJSONObject(0).names();
            for(int i = 0; i < names.length(); i++){
                keys.add(names.getString(i));
            }

            for(int i = 0; i < userGroups.length(); i++){
                JSONObject obj = userGroups.getJSONObject(i);
                TWCUserGroup userGroup = new TWCUserGroup();
                if(keys.contains("name")){
                    userGroup.setName(obj.getString("name"));
                }
                if(keys.contains("description")){
                    userGroup.setDescription(obj.getString("description"));
                }
                if(keys.contains("realm")){
                    userGroup.setRealm(obj.getString("realm"));
                }
                if(keys.contains("usernames")){
                    JSONArray usernames = obj.getJSONArray("usernames");
                    for(int j = 0; j < usernames.length(); j++){
                        userGroup.getUserNames().add(usernames.getString(j));
                    }
                }
                if(keys.contains("ID")){
                    userGroup.setID(obj.getString("ID"));
                }
                if(keys.contains("roleAssignments")){
                    JSONObject roleAssignments = new JSONObject(obj.getJSONArray("roleAssignments"));
                    for(int j = 0; j < roleAssignments.length(); j++){
                        JSONObject roleInfo = roleAssignments.getJSONObject("roleAssignments");
                        userGroup.getRoleAssignments().add(roleInfo.getString("roleID"));
                    }
                }
                if(!TWCUserGroups.contains(userGroup)){
                    TWCUserGroups.add(userGroup);
                }

            }


        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void listWorkspaces(){
        for(TWCWorkspace workspace: TWCWorkspaces){
            workspace.display();
        }
    }
    public void listProjects(){
        for(TWCProject project: TWCProjects){
            project.display();
        }
    }
    public void listUsers(){
        for(TWCUser user: TWCUsers){
            user.dislplay();
        }
    }

    public void listRoles(){
        for(TWCRole role: TWCRoles){
            role.display();
        }
    }


    /***
     * 
     * @author Jay James
     * This class is responsible for user authentication and must be passed inside an
     * authenticator call at the start of each API call method.
     *
     */
    public static class TWCAuthenticator extends Authenticator {
        protected PasswordAuthentication getPasswordAuthentication(){
            String username = "morty.smith";
            String password = "ime";
            // Return the information that is used by the authenticator
            return new PasswordAuthentication(username,password.toCharArray());
        }
    }
    
    /**
     * This method allows you change a user's access to the server. 
     * WARNING !!!! PLEASE SELECT THE RIGHT OPTION WHEN PROMPTED BECAUSE YOU COULD ACCIDENTALLY DISABLE
     * A USER. IF ALL USERS WITH USER ADMIN RIGHTS ARE DISABLED THEN YOU WONT BE ABLE TO MAKE
     * ANY CALLS TO UPDATE INFORMATION TO THE SERVER.
     * @param email
     */
    public void enableOrDisableUser(String userName){
        boolean enabled = false;
        for(TWCUser user: TWCUsers){
            if(user.getUserName() != null && user.getUserName().equalsIgnoreCase(userName)){
                scan = new Scanner(System.in);
                System.out.println("Type -" + "\n" +
                        "1 - Enable User" + "\n"  +
                        "2 - Disable User");
                int choice = scan.nextInt();
                if(choice == 1) enabled = true;
                    try{
                        bypassCertificate();
                        Authenticator.setDefault(new test.TWCAuthenticator());
                        JSONObject userJSON = new JSONObject();
                        JSONObject otherAttributes = new JSONObject();
                        userJSON.put("userName",user.getUserName());
                        userJSON.put("external",user.getExternal());
                        userJSON.put("realm",user.getRealm());
                        userJSON.put("enabled",enabled);
                        userJSON.put("otherAttributes",otherAttributes);
                        otherAttributes.put("mobile",user.getMobile());
                        otherAttributes.put("name",user.getName());
                        otherAttributes.put("department",user.getDepartment());
                        otherAttributes.put("email",user.getEmail());
                        System.out.println(userJSON);
                        byte[] postDataBytes = userJSON.toString().getBytes();
                        url = new URL(baseUrl + "admin/users/" + user.getUserName());
                        con = (HttpsURLConnection) url.openConnection();
                        con.setRequestMethod("PUT");
                        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                        con.setRequestProperty("Content-Type","application/json");
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(10000);
                        con.setDoOutput(true);
                        OutputStream os = con.getOutputStream();
                        os.write(postDataBytes);
                        os.flush();
                        os.close();
                        JSONObject object = new JSONObject(userJSON);
                        System.out.println("nSending 'POST' request to URL : " + url);
                        System.out.println("Post Data : " + object.toString());
                        System.out.println("Response Code : " + con.getResponseCode());
                        System.out.println("Response Message : " + con.getResponseMessage());

                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

            }
        }


    }
    
    /**
     * Creates a comma separated email listing of all Active TWC Users (Enabled && External)
     */

    public void enabledExternalUsers(){
        File enabledExternalUsers = new File("Enabled External Users.txt");
        try{
            if(!enabledExternalUsers.exists()){
                enabledExternalUsers.createNewFile();
            }
            FileWriter userWriter = new FileWriter(enabledExternalUsers);
            for(TWCUser user: this.TWCUsers){
                if(user.isEnabled() && user.isExternal()){
                    userWriter.write(user.getEmail() + ";" + " ");
                }
            }
            userWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Creates an email listing of all active users that have not logged in within 30 days.
     */
    public void emailListingOfEnabledExternalUsersPast30Days(){
        File enabledExternalUsers = new File("Enabled External Users Who Have Not Logged In Over 30 Days.txt");
        try{
            if(!enabledExternalUsers.exists()){
                enabledExternalUsers.createNewFile();
            }
            FileWriter userWriter = new FileWriter(enabledExternalUsers);
            for(TWCUser user: this.TWCUsers){
                if(user.isEnabled() && user.isExternal()){
                    if(user.getLastLoginDate() != null){
                        System.out.println(user.getLastLoginDate());
                        int year = Integer.parseInt(user.getLastLoginDate().substring(0,3));
                        int month = Integer.parseInt(user.getLastLoginDate().substring(4,5));
                        int date = Integer.parseInt(user.getLastLoginDate().substring(6,7));
                        Date lastLoginDate = new Date(year,month,date);
                        Date today = new Date();

                        if(Math.abs(today.getTime() - lastLoginDate.getTime()) > 30){
                            userWriter.write(user.getEmail() + ";" + " ");
                        }
                    }
                }
            }
            userWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Takes a project's ID as input and returns a list of all the roles available to it.
     * @param resourceID
     * @return
     */
    public ArrayList<TWCRole> getRolesByProject(String resourceID){
        ArrayList<TWCRole> roles = new ArrayList<>();
        try{
            bypassCertificate();
        }catch (Exception e){
            e.printStackTrace();
        }
        // Getting the workspace ID's so that we can get more detailed information about them
        try{
            responseContent = new StringBuffer();
            Authenticator.setDefault(new test.TWCAuthenticator());
            url = new URL(baseUrl + "resources/"+resourceID+"/roles");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(10000);
            con.addRequestProperty("Content-Type","application/json");


            responseCode = con.getResponseCode();
            //System.out.println(responseCode);
            if(responseCode > 299){
                reader = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }


            } else{
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while((line = reader.readLine()) != null){
                    responseContent.append(line);
                }

            }
            //System.out.println(responseContent.toString());
            reader.close();
            JSONArray roleJSON = new JSONArray(responseContent.toString());
            for(int i = 0; i < roleJSON.length(); i++){
                TWCRole role = new TWCRole();
                JSONObject roleObject = roleJSON.getJSONObject(i);
                ArrayList<String> keys = new ArrayList<>();
                JSONArray names = roleObject.names();
                for(int j = 0; j < names.length(); j++){
                    keys.add(names.getString(j));
                }
                if(keys.contains("name")){
                    role.setName(roleObject.getString("name"));
                }
                if(keys.contains("description")){
                    role.setDescription(roleObject.getString("description"));
                }
                if(keys.contains("ID")){
                    role.setID(roleObject.getString("ID"));
                }
                if(keys.contains("predefined")){
                    role.setPredefined(roleObject.getBoolean("predefined"));
                }
                if(keys.contains("containsGlobalOperationsOnly")){
                    role.setContainsGlobalOperationsOnly(roleObject.getBoolean("containsGlobalOperationsOnly"));
                }

                roles.add(role);
            }

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return roles;
    }
    
    /**
     * Creates a CSV file based on the information present in the workspaces.
     */
    public void metricsByWorkspace(){
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int average = 0;
        try{
            File file = new File("Workspace Metrics.csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            /**
             * Creating the headers for our CSV File
             */
            bw.write("WORKSPACE NAME" + ",");
            bw.write("PROJECTS" + ",");
            bw.write("MAX" + ",");
            bw.write("MIN" + ",");
            bw.write("AVERAGE" + ",");
            bw.write("\n");
            for(TWCWorkspace workspace: TWCWorkspaces){
                if(workspace.getProjects().size() > max) max = workspace.getProjects().size();
                if(workspace.getProjects().size() < min) min = workspace.getProjects().size();
                average += workspace.getProjects().size();
                bw.write(workspace.getTitle() + "," + workspace.getProjects().size() + "\n");
            }
            bw.write(" , , " + max + "," + min + "," + average/TWCWorkspaces.size());
            bw.close();
        }catch (IOException e){

        }

    }
    
    /**
     * Creates a CSV file based on the information present in the users.
     */
    public void metricsByUser(){
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int average = 0;
        try{
            File file = new File("User Role Metrics.csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            /**
             * Creating the headers for our CSV File
             */
            bw.write("USERNAME" + ",");
            bw.write("ROLES" + ",");
            bw.write("MAX" + ",");
            bw.write("MIN" + ",");
            bw.write("AVERAGE" + ",");
            bw.write("\n");
            for(TWCUser user : TWCUsers){
                if(user.getRoleAssignments().size() > max) max = user.getRoleAssignments().size();
                if(user.getRoleAssignments().size() < min) min = user.getRoleAssignments().size();
                average += user.getRoleAssignments().size();
                bw.write(user.getUserName() + "," + user.getRoleAssignments().size() + "\n");
            }
            bw.write(" , , " + max + "," + min + "," + average/getTWCUsers().size());
            bw.close();
        }catch (IOException e){

        }


    }
    
    /**
     * Counts all projects and branches
     */
    public void totalProjectsAndBranches(){
        int totalBranches = 0;
        for(TWCProject project: TWCProjects){
            totalBranches += project.getBranches().size();
        }
        System.out.println("There are a total of " + TWCProjects.size() + " projects and " + totalBranches + " branches." );
    }
    
    /*
     * Counts all commits/revisions
     */
    public void totalCommits(){
        int totalCommmits = 0;
        for(TWCRevision revsion: TWCRevisions){
            totalCommmits += revsion.getRevision()[0];
        }
        System.out.println("There are a total of " + totalCommmits + " commits");
    }

    /**
     * Creates a CSV file based on the information present in the projects.
     */
    public void projectMetrics(){
        HashMap<String,Integer> metrics = new HashMap<>();
        for(TWCProject project: TWCProjects){
            metrics.put(project.getTitle(),getUsersByProject(project.getResourceID()).size());
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int total = 0;
        Set<String> keys = metrics.keySet();
        for(String key: keys){
            if(metrics.get(key) > max) max = metrics.get(key);
            if(metrics.get(key) < min) min = metrics.get(key);
            total += metrics.get(key);
        }
        File file = new File("Project Metrics Regardless of Role.csv");
        if(! file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){

            }
        }
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            /**
             * Creating the headers for our CSV File
             */
            bw.write("PROJECT NAME" + ',' + "NUMBER OF USERS" + "," );
            bw.write("MAX" + ",");
            bw.write("MIN" + ",");
            bw.write("AVERAGE");
            bw.write("\n");
            for(String key: keys){
                bw.write(key + "," + metrics.get(key) + "\n");
            }
            bw.write(" , , " + max + "," + min + "," + total/TWCProjects.size());
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    /**
     * Creates a csv file displaying metrics for our roles
     */
    public void projectMetricsByRole() {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int total = 0;
        try{
            File file = new File("Project Metrics By Role.csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if(!file.exists()) file.createNewFile();
            writer.write("PROJECT NAME" + ",");
            for(TWCRole role: TWCRoles){
                writer.write(role.getName().toUpperCase() + ",");

            }
            writer.write("MAX" + ",");
            writer.write("MIN" + ",");
            writer.write("AVERAGE");
            writer.write("\n");
            for(TWCProject project: TWCProjects){
                writer.write(project.getTitle() + ",");
                for(TWCRole role : TWCRoles){
                    int usercount = 0;
                    for(TWCUser user : TWCUsers){
                        if(user.getRoleAssignments().contains(role.getID())){
                            usercount++;
                        }
                    }
                    if(usercount > max) max = usercount;
                    if(usercount < min) min = usercount;
                    total += usercount;
                    writer.write(usercount + "," + "");
                }
                writer.write("\n");
            }
            writer.write(" , , , , , , , , , ," + max + "," + min + "," + total/TWCProjects.size());
            writer.close();

        }catch (IOException e){

        }

    }
    /**
     * Creates a csv file displaying metrics for our branches
     */
    public void metricsByBranch(){
        try{
            File file = new File("Project Metrics By Branch.csv");
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int average = 0;
            bufferedWriter.write("PROJECT NAME" + ",");
            bufferedWriter.write("BRANCHES" + ",");
            bufferedWriter.write("MAX" + ",");
            bufferedWriter.write("MIN" + ",");
            bufferedWriter.write("AVERAGE" + ",");
            bufferedWriter.write("\n");
            for(TWCProject project: TWCProjects){
                if(project.getBranches().size() > max) max = project.getBranches().size();
                if(project.getBranches().size() < min) min = project.getBranches().size();
                average += project.getBranches().size();
                bufferedWriter.write(project.getTitle() + ',' + project.getBranches().size() + "\n");
            }
            bufferedWriter.write(" , , " + max + "," + min + "," + average/getTWCProjects().size());
            bufferedWriter.close();

        }catch (IOException e){

        }

    }
    /**
     * Creates a csv file displaying metrics for our revisions
     */
    public void metricsByRevision(){
        try{
            File file = new File("Project Metrics By Revision.csv");
            if(!file.exists()){
                file.createNewFile();
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            int average = 0;
            bufferedWriter.write("PROJECT NAME" + ",");
            bufferedWriter.write("REVISIONS" + ",");
            bufferedWriter.write("MAX" + ",");
            bufferedWriter.write("MIN" + ",");
            bufferedWriter.write("AVERAGE" + ",");
            bufferedWriter.write("\n");
            for(TWCProject project: TWCProjects){
                int[] revision = project.getRevision().getRevision();
                if(revision[0] > max) max = revision[0];
                if(revision[revision.length-1] < min) min = revision[revision.length-1];
                average += revision[0];
                bufferedWriter.write(project.getTitle() + ',' + project.getRevision().getRevision().length + "\n");
            }
            bufferedWriter.write(" , , " + max + "," + min + "," + average/getTWCProjects().size());
            bufferedWriter.close();

        }catch (IOException e){

        }
    }

    /**
     * Creates a txt file for each role and the user listed under them
     */
    public void emailListingByRoleTxt(){

        try{
            for(TWCRole role: TWCRoles){
                File roleFile = new File(role.getName() + ".txt");
                FileWriter roleWriter = new FileWriter(roleFile);
                for(TWCUser user: TWCUsers){
                    if(user.getRoleAssignments().contains(role.getID())){
                        roleWriter.write(user.getEmail() + "; ");
                    }

                }
                roleWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Takes a project's ID as input and returns a list of users assigned to that project
     * @param resourceID
     * @return
     */
    public Set<TWCUser> getUsersByProject(String resourceID){

        Set<TWCUser> users = new HashSet<>();
        try{
            bypassCertificate();
            Authenticator.setDefault(new TWCAuthenticator());
            for(TWCProject project: TWCProjects){
                if(resourceID == project.getResourceID()){
                    for(TWCRole role: getRolesByProject(project.getResourceID())){
                        for(TWCUser user: TWCUsers){
                            if(user.getRoleAssignments().contains(role.getID())){
                                    users.add(user);
                            }
                        }
                    }
                }
            }

        }catch (Exception e){

        }
        return users;
    }
    
    /**
     * Returns a user based on the username passed in as an argument
     * @param userName
     * @return
     */
    public TWCUser getUser(String userName){
        for(TWCUser user: TWCUsers){
            if(user.getUserName().matches(userName)){
                return user;
            }
        }
        return null;
    }
    
    /**
     * Bypasses certificate issues
     * @throws Exception
     */

    public static void bypassCertificate() throws Exception{
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                //return new X509Certificate[0];
                return null;
            }
        }};

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null,trustAllCerts,new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }


    /**
     * Returns a user if their email is found within the list of users
     * @param email
     * @return
     */
    public TWCUser getUserByEmail(String email){
        for(TWCUser user: TWCUsers){
            if(user.getEmail() == email){
                return user;
            }
        }
        return null;
    }
    
    /**
     * This method will take a CSV file formatted as USER, DEPARTMENT and
     * will update the department of that user. Finally that information will be
     * updated to the server
     * @param fileName
     */
    public void updateUsersThroughCSV(String fileName){
        List<String[]> CSV = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
            while(reader.readLine() != null){
                System.out.println(reader.readLine());
                String line = reader.readLine();
                if(line != null){
                    String[] lineData = line.split(",");
                    CSV.add(lineData);
                }

            }
            for(String[] line: CSV) {
            	updateUserDepartment(line[0],line[1]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }
    
    /**
     * This method will create a user and post the information to the server
     * Internal users do not have a realm ID so the prompt for this is skipped
     * below if the user is chosen as external.
     * 
     * External users have a realm ID but no password, so the prompt for password
     * is skipped if the option of making the user internal is selected
     * 
     * When choosing a username, please note that the username cannot contain spaces
     * because to access user data the user name is passed into the URL for the API
     * call and a space will create a malformed URL which will in turn cause the program 
     * to crash.
     */
    public void createUser(){

        String username;
        boolean external;
        String realm = "";
        String password = "";
        boolean enabled;
        String mobile;
        String name;
        String department;
        String email;
        scan = new Scanner(System.in);
        System.out.println("Enter a username without spaces.");
        username = scan.nextLine();
        if(username.contains(" ")){
            System.out.println("Username cannot contain spaces");
            while(true){
                System.out.println("Please Enter a username without spaces");
                username = scan.nextLine();
                if(!username.contains(" ")){
                    break;
                }
            }
        }
        System.out.println("Is the user external? Enter true or false.");
        external = Boolean.parseBoolean(scan.nextLine().toLowerCase());
        System.out.println(external);
        if(external){
            System.out.println("Enter the user's realm ID");
            realm = scan.nextLine();
        }
        if(!external){
            System.out.println("Enter a password");
            password = scan.nextLine();
        }
        System.out.println("Is the user enabled? Enter true of false.");
        enabled = Boolean.parseBoolean(scan.nextLine().toLowerCase());
        System.out.println(enabled);
        System.out.println("Enter a mobile number.");
        mobile = scan.nextLine();
        System.out.println("Enter your human name");
        name = scan.nextLine();
        System.out.println("Enter a department");
        department = scan.nextLine();
        System.out.println("Enter an email");
        email = scan.nextLine();


        try{
            bypassCertificate();
            Authenticator.setDefault(new test.TWCAuthenticator());
            JSONObject userJSON = new JSONObject();
            JSONObject otherAttributes = new JSONObject();
            userJSON.put("userName",username);
            userJSON.put("external",external);
            userJSON.put("realm",realm);
            userJSON.put("password",password);
            userJSON.put("enabled",true);
            userJSON.put("otherAttributes",otherAttributes);
            otherAttributes.put("mobile",mobile);
            otherAttributes.put("name",name);
            otherAttributes.put("department",department);
            otherAttributes.put("email",email);
            System.out.println(userJSON);
            byte[] postDataBytes = userJSON.toString().getBytes();
            url = new URL(baseUrl + "admin/users");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            con.setRequestMethod("PUT");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type","application/json");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            os.close();
            JSONObject object = new JSONObject(userJSON);
            System.out.println("nSending 'POST' request to URL : " + url);
            System.out.println("Post Data : " + object.toString());
            System.out.println("Response Code : " + con.getResponseCode());
            System.out.println("Response Message : " + con.getResponseMessage());

        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * This method allows you to assign a role to a single user or list of users
     * Administrator login privileges are needed to assign roles
     * Please login as a user with admin access
     */
    public void assignRoles(){
        StringBuilder sb = new StringBuilder();
        int index;
        scan = new Scanner(System.in);
        while(true){
            for(int i = 0; i < TWCUsers.size(); i++){
                System.out.println(i + " - " + TWCUsers.get(i).getUserName());
            }
            System.out.println("Enter the index of the user you'd like to assign a role.");
            index = scan.nextInt();
            System.out.println("Are you finished selecting users? Enter 1 - YES or 2 - NO");
            int done = scan.nextInt();
            if(done == 1){
                sb.append(TWCUsers.get(index).getUserName());
                break;
            }else{
                sb.append(TWCUsers.get(index).getUserName() + ",");
            }


        }
        for(int i = 0; i < TWCProjects.size(); i++){
            System.out.println(i + " - " + TWCProjects.get(i).getTitle());
        }
        System.out.println("Enter the index of the project who's roles you'd like to see");
        index = scan.nextInt();
        TWCProject selectedProject = TWCProjects.get(index);
        for(int i = 0; i < getRolesByProject(selectedProject.getResourceID()).size(); i++){
            System.out.println(i + " - " + getRolesByProject(selectedProject.getResourceID()).get(i).getName());
        }
        System.out.println("Enter the index of the role in this particular project you'd like to assign to the users");
        index = scan.nextInt();
        String roleID = TWCRoles.get(index).getID();

        try{
            url = new URL(baseUrl + "admin/roles/" + roleID + "/users");
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type","text/plain");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            byte[] postDataBytes = sb.toString().getBytes();
            os.write(postDataBytes);
            os.flush();
            os.close();
            System.out.println("Response Code : " + con.getResponseCode());
            System.out.println("Response Message : " + con.getResponseMessage());
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    
    /**
     * This method allows you to assign a role to a single user or list of users
     * Administrator login privileges are needed to assign groups
     * Please login as a user with admin access
     */

    public void assignUsersToUserGroup(){
        StringBuilder sb = new StringBuilder();
        int index;
        scan = new Scanner(System.in);
        for(int i = 0; i < TWCUserGroups.size(); i++){
            System.out.println(i + " - " + TWCUserGroups.get(i).getName());
        }
        System.out.println("Enter the index of the group you'd like to assign users to");
        index = scan.nextInt();
        TWCUserGroup selectedUserGroup = TWCUserGroups.get(index);
        String userGroupID = selectedUserGroup.getID();

        while(true){
            for(int i = 0; i < TWCUsers.size(); i++){
                System.out.println(i + " - " + TWCUsers.get(i).getUserName());
            }
            System.out.println("Enter the index of the user you'd like to assign a role to.");
            index = scan.nextInt();
            System.out.println("Are you finished selecting users? Enter 1 - YES or 2 - NO");
            int done = scan.nextInt();
            if(done == 1){
                sb.append(TWCUsers.get(index).getUserName());
                break;
            }else{
                sb.append(TWCUsers.get(index).getUserName() + ",");
            }


        }

        try{
            JSONArray newUserGroups = new JSONArray("[" + sb.toString() + "]");
            url = new URL(baseUrl + "admin/usergroups/" + userGroupID);
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            con.setRequestProperty("Content-Type","application/json");
            con.setRequestMethod("POST");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            byte[] postDataBytes = newUserGroups.toString().getBytes();
            os.write(postDataBytes);
            os.flush();
            os.close();
            System.out.println("Response Code : " + con.getResponseCode());
            System.out.println("Response Message : " + con.getResponseMessage());
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Exports usernames and departments to csv
     */
    public void usersToCSV(){
        File users = new File("Users.csv");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(users));
            writer.write("EMAIL" + "," +"DEPT" + "\n");
            for(TWCUser user: TWCUsers){
                writer.write(user.getEmail() + "," + user.getDepartment() + "\n");
            }
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /*
     * Posts updated department along with all other user data so no data is lost
     * when the user is updated
     */
    public void updateUserDepartment(String email,String department){
        for(TWCUser user: TWCUsers){
            if(user.getEmail() != null && user.getEmail().equals(email)){
                try{
                    bypassCertificate();
                    Authenticator.setDefault(new test.TWCAuthenticator());
                    JSONObject userJSON = new JSONObject() ;
                    JSONObject otherAttributes = new JSONObject();
                    userJSON.put("userName",user.getUserName());
                    userJSON.put("external",user.getExternal());
                    userJSON.put("realm",user.getRealm());
                    userJSON.put("enabled",true);
                    userJSON.put("otherAttributes",otherAttributes);
                    otherAttributes.put("mobile",user.getMobile());
                    otherAttributes.put("name",user.getName());
                    otherAttributes.put("department",department);
                    otherAttributes.put("email",email);
                    System.out.println(userJSON);
                    byte[] postDataBytes = userJSON.toString().getBytes();
                    url = new URL(baseUrl + "admin/users/" +user.getUserName());
                    con = (HttpsURLConnection) url.openConnection();
                    con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                    con.setRequestProperty("Content-Type","application/json");
                    con.setConnectTimeout(60000);
                    con.setReadTimeout(60000);
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    os.write(postDataBytes);
                    os.flush();
                    os.close();
                    JSONObject object = new JSONObject(userJSON);
                    System.out.println("nSending 'POST' request to URL : " + url);
                    System.out.println("Post Data : " + object.toString());
                    System.out.println("Response Code : " + con.getResponseCode());
                    System.out.println("Response Message : " + con.getResponseMessage());

                }catch (MalformedURLException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Setters and getters for each TWCRestAPI property
     * @return
     */
    public ArrayList<TWCWorkspace> getTWCWorkspaces() {
        return TWCWorkspaces;
    }

    public void setTWCWorkspaces(ArrayList<TWCWorkspace> TWCWorkspaces) {
        this.TWCWorkspaces = TWCWorkspaces;
    }

    public ArrayList<TWCUser> getTWCUsers() {
        return TWCUsers;
    }

    public void setTWCUsers(ArrayList<TWCUser> TWCUsers) {
        this.TWCUsers = TWCUsers;
    }

    public ArrayList<TWCRole> getTWCRoles() {
        return TWCRoles;
    }

    public void setTWCRoles(ArrayList<TWCRole> TWCRoles) {
        this.TWCRoles = TWCRoles;
    }

    public ArrayList<TWCProject> getTWCProjects() {
        return TWCProjects;
    }

    public void setTWCProjects(ArrayList<TWCProject> TWCProjects) {
        this.TWCProjects = TWCProjects;
    }

    public ArrayList<TWCRevision> getTWCRevisions() {
        return TWCRevisions;
    }

    public void setTWCRevisions(ArrayList<TWCRevision> TWCRevisions) {
        this.TWCRevisions = TWCRevisions;
    }

    public ArrayList<TWCUserGroup> getTWCUserGroups() {
        return TWCUserGroups;
    }

    public void setTWCUserGroups(ArrayList<TWCUserGroup> TWCUserGroups) {
        this.TWCUserGroups = TWCUserGroups;
    }


}
