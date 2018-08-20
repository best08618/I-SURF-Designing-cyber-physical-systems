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
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
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
    String sent2 = "The user indicates that she wants to purchase items which she has selected";
  
    
    TokenizerFactory<CoreLabel> tokenizerFactory =
        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    Tokenizer<CoreLabel> tok =
        tokenizerFactory.getTokenizer(new StringReader(sent2));
    List<CoreLabel> rawWords2 = tok.tokenize();
   // System.out.println("rawWords:" + rawWords2);
    parses = lp.kparse(rawWords2);
    int ii = 0;
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
    
   
    
    
    String nsbj = "";
    String dobj = "";
    String nmod = "";
    String verb="";
    int rel_flag = 0;
    String rel_noun="";
    ArrayList<Word> rel_tree;
    String rel_clause;
    int index;
    //System.out.println("Print extraction");
    Tree main;
   


    for (Tree subTree : final_tree) // traversing the sentence's parse tree 
	{	
    	    	//System.out.println(subTree.firstChild().label().value());
	     if(subTree.label().value().equals("SBAR")) //If the word's label is SBAR
	      { //Do what you want 
	    	 if(subTree.firstChild().label().value().equals("WHNP")) { 
	    		 System.out.println("Clause Tree: " +subTree);
	    		 rel_tree = subTree.yieldWords();
	    		 rel_clause = rel_tree.stream().map(e->e.toString()).collect(Collectors.joining(" "));
	    		 System.out.println(rel_clause);
	    	  	 rel_flag=1; // flag check _ if we deal the file then we have to change it as iteral and itialize it.
	    	  	 
	    	  	 
	    	 }
	      }
	}
    
    
    for (int i = 0; i < final_tdl.size(); i++) {
    	String extractElement = final_tdl.get(i).reln().toString();
    	
    	 
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
       if(rel_flag==1) {
    	   if (extractElement.equals("ref")) {
    		   rel_noun = final_tdl.get(i).gov().originalText().toLowerCase();
    		   System.out.println("relation_noun : " + rel_noun + "\r\n");
           }
       
	  }
       if (extractElement.contains("nmod")) {
    	   	   String prep;
    	   	 //  prep = extractElement.substring(5);
    	   //	   System.out.println("prep: "+ " " + prep);
               nmod = final_tdl.get(i).gov().originalText().toLowerCase() + " " + final_tdl.get(i).dep().originalText().toLowerCase();
               System.out.println("nmod: " +nmod + "\r\n");
           
         }
    }
  
	
	   
     //  System.out.println("Type dependency list" +tdl);
    System.out.println();
  //  System.out.println("after dependency " + parse);
    
    // You can also use a TreePrint object to print trees and dependencies  tree and dependency both are printed 
   // TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree , typedDependencies -> Dependecy  in TreePrint function 
 //   System.out.println("printTree function \n");
  }

  private ParserDemo() {} // static methods only

}

