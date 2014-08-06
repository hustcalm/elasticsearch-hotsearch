public class QueryGenerator {
	RandomEntry random_word;
	RandomTime random_time;
	RandomEntry randome_city;
	public QueryGenerator(RandomEntry random_word, RandomTime random_time, RandomEntry random_city){
		this.random_word = random_word;
		this.random_time = random_time;
		this.randome_city = random_city;
	}
	/**
	 * @return A random Query
	 */
	public Query getNextQuery(){
		return new Query(this.random_time.getNextDate(), 
				this.random_word.getNextWord(), 
				this.randome_city.getNextWord());
	}
}
