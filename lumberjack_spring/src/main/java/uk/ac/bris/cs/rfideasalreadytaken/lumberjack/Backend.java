package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

@Service
public class Backend implements FromCardReader{

    private boolean userLoaded = false;
    private User currentUser = new User("","",0,0,false);
    private boolean connected = false;
    private Connection conn = null;
    private Statement stmt = null;

    public final String successReturn = "Device returned successfully";
    public final String successRemoval = "Device taken out successfully";
    public final String successReturnAndRemoval = "Device was not returned correctly, so has been taken out under new user";
    public final String successUserLoaded = "User loaded sucessfully";
    public final String failScanNotRecognised = "Scan not recognised";
    public final String failNoLoadedUser = "No user has been loaded";
    public final String failUserAtDeviceLimit = "User is at their limit of removable devices";
    public final String failUserNotPermittedToRemove = "User is not permitted to remove devices";
    public final String failDeviceIsNotAvailable = "Device cannot be taken out";
    public final String errorConnectionFailed = "Error connecting to Database";
    public final String errorUserNotLoaded = "Error loading user";
    public final String errorDeviceNotLoaded = "Error loading device";
    public final String errorDeviceHandlingFailed = "Error handling device return or takeout";
    public final String errorReturnFailed = "Error returning device";
    public final String errorRemovalFailed = "Error taking out device";

    //TODO add time limit before curretn user resets
    public String scanRecieved(Scan scan) throws Exception{

        if(connectToDatabase()) {

            String result;

            if (isValidUser(scan)) {
                result = userScanned(scan);
                return result;
            }
            else if (isValidDevice(scan)) {
                result = deviceScanned(scan);

                if(result == successReturn || result == successRemoval || result == successReturnAndRemoval){
                    userLoaded = false;
                }

                return result;
            }

            return failScanNotRecognised;
        }
        else{
            return errorConnectionFailed;
        }
    }

    private boolean connectToDatabase() throws Exception{

        if(connected == false){
            try {
                MysqlDataSource dataSource = new MysqlDataSource();

                dataSource.setServerName("129.150.119.251");
                dataSource.setPortNumber(3306);
                dataSource.setDatabaseName("LumberjackDatabase");
                dataSource.setUser("lumberjack");
                dataSource.setPassword("Lumberjack1#");
                dataSource.setConnectTimeout(5000);

                conn = dataSource.getConnection();

                stmt = conn.createStatement();

                connected = true;

            }catch(com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                return false;
            }
        }

        return true;
    }

    private String userScanned(Scan scan) throws Exception{

        try {
            User loadedUser = loadUser(scan);
            currentUser = loadedUser;
            userLoaded = true;
            return successUserLoaded;
        }
        catch(Exception e){
            return errorUserNotLoaded;
        }
    }

    private String deviceScanned(Scan scan) throws Exception{

        Device loadedDevice;

        try {
            loadedDevice = loadDevice(scan);
        }
        catch(Exception e){
            return errorDeviceNotLoaded;
        }

        try {
            if (canDeviceBeRemoved(loadedDevice)) {

                if (!userLoaded) {
                    return failNoLoadedUser;
                }

                if (isDeviceCurrentlyOut(loadedDevice)) {

                    return returnDevice(loadedDevice, currentUser);
                }
                else {

                    if (isUserAtDeviceLimit(currentUser)) {
                        return failUserAtDeviceLimit;
                    }
                    else if (canUserRemoveDevices(currentUser)) {
                        return failUserNotPermittedToRemove;
                    }

                    return takeOutDevice(loadedDevice, currentUser);
                }
            }
            else {
                return failDeviceIsNotAvailable;
            }
        }
        catch(Exception e){
            return errorDeviceHandlingFailed;
        }
    }

