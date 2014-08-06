import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {
	public static String getDayStringOfDate(Date date){
		DateFormat date_format = new SimpleDateFormat("yyyyMMdd");
		return date_format.format(date);
	}
}
