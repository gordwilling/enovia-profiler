package com.highbar.tools.mri.aspects;

import com.highbar.tools.mri.monitor.MRIProfiler;
import com.highbar.tools.mri.monitor.ExecutionNode;
import com.highbar.tools.mri.monitor.JPO;
import com.highbar.util.StopWatch;
import com.highbar.tools.trigger.cache.TriggerProgramParameters;
import com.highbar.tools.trigger.cache.TriggerProgramParametersCache;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.JoinPoint;
import matrix.db.Context;

import java.util.List;

/**
 * <dl>
 * <dt><b>Description:</b>
 * <dd>
 *   This class defines the aspects and advice used by the profiler.  For greater flexibility, it should
 *   be reworked to be completely configurable - hard-coded pointcuts in this situation are stupid!
 * </dd>
 * </dt>
 * <dt><b>Copyright</b><dd>&copy 2009 Highbar Software Corporation</dd></dt>
 * </dl>
 *
 * @author Gordon Wallace - gw@highbar.com
 * @version 1.0
 */
@Aspect
public class MRIAspect
{
    private static final String JPO_TRIGGER_MANAGER = "emxTriggerManager";
    private static final String MX_MAIN = "mxMain";
    private static final String TCL_TRIGGER_WRAPPER = "execute program emxTriggerWrapper.tcl ";

    /**
     * Pointcut for jsp invocations
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(* org.apache.jsp..*._jspService(..)) && if()")
    public static boolean jsp_service()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for JPO invocations
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(* matrix.db.JPO*.invoke(..)) && if()")
    public static boolean jpo_invoke()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for JPO instantiation
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(Object matrix.db.JPOSupport*.newInstance(String,matrix.db.Context*,String[],int,boolean)) && if()")
    public static boolean jpoSupport_newInstance()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for core JPO invocation
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(Object matrix.db.JPOSupport*.invokeObject(Object, String, matrix.db.Context*, String[], boolean)) && if()")
    public static boolean jpoSupport_invokeObject()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for invocation of a JPO method
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(* *_mxJPO*.*(..)) && if()")
    public static boolean jpo_method()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for main database access methods.
     * @return true if the pointcut is enabled
     */
    @Pointcut("if() && (execution(public * matrix.db.BusinessObject*.*(..)) " +
            "|| execution(public * matrix.db.Relationship*.*(..)) " +
            "|| execution(public * matrix.db.MQLCommand*.executeCommand(..))" +
            "|| execution(public * matrix.db.Query*.select*(..))" +
            "|| execution(public * matrix.db.JPOSupport*.invokeObject(..))" +
            "|| execution(public * matrix.db.JPO*.packArgs(..))" +
            "|| execution(public * matrix.db.JPO*.unpackArgs(..)))")
    public static boolean mxAPI()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for LG CNS custom code
     * @return true if the pointcut is enabled
     */
    @Pointcut("if() &&  execution(public * lge..*.*(..))")
    public static boolean lgeAPI()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Pointcut for MQL command execution
     * @return true if the pointcut is enabled
     */
    @Pointcut("execution(boolean matrix.db.MQLCommand*.executeCommand(matrix.db.Context*,String)) && if()")
    public static boolean triggerManager_mqlCommand()
    {
        return MRIProfiler.INSTANCE.isActive();
    }

