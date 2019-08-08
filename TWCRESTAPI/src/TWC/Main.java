package TWC;

public class Main {

    public static void main(String[] args) {

        TWCRestAPI twc = new TWCRestAPI();
        try{

            twc.init();
            twc.listUsers();
            twc.listWorkspaces();
            twc.listProjects();
            twc.listRoles();
            twc.projectMetrics();
            twc.projectMetricsByRole();
            twc.metricsByBranch();
            twc.metricsByRevision();
            twc.metricsByWorkspace();
            twc.metricsByUser();
            twc.totalProjectsAndBranches();
            twc.totalCommits();
            twc.enabledExternalUsers();
            twc.emailListingOfEnabledExternalUsersPast30Days();
            twc.emailListingByRoleTxt();
            //twc.enableOrDisableUser("JayJJamesJr@Gmail.com");
            twc.assignRoles();
            twc.createUser();
            //twc.updateUsersThroughCSV("C:/Users/jayjj/Downloads/TWC DEV User Data Example for Dept Upate 17JUL19.csv");
            twc.initUserGroups();

            twc.assignUsersToUserGroup();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
