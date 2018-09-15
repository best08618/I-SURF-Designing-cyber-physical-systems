package edu.stanford.nlp.parser.lexparser.demo;

public class Action {
	Noun subj;
	Verb prod;
	Noun[] dobjarr;
	String[] modarr;
	int dobCount;
	public Action() {
		this.subj=new Noun();
		this.prod = new Verb();
		this.dobjarr = new Noun[10];
		this.dobCount=0;
	}
	public void setSubj(Noun subj)
	{
		this.subj = subj;
	}
	public void setDobj(Noun dobj)
	{
	
		this.dobjarr[dobCount]= dobj;
		this.dobCount ++;
	}
	
}
