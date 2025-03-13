package com.transparent.glossary.gui;

import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.transparent.glossary.gui.builder.GridBagConstraintsBuilder;
import com.transparent.glossary.gui.builder.JButtonBuilder;
import com.transparent.glossary.gui.builder.JCheckBoxBuilder;
import com.transparent.glossary.gui.builder.JEditorPaneBuilder;
import com.transparent.glossary.gui.builder.JFileChooserBuilder;
import com.transparent.glossary.gui.builder.JLabelBuilder;
import com.transparent.glossary.gui.builder.JMenuBarBuilder;
import com.transparent.glossary.gui.builder.JMenuBuilder;
import com.transparent.glossary.gui.builder.JPanelBuilder;
import com.transparent.glossary.gui.builder.JRadioButtonBuilder;
import com.transparent.glossary.gui.builder.JTabbedPaneBuilder;
import com.transparent.glossary.gui.builder.JTextAreaBuilder;
import com.transparent.glossary.gui.builder.JTextFieldBuilder;
import com.transparent.glossary.gui.builder.JTreeBuilder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;

/**
 * Passive View pattern, implemented in Swing. No business logic in this object -- that is always handled by the
 * Mediator.
 */
public class SwingView extends JFrame implements View
{
    /**
     * How wide the primary panel should be.
     */
    private static final int PRIMARY_PANEL_WIDTH = 785;

    /**
     * Tool tip for the L2 suffix controls.
     */
    private static final String L2_TIP = "The string that should be added to the end of the L2 file name, eg prefix_L2.xml. If blank, 'L2' is used by default.";

    /**
     * Tool tip for the L1 suffix controls.
     */
    private static final String L1_TIP = "The string that should be added to the end of the L1 file name, eg prefix_L1.xml. If blank, 'L1' is used by default.";

    /**
     * Tool tip for the file prefix controls.
     */
    private static final String PREFIX_TIP = "Specifies the string to prepend to the name of any files that are generated. If blank, 'glossary' is used by default.";

    /**
     * Message logger to use.
     */
    private final Logger theLogger = Logger.getLogger( SwingView.class );

    /**
     * Who to call when a shutdown has been requested.
     */
    private ShutdownHandler theShutdownHandler;

    /**
     * Simplifies the building of text fields.
     */
    private final JTextFieldBuilder theTextFieldBuilder = new JTextFieldBuilder().withColumnCount( 40 );

    /**
     * Displays the location of the content directory to the user.
     */
    private final JTextField theContentDirectory = theTextFieldBuilder.build();

    /**
     * Displays the location of the destination directory to the user.
     */
    private final JTextField theDestinationDirectory = theTextFieldBuilder.build();

    /**
     * Displays the location of the configuration file to the user.
     */
    private final JTextField theConfigurationFile = theTextFieldBuilder.build();

    /**
     * What the user selected to use for the generated file names.
     */
    private final JTextField theFilePrefix = theTextFieldBuilder.withTip( PREFIX_TIP ).build();

    /**
     * What the user selected to use as a suffix for the L1 file.
     */
    private final JTextField theL1Suffix = theTextFieldBuilder.withTip( L1_TIP ).build();

    /**
     * What the user selected to use as a suffix for the L2 file.
     */
    private final JTextField theL2Suffix = theTextFieldBuilder.withTip( L2_TIP ).build();

    /**
     * What the user selected to use as a internal list name map file.
     */
    private final JTextField theInternalListNameMapFile = theTextFieldBuilder.build();

    /**
     * Handles the selection of the destination directory.
     */
    private final DestinationDirectoryBrowseAction theDestinationDirectoryAction = new DestinationDirectoryBrowseAction( "Browse", "Directory where you would like the generated files to be placed." );

    /**
     * Handles the selection of the content directory.
     */
    private final ContentDirectoryBrowseAction theContentDirectoryAction = new ContentDirectoryBrowseAction( "Browse", "Directory where the root of your content is located." );

    /**
     * Handles the selection of the configuration file.
     */
    private final ConfigurationFileBrowseAction theConfigurationFileBrowseAction = new ConfigurationFileBrowseAction( "Browse", "Where your custom configuration file is located." );

    /**
     * Handles the selection of the internal list map file.
     */
    private final InternalListNameMapFileBrowseAction theInternalListNameMapFileBrowseAction = new InternalListNameMapFileBrowseAction("Browse", "Where your custom internal list name map file is located. NOT PRESENTLY USED!");

    /**
     * Handles the toggling of the MARSOC checkbox.
     */
    private final MarsocAction theMarsocAction = new MarsocAction( "MARSOC Support" );

    /**
     * Handles the case when the original extraction approach selection has been made.
     */
    private final ExtractionApproachAction theOriginalExtractionApproachAction = new ExtractionApproachAction("Original", "No POS, use folder name to generate reference.", false);

    /**
     * Handles the case when the MARSOC extraction approach selection has been made.
     */
    private final ExtractionApproachAction theMarsocExtractionApproachAction = new ExtractionApproachAction("MARSOC", "POS and reference generated from MARSOC B4X file name.", false);

    /**
     * Handles the case when the internal extraction approach selection has been made.
     */
    private final ExtractionApproachAction theInternalExtractionApproachAction = new ExtractionApproachAction("Internal" , "POS and reference extracted from within B4X file", true);

