package com.github.seqware.queryengine.system.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.seqware.queryengine.util.SeqWareIterable;

public class TagResourceTest extends AbstractGenericElementResourceTest {
  
  public TagResourceTest() {
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
   * Test of getClassName method, of class TagResource.
   */
  @Test
  public void testGetClassName() {
    System.out.println("getClassName");
    TagResource instance = new TagResource();
    String expResult = "";
    String result = instance.getClassName();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getModelClass method, of class TagResource.
   */
  @Test
  public void testGetModelClass() {
    System.out.println("getModelClass");
    TagResource instance = new TagResource();
    Class expResult = null;
    Class result = instance.getModelClass();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getElements method, of class TagResource.
   */
  @Test
  public void testGetElements() {
    System.out.println("getElements");
    TagResource instance = new TagResource();
    SeqWareIterable expResult = null;
    SeqWareIterable result = instance.getElements();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
}
