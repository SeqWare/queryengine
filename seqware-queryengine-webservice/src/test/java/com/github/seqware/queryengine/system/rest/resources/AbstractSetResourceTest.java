package com.github.seqware.queryengine.system.rest.resources;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public abstract class AbstractSetResourceTest extends AbstractGenericElementResourceTest {
  
  public AbstractSetResourceTest() {
  }
  
  @BeforeClass
  public abstract static void setUpClass();
  
  @AfterClass
  public abstract static void tearDownClass();
  
  @Before
  public abstract void setUp();
  
  @After
  public abstract void tearDown();

  @Test
  public abstract void testAddSet();
}
