package uk.ac.bris.cs.rfideasalreadytaken.lumberjack;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.DatabaseTesting;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.web.WebBackend;
import uk.ac.bris.cs.rfideasalreadytaken.lumberjack.database.data.*;

import static org.junit.Assert.*;

@WebAppConfiguration
@SpringBootTest
@TestPropertySource({ "classpath:${envTarget:config/testdatabase}.properties" })
@RunWith(SpringRunner.class)
public class FrontEndDatabaseManagerTests {

    @Autowired
    private DatabaseTesting databaseUtility;

    @Autowired
    private WebBackend frontEndDatabaseManager;

    @Before
    public void setupDatabase() throws Exception {
        databaseUtility.insertTestCases();
    }

    /*

    @Test
    public void testInsertUser() throws Exception {
        User testUser = new User();
        testUser.setCanRemove(true);
        testUser.setDeviceLimit(1);
        testUser.setDevicesRemoved(0);
        testUser.setId("test_user");
        testUser.setGroupId("groupOne");
        testUser.setScanValue("12345");

        frontEndDatabaseManager.insertUser(testUser);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
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

        frontEndDatabaseManager.insertUsers(list);
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
        assertEquals(frontEndDatabaseManager.getUser(testUser2.getId()), testUser2);
    }

    @Test
    public void testGetUserNotInDatabase() throws Exception {
        User testUser = new User();
        testUser.setId("definitely_not_in_there_test_user");
        assertNull(frontEndDatabaseManager.getUser(testUser.getId()));
    }

    @Test
    public void testGetUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()),testUser);
    }

    @Test
    public void testGetUsers() throws Exception {
        List<User> users = frontEndDatabaseManager.getUsers();
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

        frontEndDatabaseManager.editUser("Aidan9876",testUser);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()), testUser);
    }

    @Test
    public void testDeleteUser() throws Exception {
        User testUser = new User("Aidan9876", "1314831486", 2, 0, true, "groupOne");
        frontEndDatabaseManager.deleteUser(testUser.getId());
        assertNull(frontEndDatabaseManager.getUser(testUser.getId()));
    }

    @Test
    public void testResetUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("Betty1248", "457436545", 3, 1, true, "groupTwo"));
        frontEndDatabaseManager.resetUsers();
        assertEquals(frontEndDatabaseManager.getUsers(),users);
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

        frontEndDatabaseManager.insertDevice(testDevice);

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
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

        frontEndDatabaseManager.insertDevices(list);
        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
        assertEquals(frontEndDatabaseManager.getDevice(testDevice2.getId()), testDevice2);
    }

    @Test
    public void testGetDeviceNotInDatabase() throws Exception {
        Device testDevice = new Device();
        testDevice.setId("definitely_not_in_there_test_device");
        assertNull(frontEndDatabaseManager.getDevice(testDevice.getId()));
    }

    @Test
    public void testGetDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()),testDevice);
    }

    @Test
    public void testGetDevices() throws Exception {
        List<Device> obtainedDevices = frontEndDatabaseManager.getDevices();
        assertTrue(obtainedDevices.contains(new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2")));
        assertTrue(obtainedDevices.contains(new Device("laptop03", "93482364723648725", "laptop", false, false, "ruleSet1")));
        assertTrue(obtainedDevices.contains(new Device("camera01", "03457237295732925", "camera", true, false, "ruleSet2")));
    }

    @Test
    public void testDeleteDevice() throws Exception {
        Device testDevice = new Device("laptop01", "36109839730967812", "laptop", true, false, "ruleSet1");
        frontEndDatabaseManager.deleteDevice(testDevice.getId());
        assertNull(frontEndDatabaseManager.getDevice(testDevice.getId()));
    }

    @Test
    public void testResetDevices() throws Exception {
        List<Device> devices = new ArrayList<>();
        devices.add(new Device("laptop02", "23482364326842334", "laptop", true, true, "ruleSet2"));
        frontEndDatabaseManager.resetDevices();
        assertEquals(frontEndDatabaseManager.getDevices(),devices);
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

        frontEndDatabaseManager.editDevice("laptop01",testDevice);

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()), testDevice);
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

        frontEndDatabaseManager.insertDevice(testDevice);
        frontEndDatabaseManager.setDeviceType(testDevice,"tablet");

        assertEquals(frontEndDatabaseManager.getDevice(testDevice.getId()).getType(), "tablet");
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

        frontEndDatabaseManager.insertUser(testUser);
        frontEndDatabaseManager.changeUserGroup(testUser,new UserGroup("groupTwo"));

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getGroupId(), "groupTwo");
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

        frontEndDatabaseManager.insertUser(testUser);
        frontEndDatabaseManager.setUserMaxDevices(testUser,2);

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getDeviceLimit(), 2);
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

        frontEndDatabaseManager.insertUsers(list);
        frontEndDatabaseManager.changeUsersGroup(list,new UserGroup("groupTwo"));

        assertEquals(frontEndDatabaseManager.getUser(testUser.getId()).getGroupId(), "groupTwo");
        assertEquals(frontEndDatabaseManager.getUser(testUser2.getId()).getGroupId(), "groupTwo");
    }

    */

}
