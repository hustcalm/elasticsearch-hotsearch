package com.cgroups.gendata;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class RandomEntry {
	private ArrayList<String> words;
	private ArrayList<Double> weights;
	Random random = null;
	private double total_weight;
	
	public RandomEntry(){
		this.init_menbers();
	}
	
	/** 
	 * @param word_path Path of the file listing the words
	 * @param weights_path Path of the file listing weights of each word
	 */
	public RandomEntry(String word_path, String weights_path){
		this.init_menbers();
		BufferedReader br1 = null;
		BufferedReader br2 = null;
		try{
			br1 = new BufferedReader(new FileReader(word_path));
			br2 = new BufferedReader(new FileReader(weights_path));
			while (true){
				String line1=null, line2=null;
				line1 = br1.readLine();
				line2 = br2.readLine();
				if (line1==null || line2==null)
					break;
				double weight;
				try{
					weight = Double.parseDouble(line2);
				}catch (NumberFormatException e){
					continue;
				}
				words.add(line1);
				weights.add(weight);
				total_weight += weight;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{br1.close(); br2.close();} catch (Exception e){}
		}
	}
		
	/**
	 * @return A random word
	 */
	public String getNextWord(){
		double rnumber = random.nextDouble();
		double offset  = rnumber*total_weight;
		//get the index
		int i;
		for (i=0; i<weights.size(); i++){
			double weight = weights.get(i);
			if (weight + 0.000001>=offset)
				break;
			offset = offset - weight;
		}
		if (i>=0 && i < weights.size())
			return words.get(i);
		return null;
	}
	private void init_menbers(){
		random = new Random();
		words = new ArrayList<String>();
		weights = new ArrayList<Double>();
		total_weight = 0.0;
	}
	
	public void addWordsWithEqualWeight(String path){
		this.addWordsWithEqualWeight(path, 1.0);
	}
	public void addWordsWithEqualWeight(String path, double weight){
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(path));
			while (true){
				String line = br.readLine();
				if (line == null)
					break;
				if (line.equals(""))
					continue;
				this.words.add(line);
				this.weights.add(weight);
				this.total_weight += weight;
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{br.close();} catch(Exception e){}
		}
	}
	public ArrayList<String> getAllWords(){
		return this.words;
	}
}
