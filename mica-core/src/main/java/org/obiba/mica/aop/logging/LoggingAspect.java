package org.obiba.mica.aop.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect for logging.
 */
@Aspect
public class LoggingAspect {

  @SuppressWarnings("NonConstantLogger")
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String WITHIN_EXPR
      = "within(org.obiba.mica.jpa.repository..*) || within(org.obiba.mica.service..*)";

  @AfterThrowing(pointcut = WITHIN_EXPR, throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    logger.error("Exception in {}.{}() with cause = {}", joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(), e.getCause());
  }

  @Around(WITHIN_EXPR)
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    logger.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    try {
      Object result = joinPoint.proceed();
      logger.debug("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(), result);
      return result;
    } catch(IllegalArgumentException e) {
      logger.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
          joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
      throw e;
    }
  }
}