package edu.stanford.nlp.parser.lexparser.demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.ScoredObject;

class ParserDemo {
	/**
	 * The main method demonstrates the easiest way to load a parser. Simply call
	 * loadModel and specify the path of a serialized grammar model, which can be a
	 * file, a resource on the classpath, or even a URL. For example, this
	 * demonstrates loading a grammar from the models jar file, which you therefore
	 * need to include on the classpath for ParserDemo to work.
	 *
	 * Usage: {@code java ParserDemo [[model] textFile]} e.g.: java ParserDemo
	 * edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz
	 * data/chinese-onesent-utf8.txt
	 * 
	 * @throws IOException
	 *
	 */
	public static void main(String[] args) throws IOException {
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
		if (args.length > 0) {
			parserModel = args[0];
		}
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
		if (args.length == 1) {
			demoAPI(lp);
		} else {
			String textFile = "/Users/zoey/git/I-SURF-Designing-cyber-physical-systems/src/edu/stanford/nlp/parser/lexparser/demo/input.txt";
			demoDP(lp, textFile);
		}
	}

	/**
	 * demoDP demonstrates turning a file into tokens and then parse trees. Note
	 * that the trees are printed by calling pennPrint on the Tree object. It is
	 * also possible to pass a PrintWriter to pennPrint if you want to capture the
	 * output. This code will work with any supported language.
	 * 
	 * @throws IOException
	 */
	public static void demoDP(LexicalizedParser lp, String filename) throws IOException {
		/*
		 * original demoDP code Tree parse = lp.apply(sentence); parse.pennPrint();
		 * System.out.println();
		 * 
		 * if (gsf != null) { GrammaticalStructure gs =
		 * gsf.newGrammaticalStructure(parse); Collection tdl =
		 * gs.typedDependenciesCCprocessed(); System.out.println(tdl);
		 * System.out.println(); }
		 */

		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

		String line = "";
		while ((line = bufReader.readLine()) != null) {
			System.out.println("----------------------------------------");
			List<ScoredObject<Tree>> parses;
			Tree final_tree = null;
			List<TypedDependency> final_tdl = null;

			// This option shows loading and using an explicit tokenizer

			String sent2 = line;

			TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
			Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sent2));
			List<CoreLabel> rawWords2 = tok.tokenize();
			parses = lp.kparse(rawWords2);
			int ii = 0;
			for (ScoredObject<Tree> parse : parses) {
				ii++;
				Tree t = parse.object();
				TreebankLanguagePack tlp1 = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
				GrammaticalStructureFactory gsf1 = tlp1.grammaticalStructureFactory();
				GrammaticalStructure gs = gsf1.newGrammaticalStructure(t);
				List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
				for (int i = 0; i < tdl.size(); i++) {
					String extractElement = tdl.get(i).reln().toString();
					if (extractElement.equals("dobj")) {
						writer.write("Number " + ii + " parse has dobj.");
						writer.newLine();
						writer.write("Sentence : " + line);
						writer.newLine();

						System.out.println("Number " + ii + " parse has dobj.");
						System.out.println("Sentence : " + line);

						final_tree = t;
						final_tdl = tdl;
						break;
					}
				}
				if (final_tree != null)
					break;
			}
			if (ii == 5) {
				writer.append("Sentence : " + line);
				writer.newLine();
				writer.append("There are no dobj in 5 parses");
				writer.newLine();
				writer.append("===========================================================================");
				writer.newLine();
				continue;
			}

			TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree ,
			// typedDependencies -> Dependecy in TreePrint function
			// System.out.println("printTree function \n");
			tp.printTree(final_tree);

			Action act = new Action();
			int sub_flag = 0;
			String rel_clause = "";

			for (Tree subTree : final_tree) {
				if (subTree.label().value().equals("SBAR")) // If the word's label is SBAR
				{
					if (subTree.firstChild().label().value().equals("WHNP")) {
						ArrayList<Word> rel_list = subTree.yieldWords();
						rel_clause = rel_list.stream().map(e -> e.toString()).collect(Collectors.joining(" "));
						sub_flag = 1; // flag check _ if we deal the file then we have to change it as iteral and
										// itialize it.
					}
				}
			}

