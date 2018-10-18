package edu.stanford.nlp.parser.lexparser.demo;

import java.util.ArrayList;

public class Modifier {
	String name;
	String relation;
	String govIdx;
	String depIdx;
	ArrayList<Modifier> modarr;

	public Modifier() {
		this.name = "";
		this.relation = "";
		this.govIdx = "";
		this.depIdx = "";
		this.modarr = new ArrayList<Modifier>();
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
	
	public void setDepIdx(String idx) {
		this.depIdx = idx;
	}
	
	public void setModifier (Modifier mod) {
		this.modarr.add(mod);
	}
}