    private User loadUserFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            User user = new User();
            user.setCanRemove(rs.getBoolean("CanRemove"));
            user.setDeviceLimit(rs.getInt("DeviceLimit"));
            user.setDevicesRemoved(rs.getInt("DevicesRemoved"));
            user.setId(rs.getString("id"));
            user.setScanValue(rs.getString("ScanValue"));
            return user;
        }
        return null;
    }

    private Device loadDeviceFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Device device = new Device();
            device.setAvailable(rs.getBoolean("Available"));
            device.setCurrentlyAssigned(rs.getBoolean("CurrentlyAssigned"));
            device.setType(rs.getString("Type"));
            device.setId(rs.getString("id"));
            device.setScanValue(rs.getString("ScanValue"));
            return device;
        }
        return null;
    }

    private Assignment loadAssignmentFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Assignment assignment = new Assignment();
            assignment.setDeviceID(rs.getString("DeviceID"));
            assignment.setUserID(rs.getString("UserID"));
            assignment.setDateAssigned(rs.getDate("DateAssigned"));
            assignment.setAssignmentID(rs.getString("id"));
            assignment.setTimeAssigned(rs.getTime("TimeAssigned"));
            return assignment;
        }
        return null;
    }

    private AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            AssignmentHistory assignmentHistory = new AssignmentHistory();
            assignmentHistory.setAssignmentHistoryID(rs.getString("id"));
            assignmentHistory.setDeviceID(rs.getString("DeviceID"));
            assignmentHistory.setUserID(rs.getString("UserID"));
            assignmentHistory.setDateAssigned(rs.getDate("DateAssigned"));
            assignmentHistory.setTimeAssigned(rs.getTime("TimeAssigned"));
            assignmentHistory.setDateReturned(rs.getDate("DateReturned"));
            assignmentHistory.setTimeReturned(rs.getTime("TimeReturned"));
            assignmentHistory.setTimeRemovedFor(rs.getTime("TimeRemovedFor"));
            assignmentHistory.setReturnedSuccessfully(rs.getBoolean("ReturnedSuccessfully"));
            assignmentHistory.setReturnedByID(rs.getString("ReturnedBy"));
            return assignmentHistory;
        }
        return null;
    }

    //TODO switch scan value to be correct thing
    private boolean isValidUser(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private boolean isValidDevice(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT id FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

    //TODO switch scan value to be correct thing
    private User loadUser(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
    }

    //TODO switch scan value to be correct thing
    private Device loadDevice(Scan scan) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scan.getUser());
        ResultSet rs = stmt.executeQuery();
        Device device = loadDeviceFromResultSet(rs);
        return device;
    }

    public boolean canUserRemoveDevices(User user) throws Exception{
        if(user.canRemove()){
            return true;
        }
        return false;
    }

    public boolean isUserAtDeviceLimit(User user){
        if(user.getDeviceLimit() == user.getDevicesRemoved()){
            return true;
        }
        return false;
    }

    private boolean canDeviceBeRemoved(Device device) throws Exception{
        if(device.isAvailable()){
            return true;
        }
        return false;
    }

    private boolean isDeviceCurrentlyOut(Device device) throws Exception{
        if(device.isCurrentlyAssigned()){
            return true;
        }
        return false;
    }

    private String returnDevice(Device device, User returningUser) throws Exception{

        try{
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Assignments WHERE DeviceID = ");
            stmt.setString(1, device.getId());
            ResultSet rs = stmt.executeQuery();
            Assignment assignment = loadAssignmentFromResultSet(rs);

            deleteFromAssignments(assignment.getId());

            insertIntoAssignmentHistory(assignment,returningUser.getId());

            int removed = returningUser.getDevicesRemoved()-1;
            PreparedStatement stmt3 = conn.prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, assignment.getUserID());
            stmt3.execute();

            PreparedStatement stmt4 = conn.prepareStatement("UPDATE Devices SET CurrentlyAssigned = false WHERE id = ?");
            stmt4.setString(1, device.getId());
            stmt4.execute();

            if(returningUser.getId() != assignment.getUserID()){

                takeOutDevice(device, returningUser);
                return successReturnAndRemoval;
            }

            return successReturn;
        }
        catch(Exception e) {
            return errorReturnFailed;
        }
    }

    //TODO get current date and time and calculate time removed for
    public String takeOutDevice(Device device, User user) throws Exception{
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE Devices SET CurrentlyAssigned = true WHERE id = ?");
            stmt.setString(1, device.getId());
            stmt.execute();

            int removed = user.getDevicesRemoved() + 1;
            PreparedStatement stmt3 = conn.prepareStatement("UPDATE Users SET DevicesRemoved = ? WHERE id = ?");
            stmt3.setInt(1, removed);
            stmt3.setString(2, user.getId());
            stmt3.execute();

            java.sql.Date date = java.sql.Date.valueOf("2018-02-10");
            java.sql.Time time = java.sql.Time.valueOf("14:45:20");
            Assignment assignment = new Assignment(device.getId(), user.getId(), date, time);
            insertIntoAssignments(assignment);
            return successRemoval;
        }
        catch(Exception e) {
            return errorRemovalFailed;
        }
    }

    private boolean insertIntoDevices(Device device) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Devices (id, scanValue, Type, Available, CurrentlyAssigned) VALUES (?,?,?,?,?)");
        stmt.setString(1, device.getId());
        stmt.setString(2, device.getScanValue());
        stmt.setString(3, device.getType());
        stmt.setBoolean(4, device.isAvailable());
        stmt.setBoolean(5, device.isCurrentlyAssigned());
        stmt.execute();
        return true;
    }

    private boolean deleteFromDevices(String deviceID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Devices WHERE id = ?");
        stmt.setString(1, deviceID);
        stmt.execute();
        return true;
    }

    private boolean insertIntoUsers(User user) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Users (id, scanValue, DeviceLimit, DevicesRemoved, CanRemove)" +
                "VALUES (?,?,?,?,?)");
        stmt.setString(1, user.getId());
        stmt.setString(2, user.getScanValue());
        stmt.setInt(3, user.getDeviceLimit());
        stmt.setInt(4, user.getDevicesRemoved());
        stmt.setBoolean(5, user.canRemove());
        stmt.execute();
        return true;
    }

    private boolean deleteFromUsers(String userID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Users WHERE id = ?");
        stmt.setString(1, userID);
        stmt.execute();
        return true;
    }

    private boolean insertIntoAssignments(Assignment assignment) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Assignments (DeviceID, UserID, DateAssigned, TimeAssigned)\n" +
                "VALUES (?,?,?,?)");
        stmt.setString(1, assignment.getDeviceID());
        stmt.setString(2, assignment.getUserID());
        stmt.setDate(3, assignment.getDateAssigned());
        stmt.setTime(4, assignment.getTimeAssigned());
        stmt.execute();
        return true;
    }

    private boolean deleteFromAssignments(String assignmentID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Assignments WHERE id = ?");
        stmt.setString(1, assignmentID);
        stmt.execute();
        return true;
    }

    //TODO get current date and time and calculate time removed for
    private boolean insertIntoAssignmentHistory(Assignment assignment, String returningUserID) throws Exception{

        boolean returnedSuccessfully = false;
        if(assignment.getUserID() == returningUserID){
            returnedSuccessfully = true;
        }

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO AssignmentHistory (DeviceID, UserID, DateAssigned, TimeAssigned, DateReturned, TimeReturned, TimeRemovedFor, ReturnedSuccessfully, ReturnedBy)" +
                "VALUES (?,?,?,?,?,?,?,?,?)");

        stmt.setString(1, assignment.getDeviceID());
        stmt.setString(2, assignment.getUserID());
        stmt.setDate(3, assignment.getDateAssigned());
        stmt.setTime(4, assignment.getTimeAssigned());
        stmt.setDate(5, assignment.getDateAssigned());
        stmt.setTime(6, assignment.getTimeAssigned());
        stmt.setTime(7, assignment.getTimeAssigned());
        stmt.setBoolean(8, returnedSuccessfully);
        stmt.setString(9,returningUserID);
        return true;
    }

    private boolean deleteFromAssignmentHistory(String assignmentHistoryID) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM AssignmentHistory WHERE id = ?");
        stmt.setString(1, assignmentHistoryID);
        stmt.execute();
        return true;
    }

    public boolean resetDatabase() throws Exception{

        connectToDatabase();

        stmt.execute("DROP TABLE IF EXISTS AssignmentHistory");
        stmt.execute("DROP TABLE IF EXISTS Assignments");
        stmt.execute("DROP TABLE IF EXISTS Users");
        stmt.execute("DROP TABLE IF EXISTS Devices");

        stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "\nid varchar(100) NOT NULL," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nDeviceLimit int," +
                "\nDevicesRemoved int," +
                "\nCanRemove bit," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Devices (" +
                "\nid varchar(100) NOT NULL," +
                "\nScanValue varchar(100) NOT NULL UNIQUE," +
                "\nType varchar(100)," +
                "\nAvailable bit," +
                "\nCurrentlyAssigned bit," +
                "\nPRIMARY KEY (id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS Assignments (" +
                "\nid int NOT NULL AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        stmt.execute("CREATE TABLE IF NOT EXISTS AssignmentHistory (" +
                "\nid int NOT NULL AUTO_INCREMENT,\n" +
                "\nDeviceID varchar(100) NOT NULL," +
                "\nUserID varchar(100) NOT NULL," +
                "\nDateAssigned DATE," +
                "\nTimeAssigned TIME," +
                "\nDateReturned DATE," +
                "\nTimeReturned TIME," +
                "\nTimeRemovedFor time," +
                "\nReturnedSuccessfully bit," +
                "\nReturnedBy varchar(100) NOT NULL," +
                "\nPRIMARY KEY (id)," +
                "\nCONSTRAINT FOREIGN KEY (DeviceID) REFERENCES Devices(id)," +
                "\nCONSTRAINT FOREIGN KEY (UserID) REFERENCES Users(id));");

        return true;
    }

    public boolean insertTestCases() throws Exception{

        resetDatabase();

        User user = new User("Aidan9876", "scanValueU1", 2, 0, true);
        insertIntoUsers(user);
        user = new User("Betty1248", "scanValueU2", 1, 1, true);
        insertIntoUsers(user);
        user = new User("Callum2468", "scanValueU3", 3, 0, false);
        insertIntoUsers(user);
        user = new User("Dorathy0369", "scanValueU4", 1, 0, true);
        insertIntoUsers(user);

        Device device = new Device("laptop01", "scanValueD1", "laptop", true, false);
        insertIntoDevices(device);
        device = new Device("laptop02", "scanValueD2", "laptop", true, true);
        insertIntoDevices(device);
        device = new Device("laptop03", "scanValueD3", "laptop", false, false);
        insertIntoDevices(device);
        device = new Device("camera01", "scanValueD4", "camera", true, false);
        insertIntoDevices(device);

        java.sql.Date date = java.sql.Date.valueOf("2018-02-10");
        java.sql.Time time = java.sql.Time.valueOf("14:45:20");

        Assignment assignment = new Assignment("laptop02", "Betty1248", date,time);
        insertIntoAssignments(assignment);

        date = java.sql.Date.valueOf("2018-02-09");
        time = java.sql.Time.valueOf("09:32:13");

        assignment = new Assignment("laptop01", "Callum2468", date,time);
        insertIntoAssignmentHistory(assignment, "Aidan9876");

        return true;
    }
}