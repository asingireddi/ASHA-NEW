package com.miraclesoft.scvp.aop;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
/**
 * The Class Aop.
 *
 * @author Shanmukhavarma kalidindi
 */
@Component
@Aspect
public class Aop {
    /** The aop class. */
    private static Logger logger = LogManager.getLogger(Aop.class.getName());

    /**
     * startMethod.
     *
     * @param joinPoint The it is the joinPoint
     */
    @Before("execution(* com.miraclesoft.scvp.service.impl.*.*(..))")
    public void startMethod(final JoinPoint joinPoint) {
        logger.log(Level.INFO, "\n======> Started with method " + joinPoint.getSignature().getName());
    }

    /**
     * endMethod.
     *
     * @param joinPoint the joinPoint
     */
    @After("execution(* com.miraclesoft.scvp.service.impl.*.*(..))")
    public void endMethod(final JoinPoint joinPoint) {
        logger.log(Level.INFO, "\n======> Completed execution of method " + joinPoint.getSignature().getName());
    }

}
