package edu.stanford.nlp.parser.lexparser.demo;

import java.util.Vector;

public class Action { // state class
	Noun subj;
	Verb pred;
	Vector<Noun> dobjarr;
	Vector<Modifier> modarr;

	public Action() {
		this.subj = new Noun();
		this.pred = new Verb();
		this.dobjarr = new Vector<Noun>();
		this.modarr = new Vector<Modifier>();
	}

	public void setSubj(Noun s) {
		this.subj = s;
	}

	public void setVerb(Verb v) {
		this.pred = v;
	}

	public void setDobj(Noun dobj) {
		this.dobjarr.add(dobj);

	}

	public void setModifier(Modifier mod) {
		this.modarr.add(mod);
	}

}
