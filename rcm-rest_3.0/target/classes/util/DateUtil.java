package util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class DateUtil {

    /**
     * 年月日
     */
    public static final String DATEFORMAT_YYYYMM = "yyyyMM";
    public static final String DATEFORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATEFORMAT_YYYYMMDDHH = "yyyyMMddHH";
    public static final String DATEFORMAT_YYYYMMDDHHMM = "yyyyMMddHHmm";
    public static final String DATEFORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATEFORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATEFORMAT_YYYYMMDD_HH_MM_SS = "yyyyMMdd HH:mm:ss";
    public static final String DATEFORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * date转String格式化
     *
     * @param date   Date
     * @param format 格式化规则
     * @return String
     */
    public static String getDateToString(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * date转String格式化
     *
     * @param date   Date
     * @param format 格式化规则
     * @return String
     */
    public static String getOracleDateToString(Timestamp date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * date转String格式化
     * 默认格式化 yyyy-MM-dd
     *
     * @param date Date
     * @return String
     */
    public static String getDateToString(Date date) {
        return getDateToString(date, DATEFORMAT_YYYY_MM_DD);
    }

    /**
     * 转换String为date
     *
     * @param strDate 时间字符串
     * @param pattern SimpleDateFormat 格式
     * @return
     * @throws ParseException 转换异常
     */
    public static Date convertStrToDate(String strDate, String pattern) throws ParseException {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(strDate);
    }

    public static String dateTimeFormat(Date systemTime, String dateRegex1, String dateRegex2) {
        if (StringUtils.isNotBlank(dateRegex2)) {
            return getDateToString(systemTime, dateRegex2);
        } else {
            return getDateToString(systemTime, dateRegex1);
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Timestamp getCurrentDate() {
        return new Timestamp(new Date().getTime());

    }

    /**
     * 比较两个日期时间的大小(先后)
     *
     * @param d1 日期时间1
     * @param d2 日期时间2
     * @return 若d1比d2早则返回负值，若d1比d2晚则返回正值，若d1与d2相等则返回0，若d1或d2任一者为null则返回null
     */
    public static Integer cmpDatetime(Date d1, Date d2) {
        Integer result = null;
        if (d1 != null && d2 != null) {
            return d1.compareTo(d2);
        }
        return result;
    }

    /**
     * 检查两个时间段是否有重叠<br>
     * 注意若时间段本身范围有误(开始时间大于结束时间)，则会交换开始时间与结束时间再做检查<br>
     * 检查时遵循以下规则：<br>
     * 1.将空值视为无限制(开始时间为空被视为开始于无限久远之前，结束时间为空被视为结束于无限久远之后)<br>
     * 2.将边界相等视为重叠(两个时间段其中一个的开始时间与另一个的结束时间恰好相等时也视为两个时间段有重叠)
     *
     * @param start1 开始时间1
     * @param end1   结束时间1
     * @param start2 开始时间2
     * @param end2   结束时间2
     * @return 时间段有重叠则返回true，否则返回false
     */
    public static boolean periodDup(Date start1, Date end1, Date start2, Date end2) {
        return periodDup(start1, end1, start2, end2, true, true);
    }

    /**
     * 检查两个时间段是否有重叠<br>
     * 注意若时间段本身范围有误(开始时间大于结束时间)，则会交换开始时间与结束时间再做检查
     *
     * @param start1          开始时间1
     * @param end1            结束时间1
     * @param start2          开始时间2
     * @param end2            结束时间2
     * @param nullAsUnlimit   是否将空值视为无限制(为true表示：开始时间为空被视为开始于无限久远之前，结束时间为空被视为结束于无限久远之后)
     * @param boundEqualAsDup 是否将边界相等视为重叠(为true表示：两个时间段其中一个的开始时间与另一个的结束时间恰好相等时也视为两个时间段有重叠)
     * @return 时间段有重叠则返回true，否则返回false
     */
    public static boolean periodDup(Date start1, Date end1, Date start2, Date end2, boolean nullAsUnlimit, boolean boundEqualAsDup) {
        boolean dup = false;
        if (nullAsUnlimit || (start1 != null && end1 != null && start2 != null && end2 != null)) {
            Integer check1 = cmpDatetime(start1, end1);
            Integer check2 = cmpDatetime(start2, end2);
            boolean valid1 = check1 == null || check1.intValue() <= 0;
            boolean valid2 = check2 == null || check2.intValue() <= 0;
            if (!valid1) {
                Date tmp = start1;
                start1 = end1;
                end1 = tmp;
            }
            if (!valid2) {
                Date tmp = start2;
                start2 = end2;
                end2 = tmp;
            }
            Integer cmp1 = cmpDatetime(end1, start2);
            Integer cmp2 = cmpDatetime(end2, start1);
            boolean noDup1 = false;
            boolean noDup2 = false;
            if (cmp1 != null) {
                int i1 = cmp1.intValue();
                noDup1 = i1 < 0 || (i1 == 0 && !boundEqualAsDup);
            }
            if (cmp2 != null) {
                int i2 = cmp2.intValue();
                noDup2 = i2 < 0 || (i2 == 0 && !boundEqualAsDup);
            }
            dup = !(noDup1 || noDup2);
        }
        return dup;
    }
}
