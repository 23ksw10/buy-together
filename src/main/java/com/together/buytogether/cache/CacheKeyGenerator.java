package com.together.buytogether.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyGenerator {
	private final ExpressionParser parser = new SpelExpressionParser();
	private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

	public String generate(ProceedingJoinPoint joinPoint, String keyExpression, String cacheName) {
		Expression expression = expressionCache.computeIfAbsent(keyExpression, parser::parseExpression);

		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		StandardEvaluationContext context = new StandardEvaluationContext();

		String[] parameterNames = methodSignature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < parameterNames.length; i++) {
			context.setVariable(parameterNames[i], args[i]);
		}

		return cacheName + "::" + expression.getValue(context, String.class);
	}
}