    /**
     * Advice for matrix-api methods.  This advice merely wraps a timer around the original method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "mxAPI()" )
    public Object mxAPI( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        return proceed( joinPoint, getExecutionNode( joinPoint, ExecutionNode.Type.MX_API ) );
    }

    /**
     * Advice for LG CNS methods.  This advice merely wraps a timer around the original method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "lgeAPI()" )
    public Object lgeAPI( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        return proceed( joinPoint, getExecutionNode( joinPoint, ExecutionNode.Type.LG_CNS_API ) );
    }

    /**
     * Advice for JPO instantiation .  This advice identifies trigger manager instantiations and
     * wraps a timer around the original method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "jpoSupport_newInstance()" )
    public Object jpoSupport_newInstance( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        Object returnValue;

        String jpoName = (String)joinPoint.getArgs()[0];

        if ( jpoName.startsWith( JPO_TRIGGER_MANAGER ) )
        {
            StopWatch stopWatch = MRIProfiler.INSTANCE.addTriggerManagerInstance();
            stopWatch.start();
            try
            {
                returnValue = joinPoint.proceed();
            }
            finally
            {
                stopWatch.stop();
            }
        }
        else
        {
            returnValue = joinPoint.proceed();
        }

        return returnValue;
    }

    /**
     * Advice for JPO methods.  This advice identifies invocations coming from the trigger manager (thus
     * identifying Java triggers) and wraps a timer around the original method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "jpo_method()" )
    public Object jpo_method( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        ExecutionNode executionNode;

        JPO jpo = new JPO();
        jpo.setArgs( joinPoint.getArgs() );
        jpo.setName( getJPOName( joinPoint.getSignature().getDeclaringTypeName() ) );
        jpo.setMethod( getMethod( joinPoint ) );

        if ( jpo.getName().startsWith( JPO_TRIGGER_MANAGER ) )
        {
            executionNode = new ExecutionNode( ExecutionNode.Type.TRIGGER_MANAGER, jpo );
            if ( jpo.getMethod().startsWith( MX_MAIN ) )
            {
                registerTriggers( jpo );
            }
        }
        else if ( MRIProfiler.INSTANCE.isJavaTrigger() )
        {
            executionNode = new ExecutionNode( ExecutionNode.Type.JAVA_TRIGGER, jpo );
        }
        else
        {
            executionNode = new ExecutionNode( ExecutionNode.Type.JPO, jpo );
        }

        return proceed( joinPoint, executionNode );
    }

    /**
     * Advice for JSP invocations.  This advice merely wraps a timer around the jsp_service method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "jsp_service()" )
    public Object jsp_service( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        String jspName = getJSPName( joinPoint.getSignature().getDeclaringTypeName() );
        Object[] args = joinPoint.getArgs();
        ExecutionNode executionNode = new ExecutionNode( ExecutionNode.Type.JSP, jspName, "jsp_service", args );
        return proceed( joinPoint, executionNode );
    }

    /**
     * Advice for MQL.executeCommand() methods.  This advice identifies TCL trigger executions and
     * wraps a timer around the original method
     * @param joinPoint the join point representing the advised method
     * @return the result of the method execution
     * @throws Throwable if the advised method threw something
     */
    @Around( "triggerManager_mqlCommand()" )
    public Object triggerManager_mqlCommand( ProceedingJoinPoint joinPoint ) throws Throwable
    {
        String command = (String)joinPoint.getArgs()[1];
        if ( command != null && command.startsWith( TCL_TRIGGER_WRAPPER ) )
        {
            String programName = getTCLProgramName( command );
            ExecutionNode executionNode = new ExecutionNode( ExecutionNode.Type.TCL_TRIGGER, programName, "-", null );
            return proceed( joinPoint, executionNode );
        }
        else
        {
            return joinPoint.proceed();
        }
    }

    private static ExecutionNode getExecutionNode( ProceedingJoinPoint joinPoint, ExecutionNode.Type type )
    {
        Object[] args = joinPoint.getArgs();
        String name = joinPoint.getSignature().getDeclaringTypeName();
        String method = getMethod( joinPoint );

        return new ExecutionNode( type, name, method, args );
    }

    private static Object proceed( ProceedingJoinPoint joinPoint, ExecutionNode executionNode )
            throws Throwable
    {
        Object returnValue;
        StopWatch stopWatch = MRIProfiler.INSTANCE.push( executionNode );
        stopWatch.start();
        try
        {
            returnValue = joinPoint.proceed();
        }
        finally
        {
            stopWatch.stop();
            MRIProfiler.INSTANCE.pop();
        }

        return returnValue;
    }

    private static void registerTriggers( JPO jpo )
    {
        Context context = (Context)jpo.getArgs()[0];
        String[] parameters = (String[])jpo.getArgs()[1];

        boolean isProfilerActive = MRIProfiler.INSTANCE.isActive();

        // ensure profiler is off while we cache the trigger parameter objects
        MRIProfiler.INSTANCE.setActive( false );
        for ( String triggerName : parameters )
        {
            List<TriggerProgramParameters> triggers = TriggerProgramParametersCache.INSTANCE.getTriggerProgramParameters(
                    context, triggerName );

            if ( triggers == null )
            {
                MRIProfiler.INSTANCE.addMissingTrigger( triggerName );
            }
            else
            {
                for ( TriggerProgramParameters trigger : triggers )
                {
                    MRIProfiler.INSTANCE.addTrigger( trigger );
                }
            }
        }

        // return active status of profiler to its original state
        MRIProfiler.INSTANCE.setActive( isProfilerActive );
    }

    private static String getTCLProgramName( String command )
    {
        // the program name is the last single-quoted value in the command string
        StringBuilder programName = new StringBuilder( command );
        int lastQuoteIndex = programName.lastIndexOf( "'" );
        programName.delete( lastQuoteIndex, programName.length() );
        lastQuoteIndex = programName.lastIndexOf( "'" );
        programName.delete( 0, lastQuoteIndex );
        return programName.toString();
    }

    private static String getJSPName( String jspClassName )
    {
        StringBuilder jspName = new StringBuilder( jspClassName.replaceAll( "\\.", "/" ) );
        jspName.delete( 0, "org.apache.jsp".length() );
        jspName.setCharAt( jspName.length() - 4, '.' );
        return jspName.toString();
    }

    private static String getMethod( JoinPoint joinPoint )
    {
        String type = joinPoint.getSignature().getDeclaringTypeName();
        StringBuilder method = new StringBuilder( joinPoint.getSignature().toString() );

        // delete the return type from the signature
        method.delete( 0, method.indexOf( " " ) + 1 );

        // delete the type from the signature
        method.delete( 0, type.length() + 1 );

        return method.toString();
    }

    private static String getJPOName( String longName )
    {
        return longName.substring( 0, longName.indexOf( "_mxJPO" ) );
    }
}