			for (int i = 0; i < final_tdl.size(); i++) {
				String extractElement = final_tdl.get(i).reln().toString();

				if (extractElement.contains("nsubj")) {

					if (sub_flag == 1) {
						String rel_gov = "";
						String rel_dep = "";
						String rel_verb = "";
						String rel_nsubj = "";
						for (int j = 0; j < final_tdl.size(); j++) {
							String extractlabel = final_tdl.get(j).reln().toString();
							if (extractlabel.equals("acl:relcl")) {
								rel_gov = final_tdl.get(j).gov().originalText().toLowerCase();
								rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
								break;
							}
						}
						String current_verb = final_tdl.get(i).gov().originalText().toLowerCase();
						String current_nsbj = final_tdl.get(i).dep().originalText().toLowerCase();

						if (current_verb.equals(rel_dep)) {
							if (current_nsbj.equals(rel_gov)) { // current subject is antecedent
								rel_verb = current_verb;
								System.out.println("Verb in relative clause " + rel_verb);

							} else { // current nsubj is not antecedent
								// System.out.println("This is relative nsbj and verb in relative clause");
								rel_nsubj = current_nsbj;
								rel_verb = current_verb;
								// System.out.println("Rel nsbj: " + rel_nsubj + " rel verb: " + rel_verb);
							}
							continue;
						}
					}
					Noun subj = new Noun();
					subj.setName(final_tdl.get(i).dep().originalText().toLowerCase());
					subj.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					act.setSubj(subj);
				} else if (extractElement.equals("dobj")) {
					if (sub_flag == 1) {
						String rel_gov = "";
						String rel_dep = "";
						String rel_verb = "";
						String rel_dobj = "";
						for (int j = 0; j < final_tdl.size(); j++) {
							String extractlabel = final_tdl.get(j).reln().toString();
							if (extractlabel.equals("acl:relcl")) // acl:relcl pair extraction for comparing
							{
								rel_gov = final_tdl.get(j).gov().originalText().toLowerCase();
								rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
								break;
							}
						}
						String current_verb = final_tdl.get(i).gov().originalText().toLowerCase();
						String current_dobj = final_tdl.get(i).dep().originalText().toLowerCase();
						if (current_verb.equals(rel_dep)) { // it means that this dobj is dobj of which clause
							if (current_dobj.equals(rel_gov)) // this dobj is antecedent
							{
								rel_verb = current_verb; // only verb extraction
								// System.out.println("Verb in relative clause: " + rel_verb);
								// ���߿� relative verb ó�� �� �ڵ� �ֱ�
							} else {
								rel_dobj = current_dobj;
								rel_verb = current_verb;
								System.out.println("Rel obj: " + rel_dobj + " rel verb: " + rel_verb);
							}
							continue;
						}
					}
					Noun dobj = new Noun();
					dobj.setName(final_tdl.get(i).dep().originalText().toLowerCase());
					dobj.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					dobj.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
					act.setDobj(dobj);
				} else if (extractElement.equals("root")) {
					Sentence verb = new Sentence(final_tdl.get(i).dep().originalText().toLowerCase());
					String name = verb.lemma(0);
					Verb pred = new Verb();
					pred.setName(name);
					pred.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					act.setVerb(pred);
				}
			}

			Vector<Modifier> mods = new Vector<Modifier>();
			String beforeGovIdx = "";

