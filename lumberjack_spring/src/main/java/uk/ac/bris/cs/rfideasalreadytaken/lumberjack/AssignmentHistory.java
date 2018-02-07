package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import java.sql.Time;

public class AssignmentHistory {

    private String deviceID;
    private String userID;
    private java.sql.Date dateAssigned;
    private java.sql.Time timeAssigned;
    private java.sql.Date dateReturned;
    private java.sql.Time timeReturned;
    private java.sql.Time timeRemovedFor;
    private boolean returnedSuccessfully;
    private String returnedBy;

    public String getDeviceID() {
        return deviceID;
    }

    public String getUserID() {
        return userID;
    }

    public java.sql.Date getDateAssigned() {
        return dateAssigned;
    }

    public Time getTimeAssigned() {
        return timeAssigned;
    }

    public java.sql.Date getDateReturned() {
        return dateReturned;
    }

    public Time getTimeReturned() {
        return timeReturned;
    }

    public Time getTimeRemovedFor() {
        return timeRemovedFor;
    }

    public boolean isReturnedSuccessfully() {
        return returnedSuccessfully;
    }

    public String getReturnedBy() {
        return returnedBy;
    }

    public void setDeviceID(String deviceID) {

        this.deviceID = deviceID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDateAssigned(java.sql.Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public void setTimeAssigned(Time timeAssigned) {
        this.timeAssigned = timeAssigned;
    }

    public void setDateReturned(java.sql.Date dateReturned) {
        this.dateReturned = dateReturned;
    }

    public void setTimeReturned(Time timeReturned) {
        this.timeReturned = timeReturned;
    }

    public void setTimeRemovedFor(Time timeRemovedFor) {
        this.timeRemovedFor = timeRemovedFor;
    }

    public void setReturnedSuccessfully(boolean returnedSuccessfully) {
        this.returnedSuccessfully = returnedSuccessfully;
    }

    public void setReturnedBy(String returnedBy) {
        this.returnedBy = returnedBy;
    }
}
