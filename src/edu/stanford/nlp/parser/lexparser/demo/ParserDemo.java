package edu.stanford.nlp.parser.lexparser.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.io.StringReader;
import java.nio.file.DirectoryStream.Filter;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.common.ParserQuery;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonMatcher;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;
import edu.stanford.nlp.util.ScoredObject;
import edu.stanford.nlp.util.StringUtils;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.LexicalizedParserQuery;
import edu.stanford.nlp.pipeline.CoreNLPProtos.Sentence;



class ParserDemo {

  /**
   * The main method demonstrates the easiest way to load a parser.
   * Simply call loadModel and specify the path of a serialized grammar
   * model, which can be a file, a resource on the classpath, or even a URL.
   * For example, this demonstrates loading a grammar from the models jar
   * file, which you therefore need to include on the classpath for ParserDemo
   * to work.
   *
   * Usage: {@code java ParserDemo [[model] textFile]}
   * e.g.: java ParserDemo edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz data/chinese-onesent-utf8.txt
   *
   */
  public static void main(String[] args) {
    String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
    if (args.length > 0) {
      parserModel = args[0];
    }
    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);

    if (args.length == 0) {
      demoAPI(lp);
    } else {
      String textFile = (args.length > 1) ? args[1] : args[0];
      demoDP(lp, textFile);
    }
  }

  /**
   * demoDP demonstrates turning a file into tokens and then parse
   * trees.  Note that the trees are printed by calling pennPrint on
   * the Tree object.  It is also possible to pass a PrintWriter to
   * pennPrint if you want to capture the output.
   * This code will work with any supported language.
   */
  public static void demoDP(LexicalizedParser lp, String filename) {
    // This option shows loading, sentence-segmenting and tokenizing
    // a file using DocumentPreprocessor.
    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
    GrammaticalStructureFactory gsf = null;
    if (tlp.supportsGrammaticalStructures()) {
      gsf = tlp.grammaticalStructureFactory();
    }
    // You could also create a tokenizer here (as below) and pass it
    // to DocumentPreprocessor
    for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
      Tree parse = lp.apply(sentence);
      parse.pennPrint();
      System.out.println();

      if (gsf != null) {
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        Collection tdl = gs.typedDependenciesCCprocessed();
        System.out.println(tdl);
        System.out.println();
      }
    }
  }

  /**
   * demoAPI demonstrates other ways of calling the parser with
   * already tokenized text, or in some cases, raw text that needs to
   * be tokenized as a single sentence.  Output is handled with a
   * TreePrint object.  Note that the options used when creating the
   * TreePrint can determine what results to print out.  Once again,
   * one can capture the output by passing a PrintWriter to
   * TreePrint.printTree. This code is for English.
   */
  public static void demoAPI(LexicalizedParser lp) {
    // This option shows parsing a list of correctly tokenized words
   // String[] sent = { "The", "machine", "checks", "how", "much","money","has","been","deposited", "." };
   // List<CoreLabel> rawWords = SentenceUtils.toCoreLabelList(sent);
	List<ScoredObject<Tree>> parses;
	Tree final_tree=null;
	 List<TypedDependency> final_tdl = null ; 
    // This option shows loading and using an explicit tokenizer
   // String sent2 = "The system displays screen which indicates the list.";
	 String sent2 = "User watches screen which indicates previous work";
	 System.out.println(sent2);
    
    TokenizerFactory<CoreLabel> tokenizerFactory =
        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    Tokenizer<CoreLabel> tok =
        tokenizerFactory.getTokenizer(new StringReader(sent2));
    List<CoreLabel> rawWords2 = tok.tokenize();
   // System.out.println("rawWords:" + rawWords2);
    parses = lp.kparse(rawWords2);
    int ii = 0;
    
    // **This code is for finding the final parser tree that has dobj or nsubjpass among the 5 best parses 
    for (ScoredObject<Tree> parse : parses) {
    	ii++;
        Tree t = parse.object();
        TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
        GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
        GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
        List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
        
        
        for (int i = 0; i < tdl.size(); i++) {
            String extractElement = tdl.get(i).reln().toString();
            if (extractElement.equals("dobj") | extractElement.equals("nsubjpass")){
            	System.out.println(ii+ " parse has dobj or nsubjpass");
            	final_tree = t;
            	final_tdl=tdl;
            	break;
            }   
        }
        if(final_tree != null)
        	 break;
    }
    if(ii==5) {
    	System.out.println("There are no dobj in 5 parses.");
    	return;
    }
    TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree , typedDependencies -> Dependecy  in TreePrint function 
    System.out.println("printTree function \n");
    tp.printTree(final_tree);
    
   
    
    // This code is for extracting information from dependecies or parser tree
    
    String nsbj = "";
    String dobj = "";
    String nmod = "";
    String verb="";
    String rel_dobj="";
    String rel_nsubj="";
    String rel_verb="";
    
    int sub_flag = 0; // which clause flag 
    String rel_noun="";  //extract antecedent 
    Tree rel_tree;       // relative clause tree extraction
    ArrayList<Word> subc_tree;
    String rel_clause;
    String rel_gov = ""; //The reason why I use gov, dep is that there are two cases of which clause,NSUBJ,DOBJ
    String rel_dep="";

   
   
  
 // traversing the sentence's parse tree 
    for (Tree subTree : final_tree)
	{	
    	
	     if(subTree.label().value().equals("SBAR")) //If the word's label is SBAR
	      {     	 
	    	 if(subTree.firstChild().label().value().equals("WHNP")) { 
	    		 rel_tree = subTree;
	    		 ArrayList<Word> rel_list = subTree.yieldWords();
	    		 rel_clause = rel_list.stream().map(e->e.toString()).collect(Collectors.joining(" "));
	    		 System.out.println("rel_phrase "+rel_clause);
	    	  	 sub_flag=1; // flag check _ if we deal the file then we have to change it as iteral and itialize it.
	    	 }
	 
	      }
	}
    
 
    //check dependency parser    
    for (int i = 0; i < final_tdl.size(); i++) {
    	String extractElement = final_tdl.get(i).reln().toString();
    	 //need to make it optimal
    	//When sub_flag ==1 , when there is a which clause in sentence
        if(sub_flag==1) {
     	  /* if (extractElement.equals("ref")) { //antecedent extraction
     		   rel_noun = final_tdl.get(i).gov().originalText().toLowerCase();
     		   System.out.println("relation_noun : " + rel_noun + "\r\n");
            }*/
     	   
     	   if(extractElement.equals("dobj")){
     		  for (int j = 0; j < final_tdl.size(); j++) 
     		  {
     		    	String extractlabel = final_tdl.get(j).reln().toString();
     		    	if(extractlabel.equals("acl:relcl"))  //acl:relcl pair extraction for comparing
     		    	{
     		    		rel_gov= final_tdl.get(j).gov().originalText().toLowerCase();
     		    		rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
     		    		break;
     		    	}
     		  }  
     		 
     		 String current_verb= final_tdl.get(i).gov().originalText().toLowerCase();
     		 String current_dobj= final_tdl.get(i).dep().originalText().toLowerCase();
     		 
     		  if(current_verb.equals(rel_dep)) { //it means that this dobj is dobj of which clause
     			  if(current_dobj.equals(rel_gov)) // this dobj is antecedent 
     			  {
     				  rel_verb= current_verb; //only verb extraction
     				  System.out.println("Verb in relative clause "+rel_verb);
     			  }
     			  else //only verb is eql and dobj is different means that this dobj is dobj of which clause but not antecedent
     			  {
	     			  System.out.println("This is relative verb and dobj of relatvie clause");
	     			  rel_dobj = current_dobj;
	     			  rel_verb= current_verb;
	     			  System.out.println("Rel obj: " + rel_dobj+" rel verb: "+rel_verb);
     			  }
     			 continue;
     		  }	  
     	   }
     	   
     	   if(extractElement.equals("nsubj")){
     		  for (int j = 0; j < final_tdl.size(); j++) {
     		    	String extractlabel = final_tdl.get(j).reln().toString();
     		    	if(extractlabel.equals("acl:relcl")) {
     		    		rel_gov= final_tdl.get(j).gov().originalText().toLowerCase();
     		    		rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
     		    		break;
     		    	}
     		  }  
      		 String current_verb= final_tdl.get(i).gov().originalText().toLowerCase();
      		 String current_nsbj= final_tdl.get(i).dep().originalText().toLowerCase();
      		  if(current_verb.equals(rel_dep)) {
     			  if(current_nsbj.equals(rel_gov))
     			  {
     				  rel_verb= current_verb;
     				  System.out.println("Verb in relative clause "+rel_verb);
     			  }
     			  else {
	     			  System.out.println("This is relative verb and dobj in relative clause");
	     			  rel_nsubj = current_nsbj;
	     			  rel_verb= current_verb;
	     			  System.out.println("Rel nsbj: " + rel_nsubj+" rel verb: "+rel_verb);
     			  }
     			 continue;
     		  }	   
     	   }
     	//add rel sub + verb  and connect to dobj  after loop 
        }
        
        
        
        
        
        
        
        
       if (extractElement.equals("nsubj")) {
          nsbj =  final_tdl.get(i).dep().originalText().toLowerCase();
          System.out.println("nsbj: " +nsbj + "\r\n");
        }
       if (extractElement.equals("nsubjpass")) {
           nsbj = final_tdl.get(i).dep().originalText().toLowerCase();
           verb = final_tdl.get(i).gov().originalText().toLowerCase() ;
           System.out.println("nsubjpass_nsbj: " + nsbj + "\r\n");
           System.out.println("aux verb: " + verb + "\r\n");
       }
       if (extractElement.equals("dobj")) {
    	   verb = final_tdl.get(i).gov().originalText().toLowerCase() ;
           System.out.println("verb: " + verb + "\r\n");
           dobj = final_tdl.get(i).dep().originalText().toLowerCase();
           System.out.println("dobj: " + dobj + "\r\n");
       }

      /* if (extractElement.contains("nmod")) {
    	   	   String prep;
    	   	 //  prep = extractElement.substring(5);
    	   //	   System.out.println("prep: "+ " " + prep);
               nmod = final_tdl.get(i).gov().originalText().toLowerCase() + " " + final_tdl.get(i).dep().originalText().toLowerCase();
               System.out.println("nmod: " +nmod + "\r\n");
           
         }*/
  
    }
	   
     //  System.out.println("Type dependency list" +tdl);
  //  System.out.println("after dependency " + parse);
    
    // You can also use a TreePrint object to print trees and dependencies  tree and dependency both are printed 
   // TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree , typedDependencies -> Dependecy  in TreePrint function 
 //   System.out.println("printTree function \n");
  }

  private ParserDemo() {} // static methods only

}

