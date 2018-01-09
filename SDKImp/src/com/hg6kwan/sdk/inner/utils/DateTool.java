package com.hg6kwan.sdk.inner.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间相关工具 日期格式 日期计算
 * @author Zsh
 *
 */

public class DateTool {
	    public static int yyyy_MM_dd = 1;

	    public static int yyyy_M_d = 2;

	    public static int yy_MM_dd = 3;

	    public static int yy_M_d = 4;

	    public static int yyyy_MM_dd_HH_mm_ss = 5;

	    public static int yyyy_M_d_H_m_s = 6;

	    public static int yy_MM_dd_HH_mm_ss = 7;

	    public static int yy_M_d_H_m_s = 8;

	    public static int yyyy = 9;
	    
	    public static int yyyy_MM = 10;
	    
	    public static int yyyyMMdd = 11;
	    
	    public static int yyyyMM = 12;
	    
	    public static int yyMMddHHmmss = 13;
	    
	    public static int yyyyMMddHHmmss = 14;
	    
	    public static int yyyy_MM_dd_HH_mm_ss_SSSZ = 15;
	    
	    public static int yyMMddHHmmssSSS = 16;
	    
	    /**
	     * 输入String类型的日期与格式代号，以String类型返回需要的格式
	     * @param date String 类型的日期
	     * @param i 格式类型 可以这样调用DateTool.yyyy_MM_dd
	     * */
	    public static String getStringDateFormat(String date, int i) throws Exception {
	        SimpleDateFormat simpledateformat = new SimpleDateFormat();
	        switch (i) {
	        case 1: // '\001'
	            simpledateformat.applyPattern("yyyy-MM-dd");
	            break;

	        case 2: // '\002'
	            simpledateformat.applyPattern("yyyy-M-d");
	            break;

	        case 3: // '\003'
	            simpledateformat.applyPattern("yy-MM-dd");
	            break;

	        case 4: // '\004'
	            simpledateformat.applyPattern("yy-M-d");
	            break;

	        case 5: // '\005'
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
	            break;

	        case 6: // '\006'
	            simpledateformat.applyPattern("yyyy-M-d H:m:s");
	            break;

	        case 7: // '\007'
	            simpledateformat.applyPattern("yy-MM-dd HH:mm:ss");
	            break;

	        case 8: // '\b'
	            simpledateformat.applyPattern("yy-M-d H:m:s");
	            break;
	        case 9: // 
	            simpledateformat.applyPattern("yyyy");
	            break;
	        case 10: // 
	            simpledateformat.applyPattern("yyyy-MM");
	            break;
	        case 11: // 
	            simpledateformat.applyPattern("yyyyMMdd");
	            break;
	        case 12: // 
	            simpledateformat.applyPattern("yyyyMM");
	            break;
	        case 13: // 
	            simpledateformat.applyPattern("yyMMddHHmmss");
	            break;
	        case 14: // 
	            simpledateformat.applyPattern("yyyyMMddHHmmss");
	            break;
	        case 15: // 
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
	            break;
	        case 16: // 
	            simpledateformat.applyPattern("yyMMddHHmmssSSS");
	            break;
	        } 
	        return simpledateformat.format(simpledateformat.parse(date));
	    }
	    
	    
	    
	    
	    /**
	     * 输入Date类型的日期与格式代号，以String类型返回需要的格式
	     * @param date Date 类型的日期
	     * @param i 格式类型 可以这样调用DateTool.yyyy_MM_dd
	     * */

