import java.util.Date;

/**
 * Represent a query (a record in the dataset)
 */
public class Query {
	public Date date;
	public String content;
	public String city;
	public Query(Date date, String content, String city){
		this.date = date;
		this.content = content;
		this.city = city;
	}
	@Override
	public String toString(){
		return Helper.getDayStringOfDate(this.date) + "," + 
				this.content + ","+this.city;
	}
}
