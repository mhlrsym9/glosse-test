package com.transparent.glossary.gui;

import com.transparent.glossary.TreeModelBuilder;
import com.transparent.glossary.processor.StopWordProcessingType;
import java.io.File;
import javax.swing.tree.TreeNode;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
/**
 * Automated unit test of the Mediator object.
 */
public class MediatorUnitTest
{
    @Test
    public void givenGoodData_start_displaysView() throws Exception
    {
        final View mock = mock( View.class );
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy strategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( mock, new Model(), strategy, builder );
        sut.setEngine( engine );
        sut.start();

        verify( mock ).display();
    }

    @Test
    public void givenGoodEvent_handleShutdownRequest_callsExitStrategy() throws Exception
    {
        final View view = mock( View.class );
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy mock = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( view, new Model(), mock, builder );
        sut.setEngine( engine );
        sut.handleShutdownRequest();

        verify( mock ).shutdown();
    }

    private Mediator createSubjectUnderTest( final View aMock, final Model aModel )
    {
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock(ExitStrategy.class);
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator mediator = new Mediator( aMock, aModel, exitStrategy, builder );
        mediator.setEngine(engine);
        return mediator;
    }

    private Mediator createSubjectUnderTest( final Model model )
    {
        final View view = mock( View.class );
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock(ExitStrategy.class);
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator mediator = new Mediator( view, model, exitStrategy, builder );
        mediator.setEngine(engine);
        return mediator;
    }

    @Test
    public void givenGoodFile_handleContentDirectorySelected_fileIsStoredAndSelectionIsDisplayed() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest(mock, model);
        final File file = new File( "." );
        sut.handleContentDirectorySelected(file);