	    public static String getStringDateFormat(Date date, int i) throws Exception {
	        SimpleDateFormat simpledateformat = new SimpleDateFormat();
	        switch (i) {
	        case 1: // '\001'
	            simpledateformat.applyPattern("yyyy-MM-dd");
	            break;

	        case 2: // '\002'
	            simpledateformat.applyPattern("yyyy-M-d");
	            break;

	        case 3: // '\003'
	            simpledateformat.applyPattern("yy-MM-dd");
	            break;

	        case 4: // '\004'
	            simpledateformat.applyPattern("yy-M-d");
	            break;

	        case 5: // '\005'
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
	            break;

	        case 6: // '\006'
	            simpledateformat.applyPattern("yyyy-M-d H:m:s");
	            break;

	        case 7: // '\007'
	            simpledateformat.applyPattern("yy-MM-dd HH:mm:ss");
	            break;

	        case 8: // '\b'
	            simpledateformat.applyPattern("yy-M-d H:m:s");
	            break;
	        case 9: // 
	            simpledateformat.applyPattern("yyyy");
	            break;
	        case 10: // 
	            simpledateformat.applyPattern("yyyy-MM");
	            break;
	        case 11: // 
	            simpledateformat.applyPattern("yyyyMMdd");
	            break;
	        case 12: // 
	            simpledateformat.applyPattern("yyyyMM");
	            break;
	        case 13: // 
	            simpledateformat.applyPattern("yyMMddHHmmss");
	            break;
	        case 14: // 
	            simpledateformat.applyPattern("yyyyMMddHHmmss");
	            break;
	        case 15: // 
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
	            break;
	        case 16: // 
	            simpledateformat.applyPattern("yyMMddHHmmssSSS");
	            break;
	        }
	        return simpledateformat.format(date);
	    }
	    
	    
	    
	    
	    /**
	     * 输入String类型的日期与格式代号，以Date类型返回需要的格式
	     * @param date String 类型的日期
	     * @param i 格式类型 可以这样调用DateTool.yyyy_MM_dd
	     * */
	    public static Date getDateFormat(String date, int i) throws Exception {
	        SimpleDateFormat simpledateformat = new SimpleDateFormat();
	        switch (i) {
	        case 1: // '\001'
	            simpledateformat.applyPattern("yyyy-MM-dd");
	            break;

	        case 2: // '\002'
	            simpledateformat.applyPattern("yyyy-M-d");
	            break;

	        case 3: // '\003'
	            simpledateformat.applyPattern("yy-MM-dd");
	            break;

	        case 4: // '\004'
	            simpledateformat.applyPattern("yy-M-d");
	            break;

	        case 5: // '\005'
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
	            break;

	        case 6: // '\006'
	            simpledateformat.applyPattern("yyyy-M-d H:m:s");
	            break;

	        case 7: // '\007'
	            simpledateformat.applyPattern("yy-MM-dd HH:mm:ss");
	            break;

	        case 8: // '\b'
	            simpledateformat.applyPattern("yy-M-d H:m:s");
	            break;
	        case 9: // 
	            simpledateformat.applyPattern("yyyy");
	            break;
	        case 10: // 
	            simpledateformat.applyPattern("yyyy-MM");
	            break;
	        case 11: // 
	            simpledateformat.applyPattern("yyyyMMdd");
	            break;
	        case 12: // 
	            simpledateformat.applyPattern("yyyyMM");
	            break;
	        case 13: // 
	            simpledateformat.applyPattern("yyMMddHHmmss");
	            break;
	        case 14: // 
	            simpledateformat.applyPattern("yyyyMMddHHmmss");
	            break;
	        case 15: // 
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
	            break;
	        case 16: // 
	            simpledateformat.applyPattern("yyMMddHHmmssSSS");
	            break;
	        }
	        return simpledateformat.parse(date);
	    }
	    
	    
	    
	    /**
	     * 输入日期格式代号，以String类型返回需要的格式的当前日期时间
	     * @param i 格式类型 可以这样调用DateTool.yyyy_MM_dd
	     * */

	    public static String getNowDate(int i) throws Exception {
	        SimpleDateFormat simpledateformat = new SimpleDateFormat();
	        switch (i) {
	        case 1: // '\001'
	            simpledateformat.applyPattern("yyyy-MM-dd");
	            break;

	        case 2: // '\002'
	            simpledateformat.applyPattern("yyyy-M-d");
	            break;

	        case 3: // '\003'
	            simpledateformat.applyPattern("yy-MM-dd");
	            break;

	        case 4: // '\004'
	            simpledateformat.applyPattern("yy-M-d");
	            break;

	        case 5: // '\005'
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss");
	            break;

	        case 6: // '\006'
	            simpledateformat.applyPattern("yyyy-M-d H:m:s");
	            break;

	        case 7: // '\007'
	            simpledateformat.applyPattern("yy-MM-dd HH:mm:ss");
	            break;

	        case 8: // '\b'
	            simpledateformat.applyPattern("yy-M-d H:m:s");
	            break;
	        case 9: // 
	            simpledateformat.applyPattern("yyyy");
	            break;
	        case 10: // 
	            simpledateformat.applyPattern("yyyy-MM");
	            break;
	        case 11: // 
	            simpledateformat.applyPattern("yyyyMMdd");
	            break;
	        case 12: // 
	            simpledateformat.applyPattern("yyyyMM");
	            break;
	        case 13: // 
	            simpledateformat.applyPattern("yyMMddHHmmss");
	            break;
	        case 14: // 
	            simpledateformat.applyPattern("yyyyMMddHHmmss");
	            break;
	        case 15: // 
	            simpledateformat.applyPattern("yyyy-MM-dd HH:mm:ss.SSSZ");
	            break;
	        case 16: // 
	            simpledateformat.applyPattern("yyMMddHHmmssSSS");
	            break;
	        }
	        return simpledateformat.format(new Date());
	    }
	    
	    /**
	     * 
	     * 取得系统当前时间，返回类型为Date*/

