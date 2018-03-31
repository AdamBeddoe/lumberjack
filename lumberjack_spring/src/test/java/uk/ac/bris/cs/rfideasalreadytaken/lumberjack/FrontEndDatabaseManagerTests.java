package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ArrayList;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.*;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "file:${user.dir}/config/testdatabase.properties")
public class FrontEndDatabaseManagerTests {

    @Autowired
    private DatabaseTesting databaseTesting;

    @Autowired
    private WebBackend webBackend;

    @Before
    public void setupDatabase() throws Exception {
        databaseTesting.insertTestCases();
    }


    @Test
    public void testInsertUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);

        assertEquals(webBackend.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testInsertUsers() throws Exception {

        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user_1");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        User testUser2 = new User();
        testUser2.setCanRemove(true);
        testUser2.setDeviceLimit(1);
        testUser2.setDevicesRemoved(0);
        testUser2.setId("test_user_2");
        testUser2.setGroupId("groupOne");
        testUser2.setScanValue("98765");

        List<User> list = new ArrayList<>();
        list.add(testUser);
        list.add(testUser2);

        webBackend.insertUsers(list);
        assertEquals(webBackend.getUser(testUser.getId()), testUser);
        assertEquals(webBackend.getUser(testUser2.getId()), testUser2);
    }

    @Test
    public void testGetUserNotInDatabase() throws Exception {
        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNull(webBackend.getUser(testUser.getId()));
    }

    @Test
    public void testGetUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        assertEquals(webBackend.getUser(testUser.getId()),testUser);
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = webBackend.getUsers();
        assertTrue(users.contains(new User("Aidan9876", "1314831486", 2, 0, true, "groupOne")));
        assertTrue(users.contains(new User("Betty1248", "457436545", 3, 1, true, "groupTwo")));
        assertTrue(users.contains(new User("Callum2468", "845584644", 3, 0, false, "groupTwo")));
        assertTrue(users.contains(new User("Dorathy0369", "94648329837", 0, 0, true, "groupOne")));
    }

    @Test
    public void testEditUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.editUser("Aidan9876",testUser);

        assertEquals(webBackend.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testChangeUserGroup() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);
        webBackend.changeUserGroup(testUser,new UserGroup("groupTwo"));

        assertEquals(webBackend.getUser(testUser.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testSetUserMaxDevices() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        webBackend.insertUser(testUser);
        webBackend.setUserMaxDevices(testUser,2);

        assertEquals(webBackend.getUser(testUser.getId()).getDeviceLimit(), 2);
    }

    @Test
    public void testChangeUsersGroup() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user_1");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        User testUser2 = new User();
        testUser2.setCanRemove(true);
        testUser2.setDeviceLimit(1);
        testUser2.setDevicesRemoved(0);
        testUser2.setId("test_user_2");
        testUser2.setGroupId("groupOne");
        testUser2.setScanValue("98765");

        List<User> list = new ArrayList<>();
        list.add(testUser);
        list.add(testUser2);

        webBackend.insertUsers(list);
        webBackend.changeUsersGroup(list,new UserGroup("groupTwo"));

        assertEquals(webBackend.getUser(testUser.getId()).getGroupId(), "groupTwo");
        assertEquals(webBackend.getUser(testUser2.getId()).getGroupId(), "groupTwo");
    }

    @Test
    public void testDeleteUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        webBackend.deleteUser(testUser.getId());
        assertNull(webBackend.getUser(testUser.getId()));
    }

    @Test
    public void testResetUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("Betty1248", "457436545", 3, 1, true, "groupTwo"));
        webBackend.resetUsers();
        assertEquals(webBackend.getUsers(),users);
    }

    @Test
    public void testInsertDevice() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.insertDevice(testDevice);

        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testInsertDevices() throws Exception {

        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        Device testDevice2 = new Device();
        testDevice2.setType("laptop");
        testDevice2.setAvailable(true);
        testDevice2.setCurrentlyAssigned(false);
        testDevice2.setRuleID("ruleSet1");
        testDevice2.setScanValue("98765");
        testDevice2.setId("test_device_2");

        List<Device> list = new ArrayList<>();
        list.add(testDevice);
        list.add(testDevice2);

        webBackend.insertDevices(list);
        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
        assertEquals(webBackend.getDevice(testDevice2.getId()), testDevice2);
    }

    @Test
    public void testGetDeviceNotInDatabase() throws Exception {
        Device testDevice = new Device();
        testDevice.setId("definitely_not_in_there_test_device");
        assertNull(webBackend.getDevice(testDevice.getId()));
    }

    @Test
    public void testGetDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        assertEquals(webBackend.getDevice(testDevice.getId()),testDevice);
    }

    @Test
    public void testGetDevices() throws Exception {
        List<Device> obtainedDevices = webBackend.getDevices();
        assertTrue(obtainedDevices.contains(new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2")));
        assertTrue(obtainedDevices.contains(new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2")));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        webBackend.deleteDevice(testDevice.getId());
        assertNull(webBackend.getDevice(testDevice.getId()));
    }

    @Test
    public void testResetDevices() throws Exception {
        List<Device> devices = new ArrayList<>();
        devices.add(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2"));
        webBackend.resetDevices();
        assertEquals(webBackend.getDevices(),devices);
    }

    @Test
    public void testEditDevice() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.editDevice("laptop01",testDevice);

        assertEquals(webBackend.getDevice(testDevice.getId()), testDevice);
    }

    @Test
    public void testSetDeviceType() throws Exception {
        Device testDevice = new Device();
        testDevice.setType("laptop");
        testDevice.setAvailable(true);
        testDevice.setCurrentlyAssigned(false);
        testDevice.setRuleID("ruleSet1");
        testDevice.setScanValue("12345");
        testDevice.setId("test_device_1");

        webBackend.insertDevice(testDevice);
        webBackend.setDeviceType(testDevice,"tablet");

        assertEquals(webBackend.getDevice(testDevice.getId()).getType(), "tablet");
    }

    @Test
    public void testGetUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("groupOne");
        assertEquals(webBackend.getUserGroup(testUserGroup.getId()),testUserGroup);
    }

    @Test
    public void testGetUserGroups() throws Exception {
        List<UserGroup> obtainedUserGroups = webBackend.getUserGroups();
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupOne")));
        assertTrue(obtainedUserGroups.contains(new UserGroup("groupTwo")));
    }

    @Test
    public void testDeleteUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("test_group_1");
        webBackend.insertUserGroup(testUserGroup);
        webBackend.deleteUserGroup(testUserGroup.getId());
        assertNull(webBackend.getUserGroup(testUserGroup.getId()));
    }

    @Test
    public void testInsertUserGroup() throws Exception {
        UserGroup testUserGroup = new UserGroup("test_group_1");
        webBackend.insertUserGroup(testUserGroup);
        assertEquals(webBackend.getUserGroup(testUserGroup.getId()), testUserGroup);
    }

    @Test
    public void testGetRule() throws Exception {
        Rule testRule = new Rule("ruleSet1",20);
        assertEquals(webBackend.getRule(testRule.getId()),testRule);
    }

    @Test
    public void testGetRules() throws Exception {
        List<Rule> obtainedRules = webBackend.getRules();
        assertTrue(obtainedRules.contains(new Rule("ruleSet1",20)));
        assertTrue(obtainedRules.contains(new Rule("ruleSet2",22)));
    }

    @Test
    public void testDeleteRule() throws Exception {
        Rule testRule = new Rule("test_rule_1",10);
        webBackend.insertRule(testRule);
        webBackend.deleteRule(testRule.getId());
        assertNull(webBackend.getRule(testRule.getId()));
    }

    @Test
    public void testInsertRule() throws Exception {
        Rule testRule = new Rule("test_rule_1",10);
        webBackend.insertRule(testRule);
        assertEquals(webBackend.getRule(testRule.getId()), testRule);
    }

    @Test
    public void testGetGroupPermissionFromRuleAndGroup() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupOne");
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID()),testGroupPermission);
    }

    @Test
    public void testGetGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupOne");
        testGroupPermission = webBackend.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID());
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getId()),testGroupPermission);
    }

    @Test
    public void testGetGroupPermissions() throws Exception {
        List<GroupPermission> obtainedGroupPermissions = webBackend.getGroupPermissions();
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1","groupOne")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet1","groupTwo")));
        assertTrue(obtainedGroupPermissions.contains(new GroupPermission("ruleSet2","groupOne")));
    }

    @Test
    public void testDeleteGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet1","groupTwo");
        testGroupPermission = webBackend.getGroupPermission(testGroupPermission.getRuleID(), testGroupPermission.getUserGroupID());
        webBackend.deleteGroupPermission(testGroupPermission.getId());
        assertNull(webBackend.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID()));
    }

    @Test
    public void testInsertGroupPermission() throws Exception {
        GroupPermission testGroupPermission = new GroupPermission("ruleSet2","groupTwo");
        webBackend.insertGroupPermission(testGroupPermission);
        assertEquals(webBackend.getGroupPermission(testGroupPermission.getRuleID(),testGroupPermission.getUserGroupID()), testGroupPermission);
    }

    @Test
    public void testLoadAssignmentHistoryFromUserID() throws Exception {
        List<AssignmentHistory> history = webBackend.getUserAssignmentHistory("Callum2468");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }

    @Test
    public void testLoadAssignmentHistoryFromDeviceID() throws Exception {
        List<AssignmentHistory> history = webBackend.getDeviceAssignmentHistory("laptop01");

        assertEquals(history.get(0).getUserID(), "Callum2468");
        assertEquals(history.get(0).getDeviceID(), "laptop01");
        assertEquals(history.get(0).getReturnedByID(), "Aidan9876");
    }


}