    /**
     * Handles the toggling of the Use Internal List Name Map checkbox.
     */
    private final InternalListNameMapAction theInternalListNameMapAction = new InternalListNameMapAction("Use Internal List Name Map?");

    /**
     * Handles the case when the All stop word type selection has been made.
     */
    private final StopWordsAction theAllStopWordsAction = new StopWordsAction( "All", "Stop words will be applied to both words and phrases.", true );

    /**
     * Handles the case when the Words stop word type selection has been made.
     */
    private final StopWordsAction theWordsStopWordsAction = new StopWordsAction( "Words", "Stop words will be applied only to words and not phrases.", false );

    /**
     * Handles the case when the Phrases stop word type selection has been made.
     */
    private final StopWordsAction thePhrasesStopWordsAction = new StopWordsAction( "Phrases", "Stop words will be applied only to phrases and not words.", false );

    /**
     * Handles the case when the None stop word type selection has been made.
     */
    private final StopWordsAction theNoneStopWordsAction = new StopWordsAction( "None", "Stop words will not be applied to any phrases or words.", false );

    /**
     * Called when the Reset menu selection is picked.
     */
    private final ResetAction theResetAction = new ResetAction( "Reset" );

    /**
     * Fires when the Go button is clicked.
     */
    private final GenerateAction theGenerationAction = new GenerateAction();

    /**
     * Ensures extraction approach radio button behavior.
     */
    private final ButtonGroup theExtractionApproachButtonGroup = new ButtonGroup();

    /**
     * Ensures stop words radio button behavior.
     */
    private final ButtonGroup theStopWordsButtonGroup = new ButtonGroup();

    /**
     * Simplifies building buttons.
     */
    private final JButtonBuilder theButtonBuilder = new JButtonBuilder();

    /**
     * Button that controls the generation of glossary files -- the Go button.
     */
    private final JButton theGenerateButton = theButtonBuilder.withAction( theGenerationAction ).build();

    /**
     * Button that allows the user to browse for the internal list name map file.
     */
    private final JButton theInternalListNameMapBrowseButton = theButtonBuilder.withAction(theInternalListNameMapFileBrowseAction).build();

    /**
     * Simplifies building check boxes.
     */
    private final JCheckBoxBuilder theCheckBoxBuilder = new JCheckBoxBuilder();

    /**
     * Check box that controls MARSOC support.
     */
    private final JCheckBox theMarsocCheckBox = theCheckBoxBuilder.withAction( theMarsocAction ).build();

    /**
     * Check box that controls internal list name map support.
     */
    private final JCheckBox theInternalListNameMapCheckBox = theCheckBoxBuilder.withAction(theInternalListNameMapAction).build();

    /**
     * Simplifies the building of text areas.
     */
    private final JTextAreaBuilder theTextAreaBuilder = new JTextAreaBuilder().withArea( 12, 80 ).withEditable( false );

    /**
     * Text area that holds messages intended for developers.
     */
    private final JTextArea theDeveloperMessages = theTextAreaBuilder.build();

    /**
     * Text area that holds messages intended for the user.
     */
    private final JTextArea theUsersMessages = theTextAreaBuilder.build();

    /**
     * The button model associated with the default extraction approach selection.
     */
    private ButtonModel theDefaultExtractionApproachButtonModel;

    /**
     * The button model associated with the default stop word selection.
     */
    private ButtonModel theDefaultStopWordButtonModel;

    /**
     * Simplifies building trees.
     */
    private final JTreeBuilder theTreeBuilder = new JTreeBuilder().withToolTips( true );

    /**
     * Tree that displays the generated glossary data.
     */
    private final JTree theGlossary = theTreeBuilder.withRenderer( new GlossaryRenderer() ).build();

    /**
     * Simplifies building of panels.
     */
    private final JPanelBuilder thePanelBuilder = new JPanelBuilder();

    /**
     * Simplifies building of tabbed panes.
     */
    private final JTabbedPaneBuilder theTabBuilder = new JTabbedPaneBuilder().withTopTabs().withScroller( true );

    /**
     * Simplifies the building of extraction approach radio buttons.
     */
    private final JRadioButtonBuilder theExtractionApproachRadioButtonBuilder = new JRadioButtonBuilder().withButtonGroup(theExtractionApproachButtonGroup);

    /**
     * Simplifies the building of stop words radio buttons.
     */
    private final JRadioButtonBuilder theStopWordsRadioButtonBuilder = new JRadioButtonBuilder().withButtonGroup(theStopWordsButtonGroup);

    /**
     * Simplifies building of labels.
     */
    private final JLabelBuilder theLabelBuilder = new JLabelBuilder();

    /**
     * Simplifies building file choosers.
     */
    private final JFileChooserBuilder theChooserBuilder = new JFileChooserBuilder().withFileSystemView( FileSystemView.getFileSystemView() );

    public SwingView( final String title,
                      final int width,
                      final int height,
                      final LookAndFeel lookAndFeel ) throws HeadlessException
    {
        super(title);

        addWindowListener( new CustomWindowAdapter() );
        setWindowSize(width, height);
        installApplicationIcon();
        installMenu();
        installLookAndFeel( lookAndFeel );

        //thePanelBuilder.withRandomBackgroundColor( true );

        buildMainContent();
        centerOnScreen(this);
        customizeGenerateButton();
        composeWindowTitle();
    }

