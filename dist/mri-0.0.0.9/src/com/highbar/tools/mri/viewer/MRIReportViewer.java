package com.highbar.tools.mri.viewer;

import com.highbar.tools.mri.monitor.ExecutionNode;
import com.highbar.tools.mri.jaxb.XEnoviaProfilerReport;
import com.highbar.tools.mri.jaxb.XExecutionNode;
import com.highbar.tools.mri.jaxb.XMethodParameter;
import com.highbar.tools.mri.jaxb.XMethodSummary;
import com.highbar.tools.mri.jaxb.XMissingTrigger;
import com.highbar.tools.mri.jaxb.XStackTrace;
import com.highbar.tools.mri.jaxb.XTrigger;
import com.highbar.util.Filter;
import com.highbar.util.FilteredList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 * This class provides a user interface for viewing profiling data collected and saved in an xml file.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public class MRIReportViewer
{
    private static JFrame frame;

    protected JTabbedPane mainTabbedPane;
    protected JTabbedPane triggersTabPane;
    protected JPanel summaryPanel;
    protected JPanel callStackPanel;
    protected JPanel triggersPanel;
    protected JPanel triggerSummaryPanel;
    protected JPanel missingTriggerPanel;
    protected JPanel inactiveTriggerPanel;
    protected JPanel activeTriggerPanel;

    private JPanel mainPanel;
    private JCheckBox triggerManagerCheckBox;
    private JCheckBox triggersCheckBox;
    private JCheckBox jpoCheckBox;
    private JCheckBox jspCheckBox;
    private JCheckBox apiCheckBox;
    private JTable mainSummaryTable;
    private JTable callStackTable;
    private JTable missingTriggerTable;
    private JTable inactiveTriggerTable;
    private JTable activeTriggerTable;
    private JTable methodParametersTable;
    private JTable methodAncestorsTable;
    private JLabel triggerManagerInstancesLabel;
    private JLabel triggerManagerInstantiationOverheadLabel;
    private JLabel triggerManagerMxMainInvocationsLabel;
    private JLabel triggerManagerInvocationOverheadLabel;
    private JLabel activeTriggersFiredLabel;
    private JLabel inactiveTriggerEncounteredLabel;
    private JLabel missingTriggerProgramParametersLabel;
    private JLabel timingSliderValue;
    private JSlider timingSlider;
    private JButton scrollToRowButton;

    private static final int FILTER_MAX_MILLIS = 500;

    private final List<XExecutionNode> allNodes = new ArrayList<XExecutionNode>();
    private final CallStackTableFilter callStackTableFilter = new CallStackTableFilter();

    private FilteredList<XExecutionNode> filteredNodes;
    private XExecutionNode selectedNode;

    public MRIReportViewer()
    {
        $$$setupUI$$$();
        getFileAndDisplay();

//        openFileButton.addActionListener( new ActionListener() {
//
//            public void actionPerformed( ActionEvent e )
//            {
//                getFileAndDisplay();
//            }
//        });
    }

    private void getFileAndDisplay()
    {
        File file = getFileFromUserInput();
        if ( file != null )
        {
            displayFileContent( file );
        }
    }

    private File getFileFromUserInput()
    {
        JFileChooser fc = new JFileChooser();
        fc.showOpenDialog( new JFrame() );
        return fc.getSelectedFile();
    }

    private void displayFileContent( File file )
    {
        if ( file != null )
        {
            XEnoviaProfilerReport report = unmarshal( file );
            initTimingSlider();
            initStackRelatedCheckboxes();
            initStackTraces( report.getStackTraces() );
            initScrollToRowButton();
            displayReport( report );

            frame.setTitle( "MRI - Matrix Runtime Inspector - " + file.getName() );
        }
    }

    private void initTimingSlider()
    {
        timingSlider.setModel( new DefaultBoundedRangeModel() );
        timingSlider.setMinimum( 0 );
        timingSlider.setMaximum( FILTER_MAX_MILLIS );
        timingSlider.setValue( 1 );
        timingSliderValue.setText( String.valueOf( timingSlider.getValue() ) + "ms" );
        timingSlider.addChangeListener( new ChangeListener()
        {
            public void stateChanged( ChangeEvent e )
            {
                timingSliderValue.setText( String.valueOf( timingSlider.getValue() ) + "ms" );
                callStackTableFilter.filter();
            }
        } );
    }

    void initStackRelatedCheckboxes()
    {
        triggerManagerCheckBox.setSelected( true );
        triggersCheckBox.setSelected( true );
        jpoCheckBox.setSelected( true );
        jspCheckBox.setSelected( true );
        apiCheckBox.setSelected( false );

        ItemListener listener = new ItemListener()
        {
            public void itemStateChanged( ItemEvent e )
            {
                callStackTableFilter.filter();
            }
        };

        triggerManagerCheckBox.addItemListener( listener );
        triggersCheckBox.addItemListener( listener );
        jpoCheckBox.addItemListener( listener );
        jspCheckBox.addItemListener( listener );
        apiCheckBox.addItemListener( listener );
    }

    void initScrollToRowButton()
    {
        scrollToRowButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                int selectedRow = callStackTable.getSelectedRow();

                if ( selectedRow != -1 )
                {
                    callStackTable.scrollRectToVisible( callStackTable.getCellRect( selectedRow, 0, true ) );
                }
            }
        } );
    }

    public static void main( String[] args )
    {
        frame = new JFrame( "MRI - Matrix Runtime Inspector" );
        frame.setContentPane( new MRIReportViewer().mainPanel );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }

    XEnoviaProfilerReport unmarshal( File file )
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance( "com.highbar.tools.mri.jaxb" );
            Unmarshaller u = jc.createUnmarshaller();
            return (XEnoviaProfilerReport)u.unmarshal( file );
        }
        catch ( JAXBException e )
        {
            e.printStackTrace();
            throw new Error( e );
        }
    }

    void displayReport( XEnoviaProfilerReport report )
    {
        displaySummary( report.getMethodSummary() );
        displayTriggers( report );
    }

    private void initStackTraces( List<XStackTrace> stackTraces )
    {
        initExecutionNodeList( stackTraces );

        // show the selected node's ancestors and parameter values when clicked
        ListSelectionModel selectionModel = callStackTable.getSelectionModel();
        selectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        selectionModel.addListSelectionListener( new ListSelectionListener()
        {
            public void valueChanged( ListSelectionEvent e )
            {
                int rowIndex = callStackTable.getSelectedRow();
                if ( rowIndex != -1 )
                {
                    selectedNode = filteredNodes.get( rowIndex );
                    List<XExecutionNode> methodAncestors = getMethodAncestors( selectedNode );

                    MethodAncestorsTableModel ancestorsModel = (MethodAncestorsTableModel)methodAncestorsTable.getModel();
                    MethodParametersTableModel parametersModel = (MethodParametersTableModel)methodParametersTable.getModel();

                    ancestorsModel.setData( methodAncestors );
                    ancestorsModel.fireTableDataChanged();

                    parametersModel.setData( selectedNode.getMethodParameters() );
                    parametersModel.fireTableDataChanged();
                }
            }
        } );

        callStackTable.setModel( new ThreadStackTableModel( filteredNodes ) );

        DefaultTableCellRenderer rightJustifyRenderer = new DefaultTableCellRenderer();
        rightJustifyRenderer.setHorizontalAlignment( SwingConstants.RIGHT );
        callStackTable.getColumnModel().getColumn( 5 ).setCellRenderer( rightJustifyRenderer );
        callStackTable.getColumnModel().getColumn( 6 ).setCellRenderer( rightJustifyRenderer );
    }

    private void initExecutionNodeList( List<XStackTrace> stackTraces )
    {
        for ( XStackTrace stackTrace : stackTraces )
        {
            XExecutionNode threadRoot = new XExecutionNode();
            threadRoot.setOrder( -1 );
            threadRoot.setDepth( -1 );
            threadRoot.setType( ExecutionNode.Type.ROOT.toString() );
            threadRoot.setName( stackTrace.getThreadId() );
            threadRoot.setTotalTime( stackTrace.getTotalTime() );
            allNodes.add( threadRoot );
            allNodes.addAll( stackTrace.getExecutionNodes() );
        }

        filteredNodes = new FilteredList<XExecutionNode>( allNodes, new XExecutionNodeFilter() );
    }

    private List<XExecutionNode> getMethodAncestors( XExecutionNode node )
    {
        final List<XExecutionNode> methodAncestors = new ArrayList<XExecutionNode>();
        int index = allNodes.indexOf( node );

        methodAncestors.add( node );
        while ( node != null && node.getDepth() != 0 && index-- != 0 )
        {
            XExecutionNode parent = allNodes.get( index );
            if ( parent.getDepth() == node.getDepth() - 1 )
            {
                methodAncestors.add( 0, parent );
                node = parent;
            }
        }

        return methodAncestors;
    }

    private void displaySummary( final List<XMethodSummary> methodSummary )
    {
        mainSummaryTable.setModel( new MethodSummaryTableModel( methodSummary ) );

        DefaultTableCellRenderer rightJustifyRenderer = new DefaultTableCellRenderer();
        rightJustifyRenderer.setHorizontalAlignment( SwingConstants.RIGHT );
        mainSummaryTable.getColumnModel().getColumn( 3 ).setCellRenderer( rightJustifyRenderer );
        mainSummaryTable.getColumnModel().getColumn( 4 ).setCellRenderer( rightJustifyRenderer );
        mainSummaryTable.getColumnModel().getColumn( 5 ).setCellRenderer( rightJustifyRenderer );
    }

    private void displayTriggers( XEnoviaProfilerReport report )
    {
        List<XMissingTrigger> missingTriggers = report.getMissingTriggers();
        List<XTrigger> activeTriggers = report.getActiveTriggers();
        List<XTrigger> inactiveTriggers = report.getInactiveTriggers();

        displayTriggerSummary( report );

        missingTriggerTable.setModel( new MissingTriggersTableModel( missingTriggers ) );
        activeTriggerTable.setModel( new TriggersTableModel( activeTriggers ) );
        inactiveTriggerTable.setModel( new TriggersTableModel( inactiveTriggers ) );

        DefaultTableCellRenderer rightJustifyRenderer = new DefaultTableCellRenderer();
        rightJustifyRenderer.setHorizontalAlignment( SwingConstants.RIGHT );
        missingTriggerTable.getColumnModel().getColumn( 1 ).setCellRenderer( rightJustifyRenderer );

        activeTriggerTable.getColumnModel().getColumn( 2 ).setCellRenderer( rightJustifyRenderer );
        activeTriggerTable.getColumnModel().getColumn( 5 ).setCellRenderer( rightJustifyRenderer );

        inactiveTriggerTable.getColumnModel().getColumn( 2 ).setCellRenderer( rightJustifyRenderer );
        inactiveTriggerTable.getColumnModel().getColumn( 5 ).setCellRenderer( rightJustifyRenderer );

    }

    private void displayTriggerSummary( XEnoviaProfilerReport report )
    {
        triggerManagerInstancesLabel.setText( String.valueOf( report.getOverhead().getTimings().size() ) );
        triggerManagerInstantiationOverheadLabel.setText( getTriggerInstantiationOverhead(
                report.getOverhead().getTimings() ) + "ms" );

        int triggerManagerInvocations = 0;
        long triggerManagerTime = 0;
        List<XMethodSummary> methodSummaries = report.getMethodSummary();

        for ( XMethodSummary methodSummary : methodSummaries )
        {
            if ( ExecutionNode.Type.valueOf( methodSummary.getType() ) == ExecutionNode.Type.TRIGGER_MANAGER )
            {
                if ( methodSummary.getMethod().startsWith( "mxMain" ) )
                {
                    triggerManagerInvocations = methodSummary.getInvocations();
                }
                triggerManagerTime += methodSummary.getTotalTime();
            }
        }
        triggerManagerMxMainInvocationsLabel.setText( String.valueOf( triggerManagerInvocations ) );
        triggerManagerInvocationOverheadLabel.setText( NumberFormats.nanosToMillis( triggerManagerTime ) + "ms" );

        activeTriggersFiredLabel.setText( String.valueOf( report.getActiveTriggers().size() ) );
        inactiveTriggerEncounteredLabel.setText( String.valueOf( report.getInactiveTriggers().size() ) );
        missingTriggerProgramParametersLabel.setText( String.valueOf( report.getMissingTriggers().size() ) );
    }

    private String getTriggerInstantiationOverhead( List<Long> timings )
    {
        long total = 0;
        for ( long l : timings )
        {
            total += l;
        }
        return NumberFormats.nanosToMillis( total );
    }

    private void createUIComponents()
    {
        methodAncestorsTable = new JTable();
        methodParametersTable = new JTable();

        methodAncestorsTable.setModel( new MethodAncestorsTableModel( new ArrayList<XExecutionNode>() ) );
        methodParametersTable.setModel( new MethodParametersTableModel( new ArrayList<XMethodParameter>() ) );
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainPanel.setPreferredSize( new Dimension( 800, 600 ) );
        mainTabbedPane = new JTabbedPane();
        mainPanel.add( mainTabbedPane,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null,
                        new Dimension( 600, 400 ), null, 0, false ) );
        summaryPanel = new JPanel();
        summaryPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainTabbedPane.addTab( "Summary", summaryPanel );
        final JScrollPane scrollPane1 = new JScrollPane();
        summaryPanel.add( scrollPane1,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                        0, false ) );
        mainSummaryTable = new JTable();
        scrollPane1.setViewportView( mainSummaryTable );
        callStackPanel = new JPanel();
        callStackPanel.setLayout( new GridLayoutManager( 2, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainTabbedPane.addTab( "Call Stack", callStackPanel );
        final JPanel panel1 = new JPanel();
        panel1.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
        callStackPanel.add( panel1,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false ) );
        final JToolBar toolBar1 = new JToolBar();
        panel1.add( toolBar1 );
        final JLabel label1 = new JLabel();
        label1.setMaximumSize( new Dimension( 100, 22 ) );
        label1.setMinimumSize( new Dimension( 100, 22 ) );
        label1.setPreferredSize( new Dimension( 100, 22 ) );
        label1.setText( "Filter By Timing:" );
        toolBar1.add( label1 );
        timingSlider = new JSlider();
        timingSlider.setPreferredSize( new Dimension( 100, 21 ) );
        timingSlider.setValue( 0 );
        toolBar1.add( timingSlider );
        timingSliderValue = new JLabel();
        timingSliderValue.setInheritsPopupMenu( false );
        timingSliderValue.setMaximumSize( new Dimension( -1, 22 ) );
        timingSliderValue.setMinimumSize( new Dimension( 60, 22 ) );
        timingSliderValue.setPreferredSize( new Dimension( 60, 22 ) );
        timingSliderValue.setText( "" );
        toolBar1.add( timingSliderValue );
        final JToolBar.Separator toolBar$Separator1 = new JToolBar.Separator();
        panel1.add( toolBar$Separator1 );
        final JToolBar toolBar2 = new JToolBar();
        panel1.add( toolBar2 );
        final JLabel label2 = new JLabel();
        label2.setText( "Include Types:" );
        toolBar2.add( label2 );
        triggerManagerCheckBox = new JCheckBox();
        triggerManagerCheckBox.setText( "Trigger Manager" );
        triggerManagerCheckBox.setMnemonic( 'T' );
        triggerManagerCheckBox.setDisplayedMnemonicIndex( 0 );
        toolBar2.add( triggerManagerCheckBox );
        triggersCheckBox = new JCheckBox();
        triggersCheckBox.setText( "Triggers" );
        triggersCheckBox.setMnemonic( 'R' );
        triggersCheckBox.setDisplayedMnemonicIndex( 1 );
        toolBar2.add( triggersCheckBox );
        jspCheckBox = new JCheckBox();
        jspCheckBox.setText( "JSPs" );
        jspCheckBox.setMnemonic( 'J' );
        jspCheckBox.setDisplayedMnemonicIndex( 0 );
        toolBar2.add( jspCheckBox );
        apiCheckBox = new JCheckBox();
        apiCheckBox.setText( "API" );
        apiCheckBox.setMnemonic( 'A' );
        apiCheckBox.setDisplayedMnemonicIndex( 0 );
        toolBar2.add( apiCheckBox );
        jpoCheckBox = new JCheckBox();
        jpoCheckBox.setText( "JPOs" );
        jpoCheckBox.setMnemonic( 'P' );
        jpoCheckBox.setDisplayedMnemonicIndex( 1 );
        toolBar2.add( jpoCheckBox );
        final JToolBar.Separator toolBar$Separator2 = new JToolBar.Separator();
        panel1.add( toolBar$Separator2 );
        scrollToRowButton = new JButton();
        scrollToRowButton.setText( "Scroll To Selected Row" );
        scrollToRowButton.setMnemonic( 'S' );
        scrollToRowButton.setDisplayedMnemonicIndex( 0 );
        panel1.add( scrollToRowButton );
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setDividerLocation( 239 );
        splitPane1.setDividerSize( 4 );
        splitPane1.setOrientation( 0 );
        callStackPanel.add( splitPane1,
                new GridConstraints( 1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null,
                        new Dimension( 200, 200 ), null, 0, false ) );
        final JScrollPane scrollPane2 = new JScrollPane();
        splitPane1.setLeftComponent( scrollPane2 );
        callStackTable = new JTable();
        callStackTable.setAutoResizeMode( 2 );
        scrollPane2.setViewportView( callStackTable );
        final JSplitPane splitPane2 = new JSplitPane();
        splitPane2.setDividerLocation( 400 );
        splitPane2.setDividerSize( 4 );
        splitPane1.setRightComponent( splitPane2 );
        final JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setHorizontalScrollBarPolicy( 32 );
        splitPane2.setRightComponent( scrollPane3 );
        methodParametersTable.setAutoResizeMode( 2 );
        methodParametersTable.setEnabled( false );
        scrollPane3.setViewportView( methodParametersTable );
        final JScrollPane scrollPane4 = new JScrollPane();
        scrollPane4.setHorizontalScrollBarPolicy( 32 );
        scrollPane4.setVerticalScrollBarPolicy( 20 );
        splitPane2.setLeftComponent( scrollPane4 );
        methodAncestorsTable.setEnabled( false );
        methodAncestorsTable.setShowHorizontalLines( true );
        methodAncestorsTable.setShowVerticalLines( true );
        scrollPane4.setViewportView( methodAncestorsTable );
        triggersPanel = new JPanel();
        triggersPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        mainTabbedPane.addTab( "Triggers", triggersPanel );
        triggersTabPane = new JTabbedPane();
        triggersTabPane.setTabPlacement( 1 );
        triggersPanel.add( triggersTabPane,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null,
                        new Dimension( 200, 200 ), null, 0, false ) );
        triggerSummaryPanel = new JPanel();
        triggerSummaryPanel.setLayout( new GridLayoutManager( 8, 3, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        triggersTabPane.addTab( "Summary", triggerSummaryPanel );
        final JLabel label3 = new JLabel();
        label3.setText( "Trigger Manager Instances Created:" );
        triggerSummaryPanel.add( label3,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final Spacer spacer1 = new Spacer();
        triggerSummaryPanel.add( spacer1,
                new GridConstraints( 0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false ) );
        final Spacer spacer2 = new Spacer();
        triggerSummaryPanel.add( spacer2,
                new GridConstraints( 7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1,
                        GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false ) );
        final JLabel label4 = new JLabel();
        label4.setText( "Instantiation Overhead:" );
        triggerSummaryPanel.add( label4,
                new GridConstraints( 1, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final JLabel label5 = new JLabel();
        label5.setText( "mxMain Invocations:" );
        triggerSummaryPanel.add( label5,
                new GridConstraints( 2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final JLabel label6 = new JLabel();
        label6.setText( "Invocation Overhead:" );
        triggerSummaryPanel.add( label6,
                new GridConstraints( 3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        triggerManagerInstancesLabel = new JLabel();
        triggerManagerInstancesLabel.setText( "" );
        triggerSummaryPanel.add( triggerManagerInstancesLabel,
                new GridConstraints( 0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        triggerManagerInstantiationOverheadLabel = new JLabel();
        triggerManagerInstantiationOverheadLabel.setText( "" );
        triggerSummaryPanel.add( triggerManagerInstantiationOverheadLabel,
                new GridConstraints( 1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        triggerManagerMxMainInvocationsLabel = new JLabel();
        triggerManagerMxMainInvocationsLabel.setText( "" );
        triggerSummaryPanel.add( triggerManagerMxMainInvocationsLabel,
                new GridConstraints( 2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        triggerManagerInvocationOverheadLabel = new JLabel();
        triggerManagerInvocationOverheadLabel.setText( "" );
        triggerSummaryPanel.add( triggerManagerInvocationOverheadLabel,
                new GridConstraints( 3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final JLabel label7 = new JLabel();
        label7.setText( "Active Triggers Fired:" );
        triggerSummaryPanel.add( label7,
                new GridConstraints( 4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        activeTriggersFiredLabel = new JLabel();
        activeTriggersFiredLabel.setText( "" );
        triggerSummaryPanel.add( activeTriggersFiredLabel,
                new GridConstraints( 4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final JLabel label8 = new JLabel();
        label8.setText( "Inactive Triggers Encounted:" );
        triggerSummaryPanel.add( label8,
                new GridConstraints( 5, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        inactiveTriggerEncounteredLabel = new JLabel();
        inactiveTriggerEncounteredLabel.setText( "" );
        triggerSummaryPanel.add( inactiveTriggerEncounteredLabel,
                new GridConstraints( 5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        final JLabel label9 = new JLabel();
        label9.setText( "Missing Trigger Program Parameters:" );
        triggerSummaryPanel.add( label9,
                new GridConstraints( 6, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        missingTriggerProgramParametersLabel = new JLabel();
        missingTriggerProgramParametersLabel.setText( "" );
        triggerSummaryPanel.add( missingTriggerProgramParametersLabel,
                new GridConstraints( 6, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                        GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
                        false ) );
        activeTriggerPanel = new JPanel();
        activeTriggerPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        triggersTabPane.addTab( "Active", activeTriggerPanel );
        final JScrollPane scrollPane5 = new JScrollPane();
        activeTriggerPanel.add( scrollPane5,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                        0, false ) );
        activeTriggerTable = new JTable();
        scrollPane5.setViewportView( activeTriggerTable );
        inactiveTriggerPanel = new JPanel();
        inactiveTriggerPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        triggersTabPane.addTab( "Inactive", inactiveTriggerPanel );
        final JScrollPane scrollPane6 = new JScrollPane();
        inactiveTriggerPanel.add( scrollPane6,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                        0, false ) );
        inactiveTriggerTable = new JTable();
        scrollPane6.setViewportView( inactiveTriggerTable );
        missingTriggerPanel = new JPanel();
        missingTriggerPanel.setLayout( new GridLayoutManager( 1, 1, new Insets( 0, 0, 0, 0 ), -1, -1 ) );
        triggersTabPane.addTab( "Missing", missingTriggerPanel );
        final JScrollPane scrollPane7 = new JScrollPane();
        missingTriggerPanel.add( scrollPane7,
                new GridConstraints( 0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null,
                        0, false ) );
        missingTriggerTable = new JTable();
        scrollPane7.setViewportView( missingTriggerTable );
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$()
    { return mainPanel; }

    private class XExecutionNodeFilter implements Filter<XExecutionNode>
    {
        // this method references deprecated enum values on purpose
        // for backward compatibility only
        @SuppressWarnings( "deprecation" )
        public boolean accept( XExecutionNode value )
        {
            boolean accept;
            ExecutionNode.Type type = ExecutionNode.Type.valueOf( value.getType() );
            // check against the check box filters
            switch ( type )
            {
                case LG_CNS_API:
                case MX_API:
                case API: // for backward compatibility with older version
                    accept = apiCheckBox.isSelected();
                    break;
                case TRIGGER_MANAGER:
                    accept = triggerManagerCheckBox.isSelected();
                    break;
                case JPO:
                    accept = jpoCheckBox.isSelected();
                    break;
                case JSP:
                    accept = jspCheckBox.isSelected();
                    break;
                case JAVA_TRIGGER:
                case TCL_TRIGGER:
                case TRIGGER: // for backward compatibility with older version
                    accept = triggersCheckBox.isSelected();
                    break;
                default:
                    accept = true;
            }

            // the timing filter, but always keep root nodes
            if ( accept && type != ExecutionNode.Type.ROOT )
            {
                accept = (value.getAdjustedTime() / 1000000) >= timingSlider.getValue();
            }

            return accept;
        }
    }

    private class CallStackTableFilter
    {
        private void filter()
        {
            int selectedRow = callStackTable.getSelectedRow();
            if ( selectedRow != -1 )
            {
                // try to keep track of the selected item in the table. there
                // is a bug in one of these methods where it sometimes selects
                // the wrong row, but I'm now quite sure why at this point
                String parentThreadName = getParentThreadName( selectedRow );
                int orderOfSelectedRow = getOrderOfSelectedRow( selectedRow );

                apply();

                int newSelectedRow = getNewSelectedRowIndex( parentThreadName, orderOfSelectedRow );
                callStackTable.setRowSelectionInterval( newSelectedRow, newSelectedRow );
            }
            else
            {
                apply();
            }
        }

        private int getNewSelectedRowIndex( String parentThreadName, int orderOfSelectedRow )
        {
            int newSelectedRow = 0;

            boolean startLooking = false;

            for ( int i = 0; i < filteredNodes.size(); i++ )
            {
                XExecutionNode n = filteredNodes.get( i );

                // looking for the thread that contains the selected execution node. once we
                // find that, then we can start looking at the order of each node in that
                // thread to determine if it should be selected in the UI or not
                if ( n.getDepth() == -1 && n.getName().equals( parentThreadName ) )
                {
                    startLooking = true;
                }

                if ( startLooking )
                {
                    if ( n.getOrder() == orderOfSelectedRow )
                    {
                        // the row is still in the filtered view - select it
                        newSelectedRow = i;
                        break;
                    }
                    else if ( n.getOrder() > orderOfSelectedRow )
                    {
                        // the row is no longer in view. select the closest visible previous row
                        newSelectedRow = i - 1;
                        break;
                    }
                }
            }

            return newSelectedRow;
        }

        private void apply()
        {
            filteredNodes.onFilterModify();
            ((ThreadStackTableModel)callStackTable.getModel()).fireTableDataChanged();
        }

        private int getOrderOfSelectedRow( int selectedRow )
        {
            int orderOfSelectedRow;
            try
            {
                orderOfSelectedRow = (Integer)callStackTable.getModel().getValueAt( selectedRow, 0 );
            }
            catch ( ClassCastException e )
            {
                // no value because it's a thread id
                orderOfSelectedRow = -1;
            }

            return orderOfSelectedRow;
        }

        private String getParentThreadName( int selectedRow )
        {
            // find the index of the parent thread for the selected item as the order resets with each
            String parentThreadName = null;
            for ( int i = selectedRow; i != -1; i-- )
            {
                Object order = callStackTable.getModel().getValueAt( i, 0 );
                if ( order instanceof String )
                {
                    parentThreadName = (String)callStackTable.getModel().getValueAt( i, 1 );
                }
            }

            return parentThreadName;
        }
    }
}