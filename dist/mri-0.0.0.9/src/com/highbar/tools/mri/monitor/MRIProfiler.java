package com.highbar.tools.mri.monitor;

import com.highbar.tools.mri.jmx.MRIMonitor;
import com.highbar.tools.trigger.cache.TriggerProgramParameters;
import com.highbar.util.DefaultStopWatch;
import com.highbar.util.DefaultTreeNode;
import com.highbar.util.StopWatch;
import com.highbar.util.TreeNode;
import com.highbar.util.UnsynchronizedStack;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class provides profiling functionality for Matrix applications.  It can be used to track trigger and
 *   execution stack statistics.
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
public enum MRIProfiler
{
    INSTANCE;
    
    /**
     * Indicates if system-wide profiling is turned on or off
     */
    private boolean active = false;

    /**
     * This list holds the timings involved in instantiating trigger manager instances
     */
    private final List<StopWatch> triggerManagerOverhead = new ArrayList<StopWatch>();

    /**
     * Keeps track of the triggers that are referenced in the application, but have no parameters objects
     */
    private final Map<String, Integer> missingTriggers = new HashMap<String, Integer>();

    /**
     * Keeps track of trigger executions
     */
    private final Map<TriggerProgramParameters, Integer> triggers = new HashMap<TriggerProgramParameters, Integer>();

    /**
     * This map holds all execution node information for the system. Each key is mapped to a list
     * of ExecutionNode instances, which hold timing information about each unit of execution
     */
    private final Map<ExecutionNode, List<ExecutionNode>> executionNodeMap = Collections.synchronizedMap(
            new TreeMap<ExecutionNode, List<ExecutionNode>>() );

    /**
     * Map of executions stacks (call tree), each stack is bound to its corresponding thread id
     */
    private final Map<String, UnsynchronizedStack<TreeNode<ExecutionNode>>> executionStackMap = Collections.synchronizedMap(
            new TreeMap<String, UnsynchronizedStack<TreeNode<ExecutionNode>>>() );
    
    /**
     * Roots of each call tree (there is one per thread)
     */
    private Set<TreeNode<ExecutionNode>> rootExecutionNodes = Collections.synchronizedSet(
            new HashSet<TreeNode<ExecutionNode>>() );

    MRIProfiler()
    {
        // register Management Bean enabling control via JConsole
        try
        {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("com.highbar.tools.mri.jmx:type=MRIMonitor");
            MRIMonitor mbean = new MRIMonitor();
            mbs.registerMBean(mbean, name);
        }
        catch( JMException e )
        {
            throw new ExceptionInInitializerError( e );
        }
    }

    public StopWatch addTriggerManagerInstance()
    {
        StopWatch stopWatch = new DefaultStopWatch();
        triggerManagerOverhead.add( stopWatch );
        return stopWatch;
    }

    /**
     * Pushes an execution node onto the execution stack
     * @param executionNode the ExecutionNode to add to the execution log
     * @return the stop watch bound to the execution node.  this should be started and stopped
     *         by the caller at the appropriate times to gather timing information
     */
    public StopWatch push( ExecutionNode executionNode )
    {
        // enables tracking of how many nodes per type have been executed
        addNodeToList( executionNode );
        // enables tracking of the call stack
        addNodeToStack( executionNode );

        return executionNode.getStopWatch();
    }

    /**
     * Pops an execution node from the execution stack
     */
    public void pop()
    {
        UnsynchronizedStack<TreeNode<ExecutionNode>> executionStack = executionStackMap.get(
                Thread.currentThread().toString() );

        if ( executionStack == null || executionStack.isEmpty() )
        {
            throw new IllegalStateException(
                    "pop() without push() called for execution stack. ThreadId: " + Thread.currentThread() );
        }

        executionStack.pop();
    }

    /**
     * @return the set of execution nodes that have been profiled.  Each root execution node
     *         represents a thread instance
     */
    public Set<TreeNode<ExecutionNode>> getRootExecutionNodes()
    {
        return rootExecutionNodes;
    }

    /**
     * @return a map of execution nodes - each execution node is mapped to a list of
     * matching execution nodes which enables calculation of total execution time and
     * total number of invocations for each method
     */
    public Map<ExecutionNode, List<ExecutionNode>> getExecutionNodeMap()
    {
        return new TreeMap<ExecutionNode,List<ExecutionNode>>( executionNodeMap );
    }

    /**
     * Turns profiling on or off.
     * @param active true to turn profiling on, false to turn it off
     */
    public void setActive( boolean active )
    {
        this.active = active;
    }

