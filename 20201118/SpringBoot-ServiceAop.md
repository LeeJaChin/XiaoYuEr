#springboot-service-aop
```java

@Configuration
public class ServiceAdvisorConfig {

    @Value("${dao.aop.execution:}")
    private String execution = "execution(public * com.mongodao.dao.impl.*.save*(.)) or execution(public * com.mongodao.dao.impl.*.insert*(.)) or execution(public * com.mongodao.dao.impl.*.add*(.)) or execution(public * com.mongodao.dao.impl.*.update*(.)) or execution(public * com.mongodao.dao.impl.*.findAndModify*(.))  or execution(public * com.mongodao.dao.impl.*.modify*(.))";

    @Bean
    @ConditionalOnProperty(prefix = "dao.aop" , name={"execution"} )
    public AspectJExpressionPointcutAdvisor configurabledvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(execution);
        advisor.setAdvice((Advice) new ServiceMethodInterceptor());
        return advisor;
    }


}
```
```java

@Slf4j
public class ServiceMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object[] paramArray = methodInvocation.getArguments();
        String account = SessionUtils.getCurrentUseAccountNulltoSysUser();
        String methodName = methodInvocation.getMethod().getName();
        if(methodName.startsWith("save") || methodName.startsWith("insert") || methodName.startsWith("add")) {
            paramArray = this.invokeSaveParameter(paramArray,account);
        } else if(methodName.startsWith("update") ||  methodName.startsWith("findAndModify") ||  methodName.startsWith("modify") || methodName.startsWith("bulkUpateForOne")  || methodName.startsWith("bulkUpateForMulti") ) {
            paramArray = this.invokeUpdateParameter(paramArray,account);
        }

        Object returnObj = methodInvocation.proceed();
        return returnObj;
    }
}
```