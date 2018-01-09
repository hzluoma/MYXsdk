package com.hg6kwan.sdk.inner.utils.json;

import com.hg6kwan.sdk.inner.utils.reflection.MethodParameterDiscover;
import com.hg6kwan.sdk.inner.utils.reflection.ReflectionUtils;
import com.hg6kwan.sdk.inner.utils.reflection.TypeDiscoverInfo;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/**
 * 将json的Map或List表示方式转换为强类型
 * @author Zsh
 *
 * @param <T> 参数强类型
 */
public class EJSTypeConverter<E> {
	
	/**
	 * 创建强类型的对象
	 * @author Zsh
	 * @param dicParam
	 * @param targetType
	 * @return
	 * @throws Exception
	 */
	public E createObject(Map<Object, Object> dicParam, Class<?> targetType) throws Exception {
		if(dicParam == null) return null;
		E instance = (E)targetType.newInstance();
		
		//先看看有那些方法是符合set property方式的
		Method[] methods = targetType.getMethods();
		Map<String, Method> methodMap = new HashMap<String, Method>();  
		for (Method method : methods) {     
			if(method.getName().startsWith("set") && method.getParameterTypes() != null && method.getParameterTypes().length == 1) {
				methodMap.put(method.getName(),method);  
			}
		}
 
		for(Entry<Object, Object> entry : dicParam.entrySet()){
			String fieldName = entry.getKey().toString();
			String setMethodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1, fieldName.length());
			if(methodMap.containsKey(setMethodName)){
				Method method = methodMap.get(setMethodName);
				List<TypeDiscoverInfo> lsMethodParameter = MethodParameterDiscover.discoverParameter(method);
				if(lsMethodParameter.size() != 1)
					continue;
				TypeDiscoverInfo parameterInfo = lsMethodParameter.get(0);
				Class<?> fieldType = parameterInfo.parameterType;
				Object[] params = null;
				if(ReflectionUtils.isSimpleType(fieldType)) {
					params = new Object[]{ReflectionUtils.convertToSimpleType(entry.getValue(), fieldType)};
				} else if(fieldType.isArray()) {
					//如果是数组类型
					Class<?> arrayComponentType = fieldType.getComponentType();
					if(ReflectionUtils.isSimpleType(arrayComponentType)) {
						//如果是简单类型
						Object objArray = null;
						if(ReflectionUtils.isImplementOfInterface(entry.getValue().getClass(), Map.class)) {
							//适应asobject类型
							Map<String, Double> dicValue = (Map<String, Double>)entry.getValue();
							float[] aryFloat = new float[2];
							for(int i = 0; i < aryFloat.length; ++i) {
								if(dicValue.containsKey(String.valueOf(i))) aryFloat[i] = dicValue.get(String.valueOf(i)).floatValue();
								else aryFloat[i] = 0.0f;
							}
							objArray = aryFloat;
						} else {
							List<?> lsArray = (List<?>)entry.getValue();
							objArray = Array.newInstance(arrayComponentType, lsArray.size());
							for(int index = 0; index < lsArray.size(); index++){
								Array.set(objArray, index, ReflectionUtils.convertToSimpleType(lsArray.get(index), arrayComponentType));
							}
						}
						params = new Object[]{objArray};
					} else {
						//如果是复杂类型
						params = new Object[]{createList((List<Map<Object,Object>>)entry.getValue(), arrayComponentType).toArray()};
					}
				} else if(ReflectionUtils.isImplementOfInterface(fieldType, List.class)){
					Class<?> strongType = parameterInfo.typeArgumentList.get(0);
					Object ret = null;
					if(ReflectionUtils.isSimpleType(strongType)) {
						//如果是简单类型
						if(ReflectionUtils.isSimpleType(entry.getValue().getClass())) {
							ret = createSimpleList(entry.getValue(), strongType);
						} else {
							ret = createSimpleList((List<?>)entry.getValue(), strongType);
						}
					} else {
						//如果是复杂类型
						ret = createList((List<Map<Object,Object>>)entry.getValue(), strongType);
					}
					params = new Object[]{ret};
				
				} else if(ReflectionUtils.isImplementOfInterface(fieldType, Map.class)){
					if(ReflectionUtils.isImplementOfInterface(entry.getValue().getClass(), List.class)) {
						params = new Object[]{null};
					} else {
						params = new Object[]{createStrongTypeMap((Map<Object,Object>)entry.getValue(), parameterInfo)};
					}
				}else {
					if(entry.getValue() != null && entry.getValue().toString().equals("")) {
						params = new Object[]{null};
					} else {
						params = new Object[]{createObject((Map<Object,Object>)entry.getValue(), fieldType)};
					}
				}
				//请自行添加其它类型
				method.invoke(instance, params);
			}
		}
		return instance;
	}
	
	public Map<?, ?> createStrongTypeMap(Map<Object,Object> dicOriginal, TypeDiscoverInfo parameterInfo) throws Exception {
		Map<Object, Object> dicResult = new HashMap<Object, Object>();
		TypeDiscoverInfo valueDiscover = new TypeDiscoverInfo();
		valueDiscover.parameterType = parameterInfo.typeArgumentList.get(1);
		valueDiscover.isGeneric = true;
		if(parameterInfo.extra != null) {
			valueDiscover.typeArgumentList = parameterInfo.extra.typeArgumentList;
			valueDiscover.extra = parameterInfo.extra.extra;
		}
		for(Entry<Object, Object> entry : dicOriginal.entrySet()){
			Class<?> clzKey = parameterInfo.typeArgumentList.get(0);
			Object key = null;
			if(ReflectionUtils.isSimpleType(clzKey)) {
				key = ReflectionUtils.convertToSimpleType(entry.getKey(), clzKey);
			}
			Object value = ReflectionUtils.buildStrongTypeObject(valueDiscover, entry.getValue());
			dicResult.put(key, value);
		}
		return dicResult;
	}
	
	/**
	 * 创建强类型的list
	 * @author Zsh
	 * @param lsOriginal
	 * @param targetType
	 * @return
	 * @throws Exception
	 */
	public List<E> createList(List<Map<Object, Object>> lsOriginal, Class<?> targetType) throws Exception{		
		List<E> list = new ArrayList<E>();
 
		for(Object p : lsOriginal){
			if(ReflectionUtils.isImplementOfInterface(p.getClass(), Map.class))
				list.add(createObject((Map<Object, Object>)p, targetType));
		}
		return list;
	}
	
	/**
	 * 创建强类型的list
	 * @author Zsh
	 * @param lsOriginal
	 * @param targetType
	 * @return
	 * @throws Exception
	 */
	public List<E> createSimpleList(List<?> lsOriginal, Class<?> targetType) throws Exception{		
		List<E> list = new ArrayList<E>();
 
		for(Object p : lsOriginal){
			list.add((E)ReflectionUtils.convertToSimpleType(p, targetType));
		}
		return list;
	}
	
	/**
	 * 创建强类型的list
	 * @author Zsh
	 * @param lsOriginal
	 * @param targetType
	 * @return
	 * @throws Exception
	 */
	public List<E> createSimpleList(Object objOriginal, Class<?> targetType) throws Exception{		
		List<E> list = new ArrayList<E>();
		list.add((E)ReflectionUtils.convertToSimpleType(objOriginal, targetType));
		return list;
	}

}