    private void buildMainContent()
    {
        getContentPane().setLayout(new GridBagLayout());
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();

        builder.withFill( GridBagConstraints.HORIZONTAL ).withAnchor(GridBagConstraints.FIRST_LINE_START);

        getContentPane().add(constructRequiredPanel(), builder.withLocation(0, 0).withLowWeights().build());
        getContentPane().add(constructOptionalPanel(), builder.withLocation(0, 1).withLowWeights().build());
        getContentPane().add(constructGeneratePanel(), builder.withLocation(0, 2).withFill(GridBagConstraints.BOTH).withHighWeights().build());
    }

    private void setWindowSize( final int width, final int height )
    {
        final Dimension size = new Dimension( width, height );
        setSize(size);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    private void customizeGenerateButton()
    {
        theGenerateButton.setEnabled(false);
        theGenerateButton.setBorderPainted(false);
    }

    private void composeWindowTitle()
    {
        try
        {
            final String version = readProperty( "/version.properties", "version", "0" );
            setTitle(  new StringBuilder("Gloss-E v").append(version).toString()  );
        }
        catch( IOException e )
        {
            theLogger.warn( "Unable to load the version properties file.", e );
        }
    }


    private String readProperty( final String propertyFileName, final String property, final String defaultValue ) throws IOException
    {
        final Properties versionProperties = new Properties();
        versionProperties.load(SwingView.class.getResourceAsStream(propertyFileName));
        return versionProperties.getProperty( property, defaultValue );
    }

    private void installLookAndFeel( final LookAndFeel lookAndFeel )
    {
        try
        {
            UIManager.put( "ClassLoader", LookUtils.class.getClassLoader() );
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch( Exception e )
        {
            theLogger.warn( "Problem loading look and feel.", e );
            try
            {
                UIManager.put( "ClassLoader", Plastic3DLookAndFeel.class.getClassLoader() );
                UIManager.setLookAndFeel( new Plastic3DLookAndFeel() );
            }
            catch( UnsupportedLookAndFeelException e1 )
            {
                theLogger.warn( "Problem loading look and feel.", e1 );
            }
        }
    }

    private void installApplicationIcon()
    {
        try
        {
            final Image image = loadImageFromClasspath( "/images/010288-3d-glossy-blue-orb-icon-animals-animal-spider1.png" );
            setIconImage(image);
        }
        catch( IOException e )
        {
            theLogger.warn( "Unable to install application icon.", e );
        }
    }

    private Image loadImageFromClasspath( final String imagePath ) throws IOException
    {
        final InputStream inputStream = SwingView.class.getResourceAsStream( imagePath );
        return ImageIO.read( inputStream );
    }

    /**
     * Locates the given component on the screen's center.
     */
    private void centerOnScreen( final Component component )
    {
        final Dimension paneSize = component.getSize();
        final Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation( (screenSize.width - paneSize.width) / 2, (screenSize.height - paneSize.height) / 2 );
    }


    @Override
    public void registerContentSelectionHandler( final ContentDirectorySelectionHandler handler )
    {
        theContentDirectoryAction.setTheHandler(handler);
    }

    @Override
    public void registerDestinationSelectionHandler( final DestinationDirectorySelectionHandler handler )
    {
        theDestinationDirectoryAction.setTheHandler(handler);
    }

    @Override
    public void registerConfigurationFileSelectionHandler( final ConfigurationFileSelectionHandler handler )
    {
        theConfigurationFileBrowseAction.setTheHandler( handler );
    }

    @Override
    public void registerInternalListNameMapFileSelectionHandler(final InternalListNameMapFileSelectionHandler handler)
    {
        theInternalListNameMapFileBrowseAction.setTheHandler(handler);
    }

    @Override
    public void registerGlossaryGenerationHandler( final GlossaryGenerationHandler handler )
    {
        theGenerationAction.setTheHandler( handler );
    }

    @Override
    public void registerMarsocSelectionHandler( final MarsocSelectionHandler handler )
    {
        theMarsocAction.setTheHandler( handler );
    }

    @Override
    public void registerInternalListNameMapSelectionHandler(final InternalListNameMapSelectionHandler handler)
    {
        theInternalListNameMapAction.setTheHandler(handler);
    }

    @Override
    public void registerExtractionApproachSelectionHandler(final ExtractionApproachSelectionHandler handler)
    {
        theOriginalExtractionApproachAction.setHandler(handler);
        theMarsocExtractionApproachAction.setHandler(handler);
        theInternalExtractionApproachAction.setHandler(handler);
    }

    @Override
    public void registerStopWordSelectionHandler( final StopWordSelectionHandler handler )
    {
        theAllStopWordsAction.setHandler( handler );
        theWordsStopWordsAction.setHandler( handler );
        thePhrasesStopWordsAction.setHandler( handler );
        theNoneStopWordsAction.setHandler(handler);
    }

    @Override
    public void registerResetSelectionHandler( final ResetSelectionHandler handler )
    {
        theResetAction.setHandler(handler);
    }

    @Override
    public void displayContentDirectory( final String path )
    {
        theContentDirectory.setText(path);
    }

    @Override
    public void displayDestinationDirectory( final String path )
    {
        theDestinationDirectory.setText(path);
    }

    @Override
    public void displayConfigurationFile( final String path )
    {
        theConfigurationFile.setText( path );
    }

    @Override
    public void displayInternalListNameMapFile(final String name)
    {
        theInternalListNameMapFile.setText(name);
    }

    @Override
    public void displayError( final String message )
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void displayCompletionMessage( final String message )
    {
        JOptionPane.showMessageDialog( this, message, "Success", JOptionPane.INFORMATION_MESSAGE );
        emitBeep();
    }

    private void emitBeep()
    {
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void enableGeneration( final boolean state )
    {
        theGenerateButton.setEnabled(state);
    }

    @Override
    public void enableInternalListNameMapCheckBox(final boolean state) {theInternalListNameMapCheckBox.setEnabled(state);}

    @Override
    public void enableInternalListNameMapBrowseButton(final boolean state) {theInternalListNameMapBrowseButton.setEnabled(state);}

    @Override
    public void displayGlossary( final TreeNode root )
    {
        theGlossary.setModel(new DefaultTreeModel(root));
    }

    @Override
    public void reset()
    {
        theContentDirectory.setText( null );
        theDestinationDirectory.setText( null );
        theConfigurationFile.setText( null );
        theFilePrefix.setText( null );
        theL1Suffix.setText(null);
        theL2Suffix.setText(null);
        theMarsocCheckBox.setSelected(false);
        theUsersMessages.setText(null);
        theDeveloperMessages.setText(null);
        theGenerateButton.setEnabled(false);
        theExtractionApproachButtonGroup.setSelected(theDefaultExtractionApproachButtonModel, true);
        theInternalListNameMapCheckBox.setSelected(false);
        theInternalListNameMapCheckBox.setEnabled(true);
        theInternalListNameMapBrowseButton.setEnabled(false);
        theInternalListNameMapFile.setText(null);
        theStopWordsButtonGroup.setSelected(theDefaultStopWordButtonModel, true);
    }

    private JPanel constructRequiredPanel()
    {
        final JPanel panel = thePanelBuilder.withTitledBorder( "Required" ).withSpecificSize( PRIMARY_PANEL_WIDTH, 80 ).build();
        constructContentFolderControl(panel, 0);
        constructDestinationFolderControl(panel, 1);
        return panel;
    }

    private JPanel constructOptionalPanel()
    {
        final JPanel panel = thePanelBuilder.withTitledBorder( "Optional" ).withSpecificSize( PRIMARY_PANEL_WIDTH, 260 ).build();

        constructConfigurationFileControl(panel, 0);
        constructFilePrefixControl(panel, 1);
        constructL1SuffixControl(panel, 2);
        constructL2SuffixControl(panel, 3);
        constructOptionalPanelFourthRow(panel, 4);
        return panel;
    }

    private JPanel constructGeneratePanel()
    {
        final JPanel panel = thePanelBuilder.withTitledBorder( "Generate Glossary Files" ).withSpecificSize( PRIMARY_PANEL_WIDTH, 240 ).build();

        theTabBuilder.withTab("User Messages", theUsersMessages, "Normal operational messages");
        theTabBuilder.withTab("Developer Messages", theDeveloperMessages, "Error and developers messages");
        theTabBuilder.withTab("Glossary Viewer", theGlossary, "Visualization of the generated content");
        final JTabbedPane tabbedPane = theTabBuilder.build();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        panel.add(tabbedPane, builder.withLocation(0, 0).withFill(GridBagConstraints.BOTH).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.HIGH).build());
        panel.add(theGenerateButton, builder.withLocation(0, 1).withFill(GridBagConstraints.NONE).withLowWeights().withAnchor(GridBagConstraints.PAGE_END).build());
        return panel;
    }

    private void constructOptionalPanelFourthRow(final JPanel panel, final int row)
    {
        final JPanel fourthRowPanel = thePanelBuilder.withTitledBorder(null).withSpecificSize(PRIMARY_PANEL_WIDTH, 130).build();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill(GridBagConstraints.NONE).withAnchor(GridBagConstraints.LINE_START);
        constructExtractionApproachControl(fourthRowPanel);
        constructStopWordControl(fourthRowPanel);

        panel.add(fourthRowPanel, builder.withLocation(0, 4).withColumnSpan(3).withRowSpan(4).withFill(GridBagConstraints.BOTH).withLowWeights().build());
    }

    private void constructStopWordControl( final JPanel panel )
    {
        final JPanel stopWordPanel = thePanelBuilder.withTitledBorder( "Stop Word Type" ).withSpecificSize( 160, 130 ).build();
        final JRadioButton all = theStopWordsRadioButtonBuilder.withAction(theAllStopWordsAction).build();
        final JRadioButton words = theStopWordsRadioButtonBuilder.withAction( theWordsStopWordsAction ).build();
        final JRadioButton phrases = theStopWordsRadioButtonBuilder.withAction( thePhrasesStopWordsAction ).build();
        final JRadioButton none = theStopWordsRadioButtonBuilder.withAction( theNoneStopWordsAction ).build();
        theDefaultStopWordButtonModel = all.getModel();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill( GridBagConstraints.NONE ).withAnchor(GridBagConstraints.LINE_START);
        stopWordPanel.add(all, builder.withLocation(0, 0).withLowWeights().build());
        stopWordPanel.add(words, builder.withLocation(0, 1).withLowWeights().build());
        stopWordPanel.add(phrases, builder.withLocation(0, 2).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).build());
        stopWordPanel.add(none, builder.withLocation(0, 3).withLowWeights().build());

        panel.add(stopWordPanel, builder.withLocation(1, 0).withFill(GridBagConstraints.BOTH).withLowWeights().build());
    }

    private void constructInternalListNameMapControl(final JPanel panel)
    {
        final JPanel internalListNameMapPanel = thePanelBuilder.withTitledBorder(null).withSpecificSize(480, 40).build();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill(GridBagConstraints.NONE).withAnchor(GridBagConstraints.LINE_START);
        internalListNameMapPanel.add(theInternalListNameMapCheckBox, builder.withLocation( 0, 0 ).withLowWeights().build() );
        internalListNameMapPanel.add(theInternalListNameMapFile, builder.withLocation(1, 0).
                withFill(GridBagConstraints.HORIZONTAL).
                withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).
                build());
        theInternalListNameMapFile.setEditable(false);
        internalListNameMapPanel.add(theInternalListNameMapBrowseButton,
                builder.withLocation(2, 0).withWeights(GridBagConstraintsBuilder.LOW, GridBagConstraintsBuilder.HIGH).build());
        theInternalListNameMapBrowseButton.setEnabled(false);

        panel.add(internalListNameMapPanel, builder.withLocation(0, 3).withLowWeights().build());
    }

