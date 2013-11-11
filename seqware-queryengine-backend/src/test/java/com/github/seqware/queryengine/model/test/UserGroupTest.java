package com.github.seqware.queryengine.model.test;

import com.github.seqware.queryengine.Constants;
import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import java.util.Date;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests of {@link com.github.seqware.queryengine.model.User} and {@link com.github.seqware.queryengine.model.Group}.
 *
 * @author dyuen
 * @version $Id: $Id
 * @since 0.13.3
 */
public class UserGroupTest {

    private static User a1, a2, a3;
    private static Group g1,g2; 

    /**
     * <p>setupTests.</p>
     */
    @BeforeClass
    public static void setupTests() {
//        Logger.getLogger(UserGroupTest.class.getName()).info( "@BeforeClass");
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        g1 = mManager.buildGroup().setName("Developers").setDescription("Group for Developers").build();
        g2 = mManager.buildGroup().setName("Variant-Developers").setDescription("Group for Developers").build();
        a1 = mManager.buildUser().setFirstName("Joe").setLastName("Smith").setEmailAddress("smith@googly.com").setPassword("password").build();
        a2 = mManager.buildUser().setFirstName("bev").setLastName("Smith").setEmailAddress("bev@googly.com").setPassword("password").build();
        a3 = mManager.buildUser().setFirstName("Tim").setLastName("Smith").setEmailAddress("tim@googly.com").setPassword("password").build();
        g1.add(a1, a2, a3);
        // persisting users and group to back-end
        mManager.flush();
        mManager.close();
    }

    /**
     * <p>testUserCreation.</p>
     */
    @Test
    public void testUserCreation() {
//        Logger.getLogger(UserGroupTest.class.getName()).info( "@Test");
        // check that Users are present match
        boolean b1 = false;
        boolean b2 = false;
        boolean b3 = false;
        for (User u : SWQEFactory.getQueryInterface().getUsers()) {
            if (u.equals(a1)) {
                b1 = true;
            } else if (u.equals(a2)) {
                b2 = true;
            } else if (u.equals(a3)) {
                b3 = true;
            }
        }
        Assert.assertTrue(b1 && b2 && b3);
    }
    
    /**
     * <p>testGroupCreation.</p>
     */
    @Test
    public void testGroupCreation() {
//       Logger.getLogger(UserGroupTest.class.getName()).info( "@Test");
        // check that Group are present match
        boolean b1 = false;
        for (Group u : SWQEFactory.getQueryInterface().getGroups()) {
            if (u.equals(g1)) {
                b1 = true;
            } 
        }
        Assert.assertTrue(b1);
    }

    /**
     * <p>testUserPasswordChanging.</p>
     */
    @Test
    public void testUserPasswordChanging(){
//        Logger.getLogger(UserGroupTest.class.getName()).info( "@Test");
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        String password1 = "ITMfL";
        User n1 = mManager.buildUser().setFirstName("Cheung").setLastName("Man-Yuk").setEmailAddress("cmy@googly.com").setPassword(password1).build();
        mManager.flush();
        User oldUser = n1;
        // check current User's password
        Assert.assertTrue(n1.checkPassword(password1));  
        String password2 = "2046";
        n1 = n1.toBuilder().setPassword(password2).build();
        if (Constants.TRACK_VERSIONING){
            n1.setPrecedingVersion(oldUser);
        }
        mManager.flush();
        
        /* FIXME: this is a pointless check since you're just seeing what the object
         * looks like in memory.  Why not try to pull it back fromt eh DB?  You'll
         * find that your db code creates a duplicate for this instad of an update
         */
        
        // check new current User's password
        Assert.assertTrue(n1.checkPassword(password2));  
        if (Constants.TRACK_VERSIONING){
            // check old User's password via Versionable interface
            Assert.assertTrue(n1.getPrecedingVersion().checkPassword(password1));  
            // check old User's password by re-retrieving it
            User oldN1 = SWQEFactory.getQueryInterface().getAtomBySGID(User.class, oldUser.getSGID());
            Assert.assertTrue(oldN1.checkPassword(password1));  
        }
    }
    
    @Test
    public void testUpdateUserAndCheckRowkey(){
        // this test should create a user, flush it, update, change, flush again, and then check the rowkey
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        User initialUser = mManager.buildUser().setFirstName("Iron").setLastName("Man").build();
        mManager.flush();
        User foundUser = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(initialUser.getSGID().getRowKey(), User.class);
        User builtUser = foundUser.toBuilder().setEmailAddress("I.am.iron.man@gmail.com").build();
        mManager.update(foundUser, builtUser);
        mManager.flush();
        User foundUser2 = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(initialUser.getSGID().getRowKey(), User.class);
        Assert.assertTrue("row key changed between initial flush and retrieval", initialUser.getSGID().getRowKey().equals(foundUser.getSGID().getRowKey()));
        Assert.assertTrue("row key changed between first retrieval flush and retrieval after update", foundUser.getSGID().getRowKey().equals(foundUser2.getSGID().getRowKey()));
        Assert.assertTrue("updated version has new data", foundUser2.getEmailAddress().equals("I.am.iron.man@gmail.com"));
    }
    
    @Test
    public void testUpdateGroupAndCheckRowkey(){
        // this test should create a user, flush it, update, change, flush again, and then check the rowkey
        CreateUpdateManager mManager = SWQEFactory.getModelManager();
        Group initialUser = mManager.buildGroup().setName("Avengers").build();
        mManager.flush();
        Group foundUser = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(initialUser.getSGID().getRowKey(), Group.class);
        Group builtUser = foundUser.toBuilder().setDescription("Dysfunctional").build();
        mManager.update(foundUser, builtUser);
        mManager.flush();
        Group foundUser2 = SWQEFactory.getQueryInterface().getLatestAtomByRowKey(initialUser.getSGID().getRowKey(), Group.class);
        Assert.assertTrue("row key changed between initial flush and retrieval", initialUser.getSGID().getRowKey().equals(foundUser.getSGID().getRowKey()));
        Assert.assertTrue("row key changed between first retrieval flush and retrieval after update", foundUser.getSGID().getRowKey().equals(foundUser2.getSGID().getRowKey()));
        Assert.assertTrue("updated version has new data", foundUser2.getDescription().equals("Dysfunctional"));
    }
}