	    public static Date getNowDate () throws ParseException{
	    return new Date();
	    }
	/*
	 * -----------------------------------------------------------------------------------------------------------
	 * -----------------------------------------------------------------------------------------------------------
	 */
	    
	    
	    /**
	     * 日期的计算*/
	    
	    /**
		 * 取得一天的开始时间
		 * @param date
		 * @param pattern
		 * @return
	     * @throws Exception 
		 */
		public static Date getFristTimeOfDate(Date date) throws Exception{
			String s=DateTool.getStringDateFormat(date, DateTool.yyyy_MM_dd);
			return DateTool.getDateFormat(s+" 00:00:00", DateTool.yyyy_MM_dd_HH_mm_ss);   			
		}
		
		/**
		 * 取得一天的最后时间
		 * @param date
		 * @param pattern
		 * @return
		 * @throws Exception 
		 * @throws Exception
		 */
		public static Date getLastTimeOfDate(Date date) throws Exception{
			
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(date); 
				gc.add(GregorianCalendar.DATE,1);
	            return getFristTimeOfDate(gc.getTime());   			
		}
		
		/**
		 * 取得前N天前或后的时间
		 * @param date 日期
		 * @param N date的N天 负数为前N天 正数为后N天
		 * @throws Exception
		 */
		public static Date getNDayOfDate(Date date,Integer N){
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(date); 
				gc.add(GregorianCalendar.DATE,N);
	            return gc.getTime();   			
		}
		
		/**
		 * date1与date2相差的天数
		 * date1
		 * date2
		 * @throws Exception */
		public static Long getIntervalDays(String date1,String date2) throws Exception{
			Date dt1 = DateTool.getDateFormat(date1, DateTool.yy_MM_dd); 
			Date dt2 = DateTool.getDateFormat(date2, DateTool.yy_MM_dd);
			return getIntervalDays(dt1, dt2);	
		}
		
		/**
		 * date1与date2相差的天数
		 * date1
		 * date2
		 * @throws Exception */
		public static Long getIntervalDays(Date date1,Date date2) throws Exception{
			long days=0;
			days = date1.getTime() - date2.getTime(); 
			days = days / 1000 / 60 / 60 / 24; 
			return days;	
		}
		/**
		 * 取得日期对应的星期
		 * @param date
		 */
		public static String getWeekDay(Date date){
			Calendar c = Calendar.getInstance();
			c.setTime(date); 
			int i = c.get(Calendar.DAY_OF_WEEK)-1;   
			String day[] = new String[]{"SUNDAY", "MONDAY", "TUESDAY", 
					"WEDNESDAY", "THURSDAY","FRIDAY", "SATURDAY"};   
			return day[i];
		}
		
		/**
		 * 取当前时间的秒数
		 * */
		public static Integer getSecond(){
			
			java.util.Calendar c=java.util.Calendar.getInstance();
			c.setTime(new Date());
			return c.get(java.util.Calendar.SECOND);
			
			
		}
		
		/**
		 * 将指定的天数添加到给定的日历字段中。
		 * 例: date=2007.12.1 days=2 return 2007.12.3
		 * 例: date=2007.12.1 days=-2 return 2007.11.29
		 * @param date 指定日期
		 * @param days 时间量
		 * @return 返回修改后的日期
		 */
		public static Date addDays(Date date,int days){
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date); 
			gc.add(Calendar.DAY_OF_MONTH,days);
			return gc.getTime();   			
		}
		
		/**
		 * 将指定的天数添加到给定的日历字段中。
		 * 例: date=2007.12.1 weeks=2 return 2007.12.15
		 * 例: date=2007.12.1 weeks=-2 return 2007.11.17
		 * @param date 指定日期
		 * @param days 时间量
		 * @return 返回修改后的日期
		 */
		public static Date addWeeks(Date date,int weeks){
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date); 
			gc.add(Calendar.WEEK_OF_YEAR, weeks);
			return gc.getTime();   			
		}

		/**
		 * 将指定的天数添加到给定的日历字段中。
		 * 例: date=2007.12.1 months=2 return 2008.02.1
		 * 例: date=2007.12.1 months=-2 return 2007.10.30
		 * @param date 指定日期
		 * @param days 时间量
		 * @return 返回修改后的日期
		 */
		public static Date addMonths(Date date,int months){
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date); 
			gc.add(Calendar.MONTH, months);
			return gc.getTime();   			
		}
		
		/**
		 * 将指定的分钟添加到给定的日历字段中。
		 * @param date 指定日期
		 * @param days 时间量
		 * @return 返回修改后的日期
		 */
		public static Date addMinutes(Date date,int minute){
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(date); 
			gc.add(Calendar.MINUTE, minute);
			return gc.getTime();   			
		}
}