    private void constructExtractionApproachControl(final JPanel panel)
    {
        final JPanel extractionApproachPanel = thePanelBuilder.withTitledBorder("Extraction Approach").withSpecificSize(480, 130).build();
        final JRadioButton original = theExtractionApproachRadioButtonBuilder.withAction(theOriginalExtractionApproachAction).build();
        final JRadioButton marsoc = theExtractionApproachRadioButtonBuilder.withAction(theMarsocExtractionApproachAction).build();
        final JRadioButton internal = theExtractionApproachRadioButtonBuilder.withAction(theInternalExtractionApproachAction).build();
        theDefaultExtractionApproachButtonModel = internal.getModel();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill(GridBagConstraints.NONE).withAnchor(GridBagConstraints.LINE_START);
        extractionApproachPanel.add(original, builder.withLocation(0, 0).withLowWeights().build());
        extractionApproachPanel.add(marsoc, builder.withLocation(0, 1).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).build());
        extractionApproachPanel.add(internal, builder.withLocation(0, 2).withLowWeights().build());
        constructInternalListNameMapControl(extractionApproachPanel);

        panel.add(extractionApproachPanel, builder.withLocation(0, 0).withFill(GridBagConstraints.BOTH).withLowWeights().build());
    }

    private void constructL2SuffixControl( final JPanel panel, final int row )
    {
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withAnchor(GridBagConstraints.LINE_START);

        final JLabel label = theLabelBuilder.withText( "L2 Suffix" ).withTip( L2_TIP ).build();
        panel.add( label, builder.withLocation( 0, row ).withFill(GridBagConstraints.NONE).withLowWeights().build() );

        panel.add( theL2Suffix, builder.withLocation(1, row).withFill( GridBagConstraints.HORIZONTAL ).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).build() );
    }

    private void constructL1SuffixControl( final JPanel panel, final int row )
    {
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withAnchor( GridBagConstraints.LINE_START );

        final JLabel label = theLabelBuilder.withText( "L1 Suffix" ).withTip( L1_TIP ).build();
        panel.add( label, builder.withLocation( 0, row ).withFill( GridBagConstraints.NONE ).withLowWeights().build() );

        panel.add( theL1Suffix, builder.withLocation( 1, row ).withFill( GridBagConstraints.HORIZONTAL ).withWeights( GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW ).build() );
    }

    private void constructFilePrefixControl( final JPanel panel, final int row )
    {
        final JLabel label = theLabelBuilder.withText( "File Prefix" ).withTip( PREFIX_TIP ).build();

        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withAnchor(GridBagConstraints.LINE_START);
        panel.add( label, builder.withLocation(0, row).withFill(GridBagConstraints.NONE).withLowWeights().build() );

        panel.add(theFilePrefix, builder.withLocation(1, row).withFill(GridBagConstraints.HORIZONTAL).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).build());
    }

    private void constructConfigurationFileControl( final JPanel panel, final int row )
    {
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill( GridBagConstraints.NONE ).withAnchor( GridBagConstraints.LINE_START );

        final JLabel label = theLabelBuilder.withText( "Configuration File" ).build();
        panel.add( label, builder.withLocation( 0, row ).withLowWeights().build() );
        panel.add(theConfigurationFile, builder.withLocation(1, row).withFill(GridBagConstraints.HORIZONTAL).withWeights(GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW).build());
        panel.add( theButtonBuilder.withAction( theConfigurationFileBrowseAction ).build(), builder.withLocation( 2, row ).withFill(GridBagConstraints.NONE).withAnchor(GridBagConstraints.LINE_START).withWeights(GridBagConstraintsBuilder.LOW, GridBagConstraintsBuilder.HIGH).build() );
        theConfigurationFile.setEditable(false);
    }

    private GridBagConstraintsBuilder newConstraintsBuilder()
    {
        final GridBagConstraintsBuilder builder = new GridBagConstraintsBuilder();
        builder.withExternalPadding(2, 2, 2, 2);
        return builder;
    }

    private void constructDestinationFolderControl( final JPanel panel, final int row )
    {
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill( GridBagConstraints.NONE ).withAnchor( GridBagConstraints.LINE_START );
        final JLabel label = theLabelBuilder.withText( "Destination Folder" ).build();
        panel.add( label, builder.withLocation( 0, row ).withLowWeights().build() );
        panel.add( theDestinationDirectory, builder.withLocation( 1, row ).withFill( GridBagConstraints.HORIZONTAL ).withWeights( GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW ).build() );
        panel.add( theButtonBuilder.withAction( theDestinationDirectoryAction ).build(), builder.withLocation( 2, row ).withWeights( GridBagConstraintsBuilder.LOW, GridBagConstraintsBuilder.HIGH ).build() );
        theDestinationDirectory.setEditable( false );
    }

    private void constructContentFolderControl( final JPanel panel, final int row )
    {
        final GridBagConstraintsBuilder builder = newConstraintsBuilder();
        builder.withFill( GridBagConstraints.NONE ).withAnchor( GridBagConstraints.LINE_START );
        final JLabel label = theLabelBuilder.withText( "Content Folder" ).build();
        panel.add( label, builder.withLocation( 0, row ).withLowWeights().build() );
        panel.add( theContentDirectory, builder.withLocation( 1, row ).withFill( GridBagConstraints.HORIZONTAL ).withWeights( GridBagConstraintsBuilder.HIGH, GridBagConstraintsBuilder.LOW ).build() );
        panel.add( theButtonBuilder.withAction( theContentDirectoryAction ).build(), builder.withLocation( 2, row ).withWeights( GridBagConstraintsBuilder.LOW, GridBagConstraintsBuilder.HIGH ).build() );
        theContentDirectory.setEditable( false );
    }

    private void installMenu()
    {
        final JMenuBarBuilder menuBarBuilder = new JMenuBarBuilder();
        final JMenuBuilder menuBuilder = new JMenuBuilder();

        final JMenu fileMenu = menuBuilder.withAction( new NullAction( "File" ) ).withMenuItem( theResetAction ).withMenuItem( new ExitAction( "Exit" ) ).build();
        final JMenu helpMenu = menuBuilder.withAction( new NullAction( "Help" ) ).withMenuItem(  new TopicsAction( "Topics" ) ).withMenuItem( new AboutAction( "About" ) ).build();
        final JMenuBar menuBar = menuBarBuilder.withMenu( fileMenu ).withMenu( helpMenu ).build();
        setJMenuBar( menuBar );
    }

    @Override
    public void display()
    {
        pack();
        setVisible( true );

        // there is an unresolved issue in JTextArea where the number of debug messages causes the UI to stop responding.
        // for now, we'll just limit the number of messages that go here to WARN and above
        final WriterAppender developerAppender = createAppender( theDeveloperMessages, null, "%r %p %c{1} %m%n" );
        Logger.getRootLogger().addAppender( developerAppender );

        final WriterAppender userAppender = createAppender( theUsersMessages, "normal.messages", "%r %m %n" );
        Logger.getLogger( "normal.messages" ).addAppender( userAppender );

        final Dimension size = getSize();
        theLogger.debug( "size = " + size.width + "x" + size.height );
    }

    private WriterAppender createAppender( final JTextArea textArea, final String appenderName, final String pattern )
    {
        final SwingWriter writer = new SwingWriter( textArea );
        final WriterAppender appender = new WriterAppender( new PatternLayout( pattern ), writer );
        appender.setName( appenderName );
        appender.setThreshold( Level.ALL );
        return appender;
    }

    @Override
    public void registerShutdownHandler( final ShutdownHandler handler )
    {
        theShutdownHandler = handler;
    }

    private void callShutdownHandler()
    {
        if ( null != theShutdownHandler )
        {
            theShutdownHandler.handleShutdownRequest();
        }
    }

    private final class CustomWindowAdapter extends WindowAdapter
    {
        @Override
        public void windowClosing( final WindowEvent e )
        {
            callShutdownHandler();
        }
    }

    protected abstract class AbstractBrowseAction extends AbstractAction
    {
        private AbstractBrowseAction( final String name, final String description )
        {
            super( name );
            putValue( SHORT_DESCRIPTION, description );
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            final JFileChooser chooser = createChooser();
            final int option = chooser.showOpenDialog( SwingView.this );
            if ( JFileChooser.APPROVE_OPTION == option )
            {
                final File selectedFile = chooser.getSelectedFile();
                handleFileSelection( selectedFile );
            }
            else if ( JFileChooser.CANCEL_OPTION == option )
            {
                handleSelectionCancelled();
            }
            else
            {
                handleSelectionError();
            }
        }

        protected abstract JFileChooser createChooser();

        protected void handleSelectionError()
        {
            theLogger.trace( "handleSelectionError called" );
        }

        protected void handleSelectionCancelled()
        {
            theLogger.trace( "handleSelectionCancelled called" );
        }

        protected void handleFileSelection( final File file )
        {
            theLogger.trace("User selected " + file.getPath());
        }
    }

    protected class DirectoryBrowseAction extends AbstractBrowseAction
    {
        protected DirectoryBrowseAction( final String name,
                                         final String description )
        {
            super(name, description);
        }

        @Override
        protected JFileChooser createChooser()
        {
            return theChooserBuilder.withDirectoriesOnly().build();
        }

        @Override
        protected void handleFileSelection( final File file )
        {
            theLogger.trace( "handleFileSelection called" );
        }
    }

    private final class ContentDirectoryBrowseAction extends DirectoryBrowseAction
    {
        private ContentDirectoryBrowseAction( final String name, final String description )
        {
            super( name, description );
        }

        private ContentDirectorySelectionHandler theHandler;

        public void setTheHandler( final ContentDirectorySelectionHandler aHandler )
        {
            theHandler = aHandler;
        }

        @Override
        protected void handleFileSelection( final File file )
        {
            theHandler.handleContentDirectorySelected(file);
        }
    }

    private final class DestinationDirectoryBrowseAction extends DirectoryBrowseAction
    {
        private DestinationDirectorySelectionHandler theHandler;

        private DestinationDirectoryBrowseAction( final String name, final String description )
        {
            super( name, description );
        }

        public void setTheHandler( final DestinationDirectorySelectionHandler theHandler )
        {
            this.theHandler = theHandler;
        }

        @Override
        protected void handleFileSelection( final File file )
        {
            theHandler.handleDestinationDirectorySelected(file);
        }
    }

    protected class ConfigurationFileBrowseAction extends AbstractBrowseAction
    {
        private ConfigurationFileSelectionHandler theHandler;

        protected ConfigurationFileBrowseAction(final String name,
                                                final String description)
        {
            super( name, description );
        }

        public void setTheHandler( final ConfigurationFileSelectionHandler aHandler )
        {
            theHandler = aHandler;
        }

        @Override
        protected JFileChooser createChooser()
        {
            return theChooserBuilder.withFilesOnly().build();
        }

        @Override
        protected void handleFileSelection( final File file )
        {
            theHandler.handleConfigurationFileSelected(file);
        }
    }

    protected class InternalListNameMapFileBrowseAction extends AbstractBrowseAction
    {
        private InternalListNameMapFileSelectionHandler theHandler;

        protected InternalListNameMapFileBrowseAction(final String name, final String description)
        {
            super(name, description);
        }

        public void setTheHandler(final InternalListNameMapFileSelectionHandler aHandler)
        {
            theHandler = aHandler;
        }

        @Override
        protected JFileChooser createChooser()
        {
            return theChooserBuilder.withFilesOnly().build();
        }

        @Override
        protected void handleFileSelection(final File file)
        {
            theHandler.handleInternalListNameMapFileSelected(file);
        }
    }

    private final class GenerateAction extends AbstractAction
    {
        private GlossaryGenerationHandler theHandler;

        private GenerateAction()
        {
            putValue( SHORT_DESCRIPTION, "Begin generation of glossary files." );
            try
            {
                final InputStream inputStream = SwingView.class.getResourceAsStream( "/images/000478-3d-glossy-blue-orb-icon-media-a-media22-arrow-forward1-64x64.png" );
                final BufferedImage image = ImageIO.read( inputStream );
                final ImageIcon icon = new ImageIcon( image, "Description" );
                putValue( SMALL_ICON, icon );
            }
            catch( IOException e )
            {
                theLogger.warn( "Unable to load button icon.", e );
            }
        }

        public void setTheHandler( final GlossaryGenerationHandler theHandler )
        {
            this.theHandler = theHandler;
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            theHandler.handleGenerateGlossarySelected(theFilePrefix.getText(), theL1Suffix.getText(), theL2Suffix.getText());
        }
    }

    private final class ResetAction extends AbstractAction
    {
        private ResetSelectionHandler theHandler;

        private ResetAction( final String name )
        {
            super( name );
            putValue( SHORT_DESCRIPTION, "Reset the form to its default values." );
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            theHandler.handleResetSelected();
        }

        public void setHandler( final ResetSelectionHandler handler )
        {
            theHandler = handler;
        }
    }

    private final class NullAction extends AbstractAction
    {
        private NullAction( final String name )
        {
            super(name);
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            // nothing to do
        }
    }

    private final class ExitAction extends AbstractAction
    {
        private ExitAction( final String name )
        {
            super( name );
            putValue(SHORT_DESCRIPTION, "Exit the application.");
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            theLogger.trace( "ExitAction called " );
            callShutdownHandler();
        }
    }

    private final class TopicsAction extends AbstractHelpAction
    {
        private TopicsAction( final String name )
        {
            super( name );
            putValue( SHORT_DESCRIPTION, "Get on-line help about the application." );
        }

        protected String getWindowTitle()
        {
            return "Gloss-E Help Topics";
        }

        protected String loadHtml()
        {
            String text = "On-line help currently unavailable.";
            try
            {
                text = loadHtml( "/online-help.html" );
            }
            catch( Exception e )
            {
                theLogger.warn( "Unable to help file.", e );
            }
            return text;
        }
    }

    private abstract class AbstractHelpAction extends AbstractAction
    {
        protected AbstractHelpAction( final String name )
        {
            super( name );
        }

        protected JComponent constructReadOnlyHtmlArea( final String html )
        {
            final JEditorPaneBuilder builder = new JEditorPaneBuilder().withContentType( "text/html" ).withText( html ).withEditing( false );
            final JEditorPane pane = builder.build();
            return new JScrollPane( pane );
        }

        protected void copyCharacters( final Reader in, final Writer out ) throws IOException
        {
            final char[] buffer = new char[1024];
            while( true )
            {
                final int charactersRead = in.read( buffer );
                if( -1 == charactersRead  )
                {
                    break;
                }
                out.write( buffer, 0, charactersRead );
            }
        }

        protected String loadHtml( final String htmlResource ) throws IOException
        {
            final InputStream stream = SwingView.class.getResourceAsStream( htmlResource );
            final Reader reader = new BufferedReader( new InputStreamReader( stream ) );
            final Writer writer = new StringWriter( 1024 );
            copyCharacters( reader, writer );
            return writer.toString();
        }

        @Override
        public void actionPerformed( final ActionEvent event )
        {
            JOptionPane.showMessageDialog(SwingView.this, constructReadOnlyHtmlArea(loadHtml()), getWindowTitle(), JOptionPane.INFORMATION_MESSAGE);
        }

        protected abstract String loadHtml();

        protected abstract String getWindowTitle();
    }

    private final class AboutAction extends AbstractHelpAction
    {
        private AboutAction( final String name )
        {
            super( name );
            putValue(SHORT_DESCRIPTION, "Get basic version information about this application.");
        }

        protected String getWindowTitle()
        {
            return "About Gloss-E";
        }

        protected String loadHtml()
        {
            String text = "Release notes currently unavailable.";
            try
            {
                text = loadHtml( "/release-notes.html" );
            }
            catch( Exception e )
            {
                theLogger.warn( "Unable to load release notes.", e );
            }
            return text;
        }
    }

    private final class MarsocAction extends AbstractAction
    {
        private MarsocSelectionHandler theHandler;

        private MarsocAction( final String name )
        {
            super( name );
            putValue(SHORT_DESCRIPTION, "If enabled, expects folder names to be encoded with part-of-speech information, eg. PORbr_ENGus_07_Verbs.");
            putValue( SELECTED_KEY, false );
        }

        public void setTheHandler( final MarsocSelectionHandler aHandler )
        {
            theHandler = aHandler;
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            final JCheckBox box = (JCheckBox) e.getSource();
            theHandler.handleMarsocChange( box.isSelected() );
        }
    }

    private final class InternalListNameMapAction extends AbstractAction
    {
        private InternalListNameMapSelectionHandler theHandler;

        private InternalListNameMapAction( final String name )
        {
            super( name );
            putValue( SHORT_DESCRIPTION, "If selected, users can import a file with internal list names and new list of references." );
            putValue( SELECTED_KEY, false );
        }

        public void setTheHandler( final InternalListNameMapSelectionHandler aHandler )
        {
            theHandler = aHandler;
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            final JCheckBox box = (JCheckBox) e.getSource();
            theHandler.handleInternalListNameMapChange(box.isSelected());
        }
    }

    private final class ExtractionApproachAction extends AbstractAction
    {
        private ExtractionApproachSelectionHandler theHandler;

        private ExtractionApproachAction(final String name, final String description, boolean selected)
        {
            super(name);
            putValue(SHORT_DESCRIPTION, description);
            putValue(SELECTED_KEY, selected);
            putValue(ACTION_COMMAND_KEY, name);
        }

        @Override
        public void actionPerformed(final ActionEvent e)
        {
            final String command = theExtractionApproachButtonGroup.getSelection().getActionCommand();
            theLogger.trace(command + " selected");
            theHandler.handleExtractionApproachSelected(command);
        }

        public void setHandler(final ExtractionApproachSelectionHandler aHandler)
        {
            theHandler = aHandler;
        }
    }

    private final class StopWordsAction extends AbstractAction
    {
        private StopWordSelectionHandler theHandler;

        private StopWordsAction( final String name, final String description, boolean selected )
        {
            super( name );
            putValue( SHORT_DESCRIPTION, description );
            putValue( SELECTED_KEY, selected );
            putValue( ACTION_COMMAND_KEY, name );
        }

        @Override
        public void actionPerformed( final ActionEvent e )
        {
            final String command = theStopWordsButtonGroup.getSelection().getActionCommand();
            theLogger.trace( command + " selected" );
            theHandler.handleStopWordSelected( command );
        }

        public void setHandler( final StopWordSelectionHandler aHandler )
        {
            theHandler = aHandler;
        }
    }

    private final class GlossaryRenderer implements TreeCellRenderer
    {
        private final JLabel theLabel = new JLabel();
        private final Icon theOpenIcon;
        private final Icon theClosedIcon;
        private final Icon theLeafIcon;

        private GlossaryRenderer()
        {
            final DefaultTreeCellRenderer defaultTreeCellRenderer = new DefaultTreeCellRenderer();
            theOpenIcon = defaultTreeCellRenderer.getOpenIcon();
            theClosedIcon = defaultTreeCellRenderer.getClosedIcon();
            theLeafIcon = defaultTreeCellRenderer.getLeafIcon();
        }

        @Override
        public Component getTreeCellRendererComponent( final JTree tree,
                                                       final Object value,
                                                       final boolean selected,
                                                       final boolean expanded,
                                                       final boolean leaf,
                                                       final int row,
                                                       final boolean hasFocus )
        {
            theLabel.setText( value.toString() );
            theLabel.setToolTipText( null );

            if ( leaf )
            {
                theLabel.setIcon( theLeafIcon );
                if ( value instanceof DefaultMutableTreeNode )
                {
                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
                    final Object userObject = node.getUserObject();
                    if ( userObject instanceof GlossaryContentHandler.Tag )
                    {
                        final GlossaryContentHandler.Tag tag =  (GlossaryContentHandler.Tag)userObject;
                        theLabel.setText( tag.data );
                        theLabel.setToolTipText( tag.tooltip );
                    }
                }
            }
            else
            {
                if ( expanded )
                {
                    theLabel.setIcon( theOpenIcon );
                }
                else
                {
                    theLabel.setIcon( theClosedIcon );
                }
            }
            return theLabel;
        }
    }
}
