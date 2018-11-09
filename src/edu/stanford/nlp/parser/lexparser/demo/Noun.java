package edu.stanford.nlp.parser.lexparser.demo;

import java.util.ArrayList;

public class Noun {
	String name;
	ArrayList<Modifier> modarr; //hash 형태로 변경 
	String depIdx;
	String govIdx;

	public Noun() {
		this.name = "";
		this.modarr = new ArrayList<Modifier>();
		this.depIdx = "";
	}

	public void setName(String s) {
		this.name = s;
	}

	public void setModifier(Modifier mod) {
		this.modarr.add(mod);
	}

	public void setDepIdx(String idx) {
		this.depIdx = idx;
	}
	
	public void setGovIdx(String idx) {
		this.govIdx = idx;
	}
}

