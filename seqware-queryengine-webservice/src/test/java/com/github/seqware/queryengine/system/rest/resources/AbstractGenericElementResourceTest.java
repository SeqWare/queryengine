package com.github.seqware.queryengine.system.rest.resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractGenericElementResourceTest {
  
  public AbstractGenericElementResourceTest() {
  }
  
  /*
  @BeforeClass
  public abstract void setUpClass();
  
  @AfterClass
  public abstract void tearDownClass();
  */
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  @Test
  public abstract void testGetClassName();
  
  @Test
  public abstract void testGetModelClass();
  
  @Test
  public abstract void testFeaturesRequest();

  @Test
  public abstract void testFeatureByIDRequest();
  
  @Test
  public abstract void testTaggedRequest();

  @Test
  public abstract void testTagsOfElementRequest();
  
  @Test
  public abstract void testUpateElement();
  
  @Test
  public abstract void testDeleteElement();
  
  @Test
  public abstract void testTagElement();
  
  @Test
  public abstract void testGetElements();
  
  @Test
  public abstract void testUpdateElementPermissions();
  
  @Test
  public abstract void testPermissionsOfElementRequest();
}
