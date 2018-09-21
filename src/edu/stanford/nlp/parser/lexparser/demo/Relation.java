package edu.stanford.nlp.parser.lexparser.demo;


public class Relation {

	public String govIdx;
	public String depIdx;
	public int wordIdx;
	public String word = "";
	
	public Relation() {
		
	}
	
	public Relation(String govIdx, String depIdx, int wordIdx) {
		this.govIdx = govIdx;
		this.depIdx = depIdx;
		this.wordIdx = wordIdx;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public void addBeforeWord(String word){
		this.word = word + " " + this.word;
	}
	
	public void addAfterWord(String word) {
		this.word = this.word + " " + word;
	}
}
