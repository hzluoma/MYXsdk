package com.hg6kwan.sdk.inner.utils.reflection;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MethodParameterDiscover {

	/**
	 * 发现方法的参数类型
	 * @author Zsh
	 * @param method
	 * @return
	 */
	public static List<TypeDiscoverInfo> discoverParameter(Method method) {
		Type[] paramTypeList = method.getGenericParameterTypes();
		List<TypeDiscoverInfo> lsRet = new ArrayList<TypeDiscoverInfo>();
        for (Type paramType : paramTypeList) {
        	// 参数类型
            TypeDiscoverInfo discoverInfo = ReflectionUtils.discoverType(paramType);
            lsRet.add(discoverInfo);
        }
        
        return lsRet;
	}
	
	/**
	 * 发现方法的返回值类型
	 * @author Zsh
	 * @param method
	 * @return
	 */
	public static TypeDiscoverInfo discoverReturnType(Method method) {
		Type returnType = method.getGenericReturnType();
        return ReflectionUtils.discoverType(returnType);
	}
}
