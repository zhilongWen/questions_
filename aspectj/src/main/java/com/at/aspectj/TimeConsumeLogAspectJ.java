package com.at.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @create 2023-07-16
 */
@Aspect
public class TimeConsumeLogAspectJ {

    //通过ThreadLocal隔离不同线程的变量
    ThreadLocal<Long> timeRecord = new ThreadLocal<>();

    /**
     * 配置切面
     */
    @Pointcut("execution(* *(..)) && @annotation(com.at.aspectj.TimeConsumeLogAnnotation)")
    public void jointPoint() {
    }

    /**
     * 前者通知
     * @param joinPoint
     */
    @Before("jointPoint()")
    public void before(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();


        System.out.println("方法" + method.getName() + "开始");

        Object[] args = joinPoint.getArgs();
        System.out.println("方法" + method.getName() + " 的参数值：" + Arrays.toString(args));

        String[] parameterNames = signature.getParameterNames();
        System.out.println("方法" + method.getName() + " 的参数名称：" + Arrays.toString(parameterNames));


        timeRecord.set(System.currentTimeMillis());
    }

    /**
     * 最终通知
     * @param joinPoint
     */
    @After("jointPoint()")
    public void after(JoinPoint joinPoint) {
        long beginTime = timeRecord.get();
        System.out.println("方法" + joinPoint.getSignature().getName() + "结束,耗时" + (System.currentTimeMillis() - beginTime) + "ms");

    }

//    @AfterReturning(pointcut = "jointPoint()",returning = "retVal")
//    public void afterReturning(Object retVal) {
//
//        if (retVal != null){
//            Class<?> aClass = retVal.getClass();
//            System.out.println("return class: " + aClass);
//
//            System.out.println("------ " + retVal);
//
//        }
//
//    }

    /**
     * 后置通知
     * @param joinPoint
     * @param retVal
     */
    @AfterReturning(pointcut = "jointPoint()", returning = "retVal")
    public void afterReturning(JoinPoint joinPoint, Object retVal) {

        if (retVal != null) {

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            Class<?> aClass = retVal.getClass();
            System.out.println("return class: " + aClass);

            System.out.println("---- method = "+ method.getName() +" -- " + retVal);

        }
    }


    /**
     * 异常通知
     * @param e
     */
    @AfterThrowing(value = "jointPoint()",throwing = "e")
    public void myAfterThrowing(JoinPoint joinPoint,Exception e){

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        System.out.println("method = "+method+", Exception: "+ e);
    }


}
