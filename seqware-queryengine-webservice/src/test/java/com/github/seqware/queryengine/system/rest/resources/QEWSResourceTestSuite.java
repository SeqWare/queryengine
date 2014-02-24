package com.github.seqware.queryengine.system.rest.resources;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        // Comma-separated testing classes and queries
        /*net.sourceforge.seqware.webservice.resources.tables.DummyExperimentIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentLibraryDesignResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentSpotDesignReadSpecResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.StudyResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SequencerRunResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SampleIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowRunResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.IusResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ProcessResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SampleResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.RootSampleResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileChildWorkflowRunsResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ProcessIDTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.FileIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.StudyIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.LaneIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.WorkflowRunIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.IusIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.SequencerRunIDResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.ExperimentResourceTest.class,
        net.sourceforge.seqware.webservice.resources.tables.LaneResourceTest.class,
        net.sourceforge.seqware.webservice.resources.queries.SampleIdFilesResourceTest.class,
    //        net.sourceforge.seqware.webservice.resources.queries.SequencerRunIdFilesResourceTest.class,
        net.sourceforge.seqware.webservice.resources.queries.StudyIdSamplesResourceTest.class,
        net.sourceforge.seqware.webservice.resources.queries.RunWorkflowResourceTest.class,
        net.sourceforge.seqware.common.metadata.MetadataWSTest.class,
        net.sourceforge.seqware.common.metadata.MetadataDBTest.class*/
})
public class QEWSResourceTestSuite {

    //protected SessionFactory sessionFactory = null;

    @BeforeClass
    public static void setUpClass() throws Exception {
      //Setup the DB and Wrappers
      //  BasicTestDatabaseCreatorWrapper.resetDatabaseWithUsers();
        //JndiDatasourceCreator.create();
//        SeqWareWebServiceMain.main(null);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
      //Disable all testing tables, drop them
//        SeqWareWebServiceMain.stop();
        //BasicTestDatabaseCreatorWrapper.dropDatabase();
    }
//    @Before
//    public void setUp() {
//        sessionFactory = BeanFactory.getSessionFactoryBean();
//        Session session = SessionFactoryUtils.getSession(sessionFactory, true);
//        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
//    }
//
//    @After
//    public void tearDown() {
//        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
//        SessionFactoryUtils.closeSession(sessionHolder.getSession());
//    }
}

