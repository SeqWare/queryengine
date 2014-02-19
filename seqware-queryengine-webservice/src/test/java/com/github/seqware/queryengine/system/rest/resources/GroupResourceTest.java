/*
 * Copyright (C) 2013 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.seqware.queryengine.system.rest.resources;

import com.github.seqware.queryengine.model.Group;
import com.github.seqware.queryengine.model.User;
import com.github.seqware.queryengine.util.SeqWareIterable;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author boconnor
 */
public class GroupResourceTest {
  
  public GroupResourceTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of getClassName method, of class GroupResource.
   */
  @Test
  public void testGetClassName() {
    System.out.println("getClassName");
    GroupResource instance = new GroupResource();
    String expResult = "";
    String result = instance.getClassName();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getModelClass method, of class GroupResource.
   */
  @Test
  public void testGetModelClass() {
    System.out.println("getModelClass");
    GroupResource instance = new GroupResource();
    Class expResult = null;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getElements method, of class GroupResource.
   */
  @Test
  public void testGetElements() {
    System.out.println("getElements");
    GroupResource instance = new GroupResource();
    SeqWareIterable expResult = null;
    SeqWareIterable result = instance.getElements();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of featureByIDRequest method, of class GroupResource.
   */
  @Test
  public void testFeatureByIDRequest() throws Exception {
    System.out.println("featureByIDRequest");
    String sgid = "";
    GroupResource instance = new GroupResource();
    Response expResult = null;
    Response result = instance.featureByIDRequest(sgid);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of updateElement method, of class GroupResource.
   */
  @Test
  public void testUpdateElement() {
    System.out.println("updateElement");
    String sgid = "";
    Group user = null;
    GroupResource instance = new GroupResource();
    Response expResult = null;
    Response result = instance.updateElement(sgid, user);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of addElement method, of class GroupResource.
   */
  @Test
  public void testAddElement() {
    System.out.println("addElement");
    String sgid = "";
    User element = null;
    GroupResource instance = new GroupResource();
    Response expResult = null;
    Response result = instance.addElement(sgid, element);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of addSet method, of class GroupResource.
   */
  @Test
  public void testAddSet() {
    System.out.println("addSet");
    Group set = null;
    GroupResource instance = new GroupResource();
    Response expResult = null;
    Response result = instance.addSet(set);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
}
