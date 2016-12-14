package rewards.internal.aspects

import org.apache.log4j.Logger
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import rewards.internal.monitor.MonitorFactory

// 	TODO-02: REQUIREMENT #1: Use AOP to log a message before
//           any find...() method is invoked.
//  Indicate this class is an aspect.
//	Also mark it as a component.
//	Place an @Autowired annotation on the constructor.
@Aspect
@Component
class LoggingAspect(@Autowired private val monitorFactory: MonitorFactory) {

    private val logger = Logger.getLogger(javaClass)


    //	TODO-03: Pointcut Expression
    //  Write a pointcut expression that selects only find* methods.
    //	Decide which advice type is most appropriate to the requirement.
    //
    //  HINT: The pointcut expression can be very hard to work out. If
    //  you get stuck refer to the examples in the slides and read the
    //  detailed instructions in the lab-notes.

    @Before(value = "execution(* rewards.internal.*.*Repository.find*(*))")
    fun implLogging(joinPoint: JoinPoint) {
        logger.info("'Before' advice implementation - " + joinPoint.target.javaClass + //

                "; Executing before " + joinPoint.signature.name + //

                "() method")
    }


    //	TODO-07: REQUIREMENT #2: Use AOP to time update...() methods.
    //
    //  Mark this method as around advice.  Write a pointcut
    //	expression to match on all update* methods on Repository classes.
    //
    //  HINT: Again, the pointcut expression can be hard to work out, so if
    //  you get stuck, refer to the pointcut expression you wrote above for
    //  implLogging(), this one is similar.
    //
    //  If you are really stuck, PLEASE ask a colleague or your instructor.

    @Throws(Throwable::class)
    @Around(value = "execution(public * rewards.internal.*.*Repository.update*(..))")
    fun monitor(repositoryMethod: ProceedingJoinPoint): Any {
        val name = createJoinPointTraceName(repositoryMethod)
        val monitor = monitorFactory.start(name)
        try {
            // Invoke repository method ...

            //  TODO-08: Add the logic to proceed with the target method invocation.
            //  Be sure to return the target method's return value to the caller
            //  and delete the line below.
            val proceed: Any = repositoryMethod.proceed()
            return proceed
        } finally {
            monitor.stop()
            logger.info("'Around' advice implementation - " + monitor)
        }
    }

    private fun createJoinPointTraceName(joinPoint: JoinPoint): String {
        val signature = joinPoint.signature
        val sb = StringBuilder()
        sb.append(signature.declaringType.simpleName)
        sb.append('.').append(signature.name)
        return sb.toString()
    }
}