package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Device;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.User;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Rule;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.UserGroup;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.GroupPermission;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.Assignment;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.data.AssignmentHistory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BackendDatabaseLoading extends BackendDatabaseLogic {

    protected User loadUser(ScanDTO scanDTO) throws Exception{
        try{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getUser());
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
        }
        catch (Exception e){return new User();}
    }

    protected User loadUser(String userID) throws Exception{
        try{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Users WHERE id = ?");
        stmt.setString(1, userID);
        ResultSet rs = stmt.executeQuery();
        User user = loadUserFromResultSet(rs);
        return user;
        }
        catch (Exception e){return new User();}
    }

    protected ArrayList<AssignmentHistory> loadUserAssignmentHistory(User user) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AssignmentHistory WHERE UserId = ?");
        stmt.setString(1, user.getId());
        ResultSet rs = stmt.executeQuery();
        ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
        while(rs.next()) assignmentHistorys.add(loadAssignmentHistoryFromResultSet(rs));
        return assignmentHistorys;
    }

    protected Device loadDevice(ScanDTO scanDTO) throws Exception{
        try{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Devices WHERE ScanValue = ?");
        stmt.setString(1, scanDTO.getDevice());
        ResultSet rs = stmt.executeQuery();
        Device device = loadDeviceFromResultSet(rs);
        return device;
        }
        catch (Exception e){return new Device();}
    }

    protected ArrayList<AssignmentHistory> loadDeviceAssignmentHistory(Device device) throws Exception{
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AssignmentHistory WHERE DeviceId = ?");
        stmt.setString(1, device.getId());
        ResultSet rs = stmt.executeQuery();
        ArrayList<AssignmentHistory> assignmentHistorys = new ArrayList<AssignmentHistory>();
        while(rs.next()) assignmentHistorys.add(loadAssignmentHistoryFromResultSet(rs));
        return assignmentHistorys;
    }

    protected User loadUserFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            User user = new User();
            user.setCanRemove(rs.getBoolean("CanRemove"));
            user.setDeviceLimit(rs.getInt("DeviceLimit"));
            user.setDevicesRemoved(rs.getInt("DevicesRemoved"));
            user.setId(rs.getString("id"));
            user.setScanValue(rs.getString("ScanValue"));
            user.setGroupId(rs.getString("GroupID"));
            return user;
        }
        return null;
    }

    protected Device loadDeviceFromResultSet(ResultSet rs) throws Exception{
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

    protected Assignment loadAssignmentFromResultSet(ResultSet rs) throws Exception{
        if(rs.next()){
            Assignment assignment = new Assignment();
            assignment.setDeviceID(rs.getString("DeviceID"));
            assignment.setUserID(rs.getString("UserID"));
            assignment.setDateAssigned(rs.getDate("DateAssigned"));
            assignment.setAssignmentID(rs.getInt("id"));
            assignment.setTimeAssigned(rs.getTime("TimeAssigned"));
            return assignment;
        }
        return null;
    }

    protected AssignmentHistory loadAssignmentHistoryFromResultSet(ResultSet rs) throws Exception{
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
}
