package com.hg6kwan.sdk.inner.utils.reflection;


import java.util.List;

public class TypeDiscoverInfo {

	/**
	 * 参数的类型
	 */
	public Class<?> parameterType;
	/**
	 * 参数是否泛型 
	 */
	public boolean isGeneric;
	/**
	 * 如果是泛型参数，则依次储存泛型参数的类型
	 */
	public List<Class<?>> typeArgumentList;
	
	/**
	 * 参数的名称
	 */
	public String parameterName;
	
	/**
	 * 可能还有额外的参数
	 */
	public TypeDiscoverInfo extra;
}
