package edu.stanford.nlp.parser.lexparser.demo;

public class Modifier {
	String name;
	String relation;
	String govIdx;

	public Modifier() {
		this.name = "";
		this.relation = "";
		this.govIdx = "";
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setRelation(String s) {
		this.relation = s;
	}

	public void setGovIdx(String idx) {
		this.govIdx = idx;
	}
}
