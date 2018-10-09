package edu.stanford.nlp.parser.lexparser.demo;

import java.util.Vector;

public class Modifier {
	String name;
	String ante;
	String relation;
	String govIdx;
	String depIdx;
	Vector<Modifier> modarr;

	public Modifier() {
		this.name = "";
		this.relation = "";
		this.govIdx = "";
	}

	public void setName(String s) {
		this.name = s;
	}
	
	public void setante(String s) {
		this.ante = s;
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

