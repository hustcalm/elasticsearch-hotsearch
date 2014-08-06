package com.cgroups.gendata;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateData {
	public static void main(String args[]){
		RandomTime t = new RandomTime();
		RandomEntry re = new RandomEntry("data/words", "data/weights");
		RandomEntry random_city = new RandomEntry();
		random_city.addWordsWithEqualWeight("data/cities");
		QueryGenerator qg = new QueryGenerator(re, t, random_city);
		BufferedWriter bw = null;
		BufferedWriter data_bw = null;
		try{ 
			bw = new BufferedWriter(new FileWriter("data/queries.csv"));
			data_bw = new BufferedWriter(new FileWriter("data/data.json"));
			for (int i=1; i<=10000; i++){
				Query query = qg.getNextQuery();
				bw.write(query.toString());
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("{\"index\":{\"_index\":\"query_data_10k\",\"_type\":\"query\",\"_id\":\"%d\"}}\n",i));
				sb.append(String.format("{\"date\":\"%s\",\"city\":\"%s\",\"word\":\"%s\",\"status\":0}\n", 
						Helper.getDayStringOfDate(query.date), query.city, query.content));
				data_bw.write(sb.toString());
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{bw.close(); data_bw.close();}catch(Exception e){}
		}
	}
}
