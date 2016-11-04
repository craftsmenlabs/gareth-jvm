package org.craftsmenlabs.gareth.execution.invoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.springframework.beans.factory.BeanExpressionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class DefinitionFactory
{
	@Autowired
	ApplicationContext applicationContext;

	/**
	 * Create a instance for particular class (only zero argument constructors supported)
	 *
	 * @param clazz
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 */
	public Object getInstanceForClass(final Class clazz)
		throws IllegalAccessException, InvocationTargetException, InstantiationException
	{

		Object definition = createDefinition(clazz);
		if (definition != null)
		{
			return definition;
		}

		Constructor constructor = null;
		Object declaringClassInstance = null;

		final boolean memberClass = clazz.isMemberClass();
		final int requiredConstructorArguments = memberClass ? 1 : 0; //

		if (memberClass)
		{
			declaringClassInstance = getInstanceForClass(clazz.getDeclaringClass());
		}
		for (final Constructor declaredConstructor : clazz.getDeclaredConstructors())
		{
			if (declaredConstructor.getGenericParameterTypes().length == requiredConstructorArguments)
			{
				constructor = declaredConstructor;
				break;
			}
		}
		// If a valid constructor is available
		if (null != constructor)
		{
			final Object instance;
			constructor.setAccessible(true);
			if (memberClass)
			{
				instance = constructor.newInstance(declaringClassInstance);
			}
			else
			{
				instance = constructor.newInstance();
			}
			return instance;
		}
		throw new InstantiationException(String.format("Class %s has no zero argument argument constructor", clazz));
	}

	private Object createDefinition(final Class clazz)
	{
		try
		{
			return applicationContext.getBean(clazz);
		}
		catch (BeanExpressionException e)
		{
			return null;
		}
	}

}