        assertThat(model.getTheSourcePath(), is(equalTo(file.getPath())));
        verify( mock ).displayContentDirectory(model.getTheSourcePath());
        verify( mock ).enableGeneration(false);
    }

    @Test
    public void givenGoodFile_handleDestinationDirectorySelected_fileIsStoredAndSelectionIsDisplayed() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( mock, model );
        final File file = new File( "." );
        sut.handleDestinationDirectorySelected(file);

        assertThat(model.getDestinationPath(), is(equalTo(file.getPath())));
        verify( mock ).displayDestinationDirectory(model.getDestinationPath());
        verify( mock ).enableGeneration(false);
    }

    @Test
    public void givenValidMessage_handleNormalTermination_tellsViewToDisplayMessage() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( mock, model );
        final File file = new File( "." );
        sut.handleNormalTermination(file.getPath(), file.getPath());

        verify( mock ).displayCompletionMessage(anyString());
        verify( mock ).displayGlossary(org.mockito.Matchers.<TreeNode>anyObject());
    }

    @Test
    public void givenValidMessage_handleAbnormalTermination_tellsViewToDisplayMessage() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( mock, model );
        sut.handleAbnormalTermination("It's all bad.");

        verify( mock ).displayError(anyString());
    }

    @Test
    public void givenGoodFile_handleConfigurationFileSelected_fileIsStoredAndSelectionIsDisplayed() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( mock, model );
        final File file = new File( "." );
        sut.handleConfigurationFileSelected( file );

        assertThat(model.getCustomConfigurationPath(), is(equalTo(file.getPath())));
        verify( mock ).displayConfigurationFile(model.getCustomConfigurationPath());
    }

    @Test
    public void givenGoodContentAndDestinationFolders_handleGenerateGlossarySelected_engineIsCalled() throws Exception
    {
        final View view = mock( View.class );
        final Model model = new Model();
        final ProcessingEngine mock = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( view, model, exitStrategy, builder );
        sut.setEngine( mock );
        final File file = new File( "." );
        sut.handleContentDirectorySelected( file );
        sut.handleDestinationDirectorySelected( file );
        final String filePrefix = "Tara";
        final String l1Suffix = "Devan";
        final String l2Suffix = "Logan";
        sut.handleGenerateGlossarySelected(filePrefix, l1Suffix, l2Suffix);

        verify( mock ).generateFiles(model);
        assertThat(model.getFilePrefix(), is(equalTo(filePrefix)));
        assertThat(model.getL1Suffix(), is(equalTo(l1Suffix)));
        assertThat(model.getL2Suffix(), is(equalTo(l2Suffix)));
    }

    @Test
    public void givenEmptyContentFolderPath_handleGenerateGlossarySelected_displaysError() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( mock, model, exitStrategy, builder );
        sut.setEngine(engine);
        sut.handleContentDirectorySelected(new File(""));
        sut.handleDestinationDirectorySelected(new File("."));
        sut.handleGenerateGlossarySelected(null, null, null);

        verify( mock ).displayError(anyString());
    }

    @Test
    public void givenGoodContentFolderAndGoodDestinationFolderAndGoodInternalListNameMapFile_handleGenerateGlossarySelected_engineIsCalled() throws Exception
    {
        final View view = mock( View.class );
        final Model model = new Model();
        final ProcessingEngine mock = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( view, model, exitStrategy, builder );
        sut.setEngine( mock );
        final File file = new File( "." );
        sut.handleContentDirectorySelected( file );
        sut.handleDestinationDirectorySelected( file );
        sut.handleExtractionApproachSelected("Internal");
        sut.handleInternalListNameMapChange(true);
        sut.handleInternalListNameMapFileSelected(new File ("." + File.separator + "testdata" + File.separator + "filters" + File.separator + "grammar.xml"));
        sut.handleGenerateGlossarySelected(null, null, null);

        verify( mock ).generateFiles(model);
    }

    @Test
    public void givenEmptyInternalListNameMapFile_handleGenerateGlossarySelected_displaysError() throws Exception
    {
        final View mock = mock( View.class );
        final Model model = new Model();
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( mock, model, exitStrategy, builder );
        sut.setEngine(engine);
        final File file = new File( "." );
        sut.handleContentDirectorySelected(file);
        sut.handleDestinationDirectorySelected(file);
        sut.handleExtractionApproachSelected("Internal");
        sut.handleInternalListNameMapChange(true);
        sut.handleInternalListNameMapFileSelected(new File(""));
        sut.handleGenerateGlossarySelected(null, null, null);

        verify( mock ).displayError(anyString());
    }

    @Test
    public void givenGoodState_handleExtractionApproachChange_updatesTheModel() throws Exception
    {
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( model );

        sut.handleExtractionApproachSelected("Original");
        assertThat(model.supportMarsoc(), is(equalTo(false)));
        assertThat( model.supportInternal(), is( equalTo( false ) ) );

        sut.handleExtractionApproachSelected("MARSOC");
        assertThat(model.supportMarsoc(), is(equalTo(true)));
        assertThat( model.supportInternal(), is( equalTo( false ) ) );

        sut.handleExtractionApproachSelected("Internal");
        assertThat(model.supportMarsoc(), is(equalTo(false)));
        assertThat( model.supportInternal(), is( equalTo( true ) ) );
    }

    @Test
    public void givenValidMode_handleStopWordSelected_updatesTheModel() throws Exception
    {
        final Model model = new Model();
        final Mediator sut = createSubjectUnderTest( model );

        final String mode = "All";
        sut.handleStopWordSelected( mode );
        assertThat( model.getStopWordMode(), is( equalTo( StopWordProcessingType.ALL ) ) );
    }

    @Test
    public void givenValidData_handleResetSelected_resetsState() throws Exception
    {
        final Model model = mock( Model.class );
        final View view = mock( View.class );
        final ProcessingEngine engine = mock( ProcessingEngine.class );
        final ExitStrategy exitStrategy = mock( ExitStrategy.class );
        final TreeModelBuilder builder = mock( TreeModelBuilder.class );
        final Mediator sut = new Mediator( view, model, exitStrategy, builder );
        sut.setEngine( engine );

        sut.handleResetSelected();

        verify( view ).reset();
        verify( engine ).reset();
        verify( engine ).reset();
    }
}
