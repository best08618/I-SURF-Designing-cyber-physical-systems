package edu.stanford.nlp.parser.lexparser.demo;

public class Verb {
	String name;
	String depIdx;

	public Verb() {
		this.name = "";
		this.depIdx = "";
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setDepIdx(String idx) {
		this.depIdx = idx;
	}
}