			for (int i = 0; i < final_tdl.size(); i++) {
				String extractElement = final_tdl.get(i).reln().toString();
				if (extractElement.equals("compound:prt")) {
					String name = "";
					Modifier mod = new Modifier();
					name = final_tdl.get(i).gov().originalText().toLowerCase() + "-"
							+ final_tdl.get(i).dep().originalText().toLowerCase();
					if (act.pred.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
						act.pred.setName(name);
					} else {
						mod.setName(name);
						mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
						mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
						mod.setRelation(extractElement);
						act.setModifier(mod);
					}
				}

				else if (extractElement.equals("advmod") || extractElement.contains("nmod")
						|| extractElement.equals("amod") || extractElement.equals("compound")) {
					Modifier mod = new Modifier();
					mod.setName(final_tdl.get(i).dep().originalText().toLowerCase());
					mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
					mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					mod.setRelation(extractElement);
					boolean hasChain = true;
					if (act.pred.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
						act.setModifier(mod);
					} else if (act.subj.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
						act.subj.setModifier(mod);
						if (final_tdl.get(i).dep().toCopyIndex().equals(beforeGovIdx)) {
							act.subj.modarr.addAll(mods);
							mods.clear();
						}
						hasChain = false;
					} else {
						for (int dIdx = 0; dIdx < act.dobjarr.size(); dIdx++) {
							if (act.dobjarr.get(dIdx).depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
								act.dobjarr.get(dIdx).setModifier(mod);
								if (final_tdl.get(i).dep().toCopyIndex().equals(beforeGovIdx)) {
									act.dobjarr.get(dIdx).modarr.addAll(mods);
									mods.clear();
								}
								hasChain = false;
								break;
							}
						}
					}

					if (hasChain) {
						mods.add(mod);
						beforeGovIdx = final_tdl.get(i).gov().toCopyIndex();
					}
				}

				if (sub_flag == 1) {
					boolean isModifierOfDobj = false;
					if (extractElement.equals("ref")) {
						Modifier mod = new Modifier();
						mod.setName(rel_clause);
						mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
						mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
						mod.setRelation(extractElement);
						for (int idx = 0; idx < act.dobjarr.size(); idx++) {
							if (act.dobjarr.get(idx).depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
								act.dobjarr.get(idx).setModifier(mod);
								isModifierOfDobj = true;
								break;
							}
						}
						if (!isModifierOfDobj) {
							act.setModifier(mod);
						}
					}
				}
			}

			System.out.println(" Subject : " + act.subj.name);
			for (int i = 0; i < act.subj.modarr.size(); i++) {
				System.out.println(" Modifier of Nsubj : " + act.subj.modarr.get(i).name);
				System.out.println(" Relation name : " + act.subj.modarr.get(i).relation);
			}
			System.out.println(" Verb : " + act.pred.name);

			for (int i = 0; i < act.modarr.size(); i++) {
				System.out.println(" Modifier of Verb : " + act.modarr.get(i).name);
				System.out.println(" Relation name : " + act.modarr.get(i).relation);
			}

			for (int i = 0; i < act.dobjarr.size(); i++) {
				System.out.println(" Object[" + i + "] : " + act.dobjarr.get(i).name);
				for (int j = 0; j < act.dobjarr.get(i).modarr.size(); j++) {
					System.out.println(" Modifier of Dobj[" + i + "] : " + act.dobjarr.get(i).modarr.get(j).name);
					System.out.println(" Relation name : " + act.dobjarr.get(i).modarr.get(j).relation);
				}
			}

			System.out.println();

			writer.append("----------------------------------------");
			writer.newLine();

			// TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree ,
			// typedDependencies -> Dependecy in TreePrint function
			// System.out.println("printTree function \n");
			// tp.printTree(final_tree);
		}
		writer.close();
	}

	/**
	 * demoAPI demonstrates other ways of calling the parser with already tokenized
	 * text, or in some cases, raw text that needs to be tokenized as a single
	 * sentence. Output is handled with a TreePrint object. Note that the options
	 * used when creating the TreePrint can determine what results to print out.
	 * Once again, one can capture the output by passing a PrintWriter to
	 * TreePrint.printTree. This code is for English.
	 */

	public static void demoAPI(LexicalizedParser lp) {
		// This option shows parsing a list of correctly tokenized words
		// String[] sent = { "The", "machine", "checks", "how",
		// "much","money","has","been","deposited", "." };
		// List<CoreLabel> rawWords = SentenceUtils.toCoreLabelList(sent);

		List<ScoredObject<Tree>> parses;
		Tree final_tree = null;
		List<TypedDependency> final_tdl = null;

		// String sent2 = "The user will present the amount that the order will cost,
		// including applicable taxes and shipping charges.";
		// String = "The system will provide the user with a tracking ID for the
		// order.";
		String sent2 = "The system prints user account and the number which user entered.";
		// String sent2 = "The system repeatedly checks passwords of documents";

		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sent2));
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
				if (extractElement.equals("dobj")) {
					System.out.println("Number " + ii + " parse has dobj.");
					System.out.println("");
					final_tree = t;
					final_tdl = tdl;
					break;
				}
			}
			if (final_tree != null)
				break;
		}
		if (ii == 5) {
			System.out.println("There are no dobj in 5 parses");
			return;
		}

		System.out.println("Sentence : " + sent2 + "\n");

		TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree ,
		// typedDependencies -> Dependecy in TreePrint function
		// System.out.println("printTree function \n");
		tp.printTree(final_tree);

		Action act = new Action();
		int sub_flag = 0;
		String rel_clause = "";

		for (Tree subTree : final_tree) {
			if (subTree.label().value().equals("SBAR")) // If the word's label is SBAR
			{
				if (subTree.firstChild().label().value().equals("WHNP")) {
					ArrayList<Word> rel_list = subTree.yieldWords();
					rel_clause = rel_list.stream().map(e -> e.toString()).collect(Collectors.joining(" "));
					sub_flag = 1; // flag check _ if we deal the file then we have to change it as iteral and
									// itialize it.
				}
			}
		}

		for (int i = 0; i < final_tdl.size(); i++) {
			String extractElement = final_tdl.get(i).reln().toString();

			if (extractElement.equals("nsubj")) {

				if (sub_flag == 1) {
					String rel_gov = "";
					String rel_dep = "";
					String rel_verb = "";
					String rel_nsubj = "";
					for (int j = 0; j < final_tdl.size(); j++) {
						String extractlabel = final_tdl.get(j).reln().toString();
						if (extractlabel.equals("acl:relcl")) {
							rel_gov = final_tdl.get(j).gov().originalText().toLowerCase();
							rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
							break;
						}
					}
					String current_verb = final_tdl.get(i).gov().originalText().toLowerCase();
					String current_nsbj = final_tdl.get(i).dep().originalText().toLowerCase();

					if (current_verb.equals(rel_dep)) {
						if (current_nsbj.equals(rel_gov)) { // current subject is antecedent
							rel_verb = current_verb;
							System.out.println("Verb in relative clause " + rel_verb);

						} else { // current nsubj is not antecedent
							// System.out.println("This is relative nsbj and verb in relative clause");
							rel_nsubj = current_nsbj;
							rel_verb = current_verb;
							// System.out.println("Rel nsbj: " + rel_nsubj + " rel verb: " + rel_verb);
						}
						continue;
					}
				}
				Noun subj = new Noun();
				subj.setName(final_tdl.get(i).dep().originalText().toLowerCase());
				subj.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
				act.setSubj(subj);
			} else if (extractElement.equals("dobj")) {
				if (sub_flag == 1) {
					String rel_gov = "";
					String rel_dep = "";
					String rel_verb = "";
					String rel_dobj = "";
					for (int j = 0; j < final_tdl.size(); j++) {
						String extractlabel = final_tdl.get(j).reln().toString();
						if (extractlabel.equals("acl:relcl")) // acl:relcl pair extraction for comparing
						{
							rel_gov = final_tdl.get(j).gov().originalText().toLowerCase();
							rel_dep = final_tdl.get(j).dep().originalText().toLowerCase();
							break;
						}
					}
					String current_verb = final_tdl.get(i).gov().originalText().toLowerCase();
					String current_dobj = final_tdl.get(i).dep().originalText().toLowerCase();
					if (current_verb.equals(rel_dep)) { // it means that this dobj is dobj of which clause
						if (current_dobj.equals(rel_gov)) // this dobj is antecedent
						{
							rel_verb = current_verb; // only verb extraction
							// System.out.println("Verb in relative clause: " + rel_verb);
							// ���߿� relative verb ó�� �� �ڵ� �ֱ�
						} else {
							rel_dobj = current_dobj;
							rel_verb = current_verb;
							System.out.println("Rel obj: " + rel_dobj + " rel verb: " + rel_verb);
						}
						continue;
					}
				}
				Noun dobj = new Noun();
				dobj.setName(final_tdl.get(i).dep().originalText().toLowerCase());
				dobj.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
				dobj.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
				act.setDobj(dobj);
			} else if (extractElement.equals("root")) {
				Sentence verb = new Sentence(final_tdl.get(i).dep().originalText().toLowerCase());
				String name = verb.lemma(0);
				Verb pred = new Verb();
				pred.setName(name);
				pred.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
				act.setVerb(pred);
			}
		}

		Vector<Modifier> mods = new Vector<Modifier>();
		String beforeGovIdx = "";

		for (int i = 0; i < final_tdl.size(); i++) {
			String extractElement = final_tdl.get(i).reln().toString();
			if (extractElement.equals("compound:prt")) {
				String name = "";
				Modifier mod = new Modifier();
				name = final_tdl.get(i).gov().originalText().toLowerCase() + "-"
						+ final_tdl.get(i).dep().originalText().toLowerCase();
				if (act.pred.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
					act.pred.setName(name);
				} else {
					mod.setName(name);
					mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
					mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					mod.setRelation(extractElement);
					act.setModifier(mod);
				}
			}

			else if (extractElement.equals("advmod") || extractElement.contains("nmod") || extractElement.equals("amod")
					|| extractElement.equals("compound")) {
				Modifier mod = new Modifier();
				mod.setName(final_tdl.get(i).dep().originalText().toLowerCase());
				mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
				mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
				mod.setRelation(extractElement);
				boolean hasChain = true;
				if (act.pred.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
					act.setModifier(mod);
				} else if (act.subj.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
					act.subj.setModifier(mod);
					if (final_tdl.get(i).dep().toCopyIndex().equals(beforeGovIdx)) {
						act.subj.modarr.addAll(mods);
						mods.clear();
					}
					hasChain = false;
				} else {
					for (int dIdx = 0; dIdx < act.dobjarr.size(); dIdx++) {
						if (act.dobjarr.get(dIdx).depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
							act.dobjarr.get(dIdx).setModifier(mod);
							if (final_tdl.get(i).dep().toCopyIndex().equals(beforeGovIdx)) {
								act.dobjarr.get(dIdx).modarr.addAll(mods);
								mods.clear();
							}
							hasChain = false;
							break;
						}
					}
				}
				if (hasChain) {
					mods.add(mod);
					beforeGovIdx = final_tdl.get(i).gov().toCopyIndex();
				}
			}

			if (sub_flag == 1) {
				if (extractElement.equals("ref")) {
					Modifier mod = new Modifier();
					mod.setName(rel_clause);
					mod.setGovIdx(final_tdl.get(i).gov().toCopyIndex());
					mod.setDepIdx(final_tdl.get(i).dep().toCopyIndex());
					mod.setRelation(extractElement);
					if (act.subj.depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
						act.subj.setModifier(mod);
					} else {
						for (int idx = 0; idx < act.dobjarr.size(); idx++) {
							if (act.dobjarr.get(idx).depIdx.equals(final_tdl.get(i).gov().toCopyIndex())) {
								act.dobjarr.get(idx).setModifier(mod);
								break;
							}
						}
					}
				}
			}
		}
		System.out.println(" Subject : " + act.subj.name);
		for (int i = 0; i < act.subj.modarr.size(); i++) {
			System.out.println(" Modifier of Nsubj : " + act.subj.modarr.get(i).name);
			System.out.println(" Relation name : " + act.subj.modarr.get(i).relation);
		}
		System.out.println(" Verb : " + act.pred.name);

		for (int i = 0; i < act.modarr.size(); i++) {
			System.out.println(" Modifier of Verb : " + act.modarr.get(i).name);
			System.out.println(" Relation name : " + act.modarr.get(i).relation);
		}

		for (int i = 0; i < act.dobjarr.size(); i++) {
			System.out.println(" Object[" + i + "] : " + act.dobjarr.get(i).name);
			for (int j = 0; j < act.dobjarr.get(i).modarr.size(); j++) {
				System.out.println(" Modifier of Dobj[" + i + "] : " + act.dobjarr.get(i).modarr.get(j).name);
				System.out.println(" Relation name : " + act.dobjarr.get(i).modarr.get(j).relation);
			}
		}

		System.out.println();
	}

	private ParserDemo() {
	} // static methods only
}