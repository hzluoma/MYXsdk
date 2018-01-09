package com.hg6kwan.sdk.inner.utils.reflection;


import com.hg6kwan.sdk.inner.utils.DateTool;
import com.hg6kwan.sdk.inner.utils.json.EJSTypeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**
 * 一些反射的工具类
 * @author Zsh
 *
 */
public class ReflectionUtils {
	/**
	 * 判断是否实现了某接口
	 * @author Zsh
	 * @param original
	 * @param dest
	 * @return
	 */
	public static boolean isImplementOfInterface(Class<?> original, Class<?> dest) {
		if(original == dest) return true;
		
		Class<?>[] interfaces = original.getInterfaces();
		if(interfaces != null) {
			for (Class<?> i : interfaces) {
				if(i == dest) return true;
			}
		}
		Class<?> clzSuperClass = original.getSuperclass();
		if(clzSuperClass != null && clzSuperClass != Object.class) {
			return isImplementOfInterface(clzSuperClass, dest);
		}
		
		return false;
	}
	
	/**
	 * 是否简单类型<br>
	 * 包括所有的基本类型、String和Date
	 * @author Zsh
	 * @param targetType
	 * @return
	 */
	public static boolean isSimpleType(Class<?> targetType) {
		return targetType.isPrimitive()
		||targetType == Integer.class
		||targetType == Short.class
		||targetType == Long.class
		||targetType == Float.class
		||targetType == Double.class
		||targetType == Byte.class
		||targetType == Boolean.class
		||targetType == Character.class
		||targetType.isAssignableFrom(Date.class)
		||targetType == String.class
		||targetType == BigDecimal.class;
	}
	
	/**
	 * 转换为简单类型
	 * @author Zsh
	 * @param objOriginal 原始的值 
	 * @param targetType 要转换到的类型
	 * @return
	 * @throws Exception
	 */
	public static Object convertToSimpleType(Object objOriginal, Class<?> targetType) throws Exception {
		if(targetType == int.class){
			if(objOriginal == null || objOriginal.equals("")) return 0;
			else return (int)Float.parseFloat(objOriginal.toString()); //int
		} else if(targetType == Integer.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return (int)Float.parseFloat(objOriginal.toString()); //integer
		} else if(targetType == long.class){
			if(objOriginal == null || objOriginal.equals("")) return 0f;
			else return Long.parseLong(objOriginal.toString()); // long
		} else if(targetType == Long.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return Long.parseLong(objOriginal.toString()); // Long
		} else if(targetType == short.class){
			if(objOriginal == null || objOriginal.equals("")) return 0f;
			else return Short.parseShort(objOriginal.toString()); // short
		} else if(targetType == Short.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return Short.parseShort(objOriginal.toString()); // Short
		}else if(targetType == float.class){
			if(objOriginal == null || objOriginal.equals("")) return 0f;
			else return Float.parseFloat(objOriginal.toString()); // web
		} else if(targetType == Float.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return Float.parseFloat(objOriginal.toString()); // Float
		} else if(targetType == double.class){
			if(objOriginal == null || objOriginal.equals("")) return 0;
			else return Double.parseDouble(objOriginal.toString()); // double
		} else if(targetType == Double.class) {
			if(objOriginal == null || objOriginal.equals("")) return null; 
			else return Double.parseDouble(objOriginal.toString());   //Double
		} else if(targetType == byte.class){
			if(objOriginal == null || objOriginal.equals("")) return 0x0;
			else return Byte.parseByte(objOriginal.toString()); // byte
		} else if(targetType == Byte.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return Byte.parseByte(objOriginal.toString()); // Byte
		} else if(targetType == boolean.class){
			if(objOriginal == null || objOriginal.equals("")) return false;
			else return Boolean.parseBoolean(objOriginal.toString()); // boolean
		} else if(targetType == Boolean.class) {
			if(objOriginal == null || objOriginal.equals("")) return null;
			else return Boolean.parseBoolean(objOriginal.toString()); // Byte
		} else if(targetType == char.class){
			if(objOriginal == null || objOriginal.equals("")) return '\0';
			else return objOriginal.toString().charAt(0); // char
		} else if(targetType == Character.class) {
			if(objOriginal == null || objOriginal.equals("")) return '\0';
			else return objOriginal.toString().charAt(0); // Character
		} else if(targetType.isAssignableFrom(Date.class)){
			if(objOriginal == null || objOriginal.equals("")) return null;
			return DateTool.getDateFormat(objOriginal.toString(), DateTool.yyyy_MM_dd_HH_mm_ss); // Date
		} else if(targetType == String.class) {
			return objOriginal == null ? null : objOriginal.toString(); //String
		} else if(targetType == BigDecimal.class) {
			return BigDecimal.valueOf(Double.parseDouble(objOriginal.toString()));
		}
		throw new IllegalArgumentException("参数类型不是简单类型：" + targetType.getName());
	}
	
	/**
	 * 通过名称取得方法
	 * @author Zsh
	 * @param serviceClass
	 * @param sMethodName
	 * @return
	 */
	public static Method getMethodByNameInClass(Class<?> clazz, String sMethodName) {
		Method[] methods = clazz.getMethods();
		Method method = null;
		for(Method m : methods){
			if(m.getName().equals(sMethodName)){
				method = m;
				break;
			}
		}
		return method;
	}
	