    /**
     * @return the current status of the profiler.  true if profiling is on, false otherwise
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Clears all data structures that track profiling information
     */
    public void clear()
    {
        triggerManagerOverhead.clear();
        executionNodeMap.clear();
        executionStackMap.clear();
        missingTriggers.clear();
        rootExecutionNodes.clear();
        triggers.clear();
    }

    /**
     * @return the list of stop watches that have recorded trigger manager invocation time
     */
    public List<StopWatch> getTriggerManagerOverhead()
    {
        return new ArrayList<StopWatch>( triggerManagerOverhead );
    }

    /**
     * Adds the name of a trigger to the list of triggers that don't exist
     * @param name the name of the missing trigger
     */
    public void addMissingTrigger( String name )
    {
        Integer count = missingTriggers.get( name );

        if ( count == null )
        {
            count = 0;
        }

        missingTriggers.put( name, ++count );
    }

    /**
     * @return the list of missing triggers that have been encountered during profiling
     */
    public Map<String, Integer> getMissingTriggers()
    {
        return new TreeMap<String, Integer>( missingTriggers );
    }

    /**
     * Adds a trigger to the list of triggers that have been encountered during profiling
     * @param trigger the trigger that has been invoked
     */
    public void addTrigger( TriggerProgramParameters trigger )
    {
        Integer count = triggers.get( trigger );

        if ( count == null )
        {
            count = 0;
        }

        triggers.put( trigger, ++count );
    }

    /**
     * @return the set of triggers that have been encountered during profiling.  Each trigger is
     * mapped to an integer value which indicates the number of invocations for that trigger
     */
    public Map<TriggerProgramParameters, Integer> getTriggers()
    {
        return new TreeMap<TriggerProgramParameters, Integer>( triggers );
    }

    /**
     * This method looks at the execution tree to see if the direct parent of the current execution node
     * is the trigger manager JPO.  If so, it means that the current node was invoked by the trigger
     * manager and is thus a Java trigger
     * @return true if the current node was invoked by the trigger manager, false otherwise
     */
    public boolean isJavaTrigger()
    {
        boolean isTrigger = false;

        UnsynchronizedStack<TreeNode<ExecutionNode>> executionStack = executionStackMap.get(
                Thread.currentThread().toString() );

        if ( executionStack != null && executionStack.size() > 1 )
        {
            TreeNode<ExecutionNode> parent = executionStack.peek();

            if ( parent.getObject().getName().equals( "matrix.db.JPOSupport" ) &&
                    parent.getObject().getMethod().startsWith( "invokeObject" ) )
            {
                TreeNode<ExecutionNode> grandParent = parent.getParent();
                isTrigger = grandParent.getObject().getType() == ExecutionNode.Type.TRIGGER_MANAGER &&
                        grandParent.getObject().getMethod().startsWith( "mxMain" );
            }
        }

        return isTrigger;
    }

    private void addNodeToStack( ExecutionNode executionNode )
    {
        UnsynchronizedStack<TreeNode<ExecutionNode>> executionStack;

        synchronized( executionStackMap )
        {
            executionStack = executionStackMap.get( executionNode.getThreadId() );
            if ( executionStack == null )
            {
                executionStack = getNewExecutionStack();
                executionStackMap.put( executionNode.getThreadId(), executionStack );
            }
        }

        TreeNode<ExecutionNode> parent = executionStack.peek();
        DefaultTreeNode<ExecutionNode> child = new DefaultTreeNode<ExecutionNode>( parent, executionNode );
        executionStack.push( child );
    }

    private void addNodeToList( ExecutionNode executionNode )
    {
        List<ExecutionNode> executionNodes;

        synchronized( executionNodeMap )
        {
            executionNodes = executionNodeMap.get( executionNode );
            if ( executionNodes == null )
            {
                executionNodes = new ArrayList<ExecutionNode>();
                executionNodeMap.put( executionNode, executionNodes );
            }
        }

        executionNodes.add( executionNode );
    }

    private UnsynchronizedStack<TreeNode<ExecutionNode>> getNewExecutionStack()
    {
        ExecutionNode nullExecutionNode = new ExecutionNode( ExecutionNode.Type.ROOT, "Root", null, null );

        TreeNode<ExecutionNode> rootExecutionNode =
                new DefaultTreeNode<ExecutionNode>( nullExecutionNode );

        UnsynchronizedStack<TreeNode<ExecutionNode>>executionStack =
                new UnsynchronizedStack<TreeNode<ExecutionNode>>();

        executionStack.push( rootExecutionNode );

        rootExecutionNodes.add( rootExecutionNode );

        return executionStack;
    }
}