	public static TypeDiscoverInfo discoverType(Type target) {
		Type returnType = target;
		TypeDiscoverInfo discoverInfo = new TypeDiscoverInfo();
		if (returnType instanceof ParameterizedType) {
        	//如果是泛型 
        	discoverInfo.isGeneric = true;
        	discoverInfo.typeArgumentList = new ArrayList<Class<?>>();
        	Class<?> rawType = (Class<?>)((ParameterizedType)returnType).getRawType();
        	discoverInfo.parameterType = rawType;
        	
            Type[] types = ((ParameterizedType) returnType).getActualTypeArguments();
            for (Type type : types) {
            	if(type instanceof ParameterizedType) {
            		discoverInfo.typeArgumentList.add((Class<?>)((ParameterizedType)type).getRawType());
            		discoverInfo.extra = discoverType(type);
            	} else {
            		discoverInfo.typeArgumentList.add((Class<?>)type);
            	}
            }
        } else {
            discoverInfo.parameterType = (Class<?>)returnType;
        }
        return discoverInfo;
	}
	
	/**
	 * 创建强类型的对象
	 * @author Zsh
	 * @param discoverInfo
	 * @param objRealParamValue
	 * @return
	 * @throws Exception
	 */
	public static Object buildStrongTypeObject(TypeDiscoverInfo discoverInfo, Object objRealParamValue) throws Exception {
		EJSTypeConverter<Object> converter = new EJSTypeConverter<Object>();
		Class<?> currentMethodParamType = discoverInfo.parameterType;
		
		if(ReflectionUtils.isSimpleType(currentMethodParamType)) {
			//转换为简单类型
			objRealParamValue = ReflectionUtils.convertToSimpleType(objRealParamValue, currentMethodParamType);
		} else if(currentMethodParamType.isArray()) {
			//如果是数组类型
			Class<?> arrayComponentType = currentMethodParamType.getComponentType();
			//Class<?> strongType = dicParamStrongType.get(i).get(0);
			if(ReflectionUtils.isSimpleType(arrayComponentType)) {
				//如果是简单类型
				//List<?> lsArray = converter.createSimpleList((List<?>)objRealParamValue, arrayComponentType);
				List<?> lsArray = (List<?>)objRealParamValue;
				Object objArray = Array.newInstance(arrayComponentType, lsArray.size());
				for(int index = 0; index < lsArray.size(); index++){
					Array.set(objArray, index, ReflectionUtils.convertToSimpleType(lsArray.get(index), arrayComponentType));
				}
				objRealParamValue = objArray;
			} else {
				//如果是复杂类型
				objRealParamValue = converter.createList((List<Map<Object,Object>>)objRealParamValue, arrayComponentType).toArray();
			}
		} else if(ReflectionUtils.isImplementOfInterface(currentMethodParamType, Map.class)){
			//如果是一个map
			objRealParamValue = converter.createStrongTypeMap((Map<Object,Object>)objRealParamValue, discoverInfo);
			
		} else if(ReflectionUtils.isImplementOfInterface(currentMethodParamType, List.class)){
			//如果是List
			Class<?> strongType = discoverInfo.typeArgumentList.get(0);
			if(ReflectionUtils.isSimpleType(strongType)) {
				//如果是简单类型
				//这里还要兼容php的json格式
				if(ReflectionUtils.isSimpleType(objRealParamValue.getClass())) {
					objRealParamValue = converter.createSimpleList(objRealParamValue, strongType);
				} else {
					objRealParamValue = converter.createSimpleList((List<?>)objRealParamValue, strongType);
				}
			} else {
				//如果是复杂类型
				objRealParamValue = converter.createList((List<Map<Object,Object>>)objRealParamValue, strongType);
			}
		} else if(currentMethodParamType.isEnum()) {
			//如果是枚举 
			Object[] aryEnum = currentMethodParamType.getEnumConstants();
			for(Object o : aryEnum) {
				if(o.toString().equals(objRealParamValue.toString())){
					objRealParamValue = o;
					break;
				}
			}
		} else {
			//如果是普通的对象
			if(objRealParamValue != null && objRealParamValue.toString().equals("")) {
				objRealParamValue = null;
			} else {
				objRealParamValue = converter.createObject((Map<Object,Object>)objRealParamValue, currentMethodParamType);
			}
		}
		return objRealParamValue;
	}
	
	public static List<PropertyDescriptor> getPropertyDescriptors(Class<?> type) {
		List<PropertyDescriptor> lsDescriptor = new ArrayList<PropertyDescriptor>();
		Method[] aryMethod = type.getMethods();
		Map<String, Method> dicMethod = new HashMap<String, Method>();  
		for (Method method : aryMethod) {     
			if(method.getName().startsWith("set") && method.getParameterTypes() != null && method.getParameterTypes().length == 1) {
				dicMethod.put(method.getName(),method);  
			}
		}
		
        for(Method method : aryMethod) {
        	if (method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
				String name = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4, method.getName().length());
				
				PropertyDescriptor desc = new PropertyDescriptor();
				desc.setDisplayName(name);
				desc.setName(name);
				desc.setReadMethod(method);
				String setMethodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1, name.length());
				if(dicMethod.containsKey(setMethodName)){
					desc.setWriteMethod(dicMethod.get(setMethodName));
				}
				lsDescriptor.add(desc);
			} else if (method.getName().startsWith("is") && method.getParameterTypes().length == 0) {
				String name = Character.toLowerCase(method.getName().charAt(2)) + method.getName().substring(3, method.getName().length());
				PropertyDescriptor desc = new PropertyDescriptor();
				desc.setDisplayName(name);
				desc.setName(name);
				desc.setReadMethod(method);
				String setMethodName = "set" + name.substring(0,1).toUpperCase() + name.substring(1, name.length());
				if(dicMethod.containsKey(setMethodName)){
					desc.setWriteMethod(dicMethod.get(setMethodName));
				}
				lsDescriptor.add(desc);
			}
        }
        
        return lsDescriptor;
	}
	
	public static List<PropertyDescriptor> getPropertyDescriptors(Object bean) {
		return getPropertyDescriptors(bean.getClass());
	}
}
