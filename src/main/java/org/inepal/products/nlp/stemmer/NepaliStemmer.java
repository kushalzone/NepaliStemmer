package org.inepal.products.nlp.stemmer;


import org.inepal.products.nlp.compounds.CompoundWordEnding;
import org.inepal.products.nlp.compounds.CompoundWordEndingPeopleName;
import org.inepal.products.nlp.compounds.CompoundWordEndingPlaces;
import org.inepal.products.nlp.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Kushal Paudyal
 * www.icodejava.com | www.inepal.org
 * 
 *         Stemmer Class for Nepali Words
 * 
 *         Stemming is a process of finding the root word from a compound word.
 *         e.g. "स्थानलगायत" is stemmed to "स्थान"
 *
 */
public class NepaliStemmer {

	public static final String SET_OF_MATRAS = "ा ि ी ु ू ृ े ै ो ौ ं : ँ ॅ्" ;
	
	public static void main(String [] args) {
		getNepaliRootWord("समयभन्दा");
	}

	/**
	 * This method finds a root word for a given compound Nepali Word. 
	 * For example: "स्थानलगायत" is stemmed to "स्थान"
	 * 
	 * @param compoundWord
	 * @return root word
	 */
	public static String getNepaliRootWord(String compoundWord) {

		if(compoundWord == null) {
			return null;
		}

		if(isPotentialName(compoundWord)) {
			return findRootName(compoundWord);
		} else if(isPotentailPlaceName(compoundWord)) {

			return findRootPlace(compoundWord);
		}
			else {
			return findRootWord(compoundWord);
		}

	}

	private static String findRootName(String compoundWord) {

		for (CompoundWordEndingPeopleName dir : CompoundWordEndingPeopleName.values()) {
			String cwe = dir.getNameEnding();

			compoundWord = replaceSuffixes(compoundWord, cwe);

		}

		compoundWord = compoundWord.trim();

		return compoundWord;

	}

	private static String findRootPlace(String compoundWord) {

		for (CompoundWordEndingPlaces dir : CompoundWordEndingPlaces.values()) {
			String cwe = dir.getPlaceEnding();

			compoundWord = replaceSuffixes(compoundWord, cwe);

		}

		compoundWord = compoundWord.trim();

		return compoundWord;

	}

	private static String replaceSuffixes(String compoundWord, String cwe) {
		if (compoundWord.endsWith(cwe)
                && isNotTheSameWord(compoundWord, cwe)
                && isAllowedLength(compoundWord, cwe)) {

            compoundWord = TextUtils.replaceLast(compoundWord, cwe, "");
        }
		return compoundWord;
	}

	private static boolean isPotentialName(String compoundWord) {
		boolean found = false;
		for (CompoundWordEndingPeopleName dir : CompoundWordEndingPeopleName.values()) {
			if (compoundWord.endsWith(dir.getNameEnding())) {
				found = true;
				break;
			}

		}

		return found;
	}

	private static boolean isPotentailPlaceName(String compoundWord) {
		boolean found = false;
		for (CompoundWordEndingPlaces dir : CompoundWordEndingPlaces.values()) {
			if (compoundWord.endsWith(dir.getPlaceEnding())) {
				found = true;
				break;
			}

		}

		return found;
	}

	private static String findRootWord(String compoundWord) {

		for (CompoundWordEnding dir : CompoundWordEnding.values()) {
			String cwe = dir.getNepaliWordEnding();

			compoundWord = replaceSuffixes(compoundWord, cwe);

		}

		compoundWord = compoundWord.trim();

		return compoundWord;
	}


	/**
     * Checks if two words are same. For example, we want to no more apply the
     * stemming rule if the word is लगायत although लगायत is a compound word
     * ending and would have been removed from other compound words
     * 
     * @param compoundWord
     * @param cwe
     * @return
     */
    private static boolean isNotTheSameWord(String compoundWord, String cwe) {
        return compoundWord.length() > cwe.length();
    }

	private static boolean isAllowedLength(String compoundWord, String cwe) {
		
		boolean allowed = true;
		
		String cweMatraReplaced = replaceMatras(cwe);
		
		if(cweMatraReplaced.length() == 1) {
			String matraReplacedWord = replaceMatras(TextUtils.replaceLast(compoundWord, cwe, "")); //TODO: Document this
			allowed = matraReplacedWord.length() > 2; //to prvent words like थुम्का being split as थुम् + का
			
		}
		
		return allowed;
	}

	/**
	 * This method takes a Nepali Word, repalces all the matras and returns the
	 * transformed word. e.g. ट्रान्सफर --> टरनसफर
	 * 
	 * @param word
	 * @return
	 */
	public static String replaceMatras(String word) {

		for (Character c : SET_OF_MATRAS.toCharArray()) {
			word = word.replace(c + "", "");
		}

		return word;
	}
	
	/**
	 * Given a word that ends in NU (नु), this method returns affirmative (positive) verb variations
	 * @param wordEndingInNU
	 * @return
	 */
	public static List<String> getAffirmativeVerbVariations(String wordEndingInNU) {
	    List<String> verbVariations = new ArrayList<String>();
	    
	    if(wordEndingInNU == null || !wordEndingInNU.endsWith("नु")) {
	        System.out.println("Invalid Input. The Parameter should be non empty and end in नु");
	        return verbVariations;
	    }
	    
	    String root = getVerbRoot(wordEndingInNU); //NU 
	    
	    boolean rootEndsInHalant = root.endsWith('\u094D'+"");
	    //चल्ईन, चल्ईस, चल्ई, चल्ऊ, चल्इछे, चल्एछ, 

	    
	    verbVariations.add(removeHalant(root)); //खा, रमा
	    verbVariations.add(wordEndingInNU); //खानु,रमाउनु
	    verbVariations.add(root + "यो"); //खायो, रमायो, चल्यो
	    verbVariations.add(root + "यौ"); //खायौ, रमायौ
	    
	    if(rootEndsInHalant) {
	        
	        verbVariations.add(removeHalant(root) + '\u0947'+'\u0901'); //चलेँ
	        verbVariations.add(removeHalant(root) +'\u0947'+"को");  //चलेको
	        verbVariations.add(removeHalant(root) +'\u0947'+"छ"); //चलेछ
	        verbVariations.add(removeHalant(root) +'\u0947'); //चले
	        verbVariations.add(removeHalant(root) + "\u093F" + "न");//चलिन
	        verbVariations.add(removeHalant(root) + "\u093F" + "स"); //चलिस
	        verbVariations.add(removeHalant(root) + "\u0940"); // चली
	        verbVariations.add(removeHalant(root) + "\u093F"); // चलि Wrong Hraswa/Dirgha but possible
	        verbVariations.add(removeHalant(root) + "\u093F" + "छे"); //चलिछे
	    } else {
	    
	        verbVariations.add(root + "एँ"); //खाएँ, रमाएँ
	        verbVariations.add(root + "ए" +"\u0902"); //खाएं, रमाएं

	        verbVariations.add(root + "ये" +"\u0902"); // खायें, रमायें
	        
	        verbVariations.add(root + "एको");  //खाएको, रमाएको
	        verbVariations.add(root + "येको");  //खायेको, रमायेको

			verbVariations.add(root + "एकोले");  //खाएकोले, रमाएकोले
			verbVariations.add(root + "येकोले");  //खायेकोले, रमायेकोले


			verbVariations.add(root + "एकाले"); //खाएकाले, रमाएकाले
			verbVariations.add(root + "येकाले"); //खायेकाले, रमायेकाले

			verbVariations.add(root + "इनु"); //खाइनु, रमाइनु
			verbVariations.add(root + "इनु"); //खाईनु, रमाईनु

			verbVariations.add(root + "उन्जेल"); //खाउन्जेल, रमाउन्जेल
			verbVariations.add(root + "उञ्जेल"); //खाउञ्जेल, रमाउञ्जेल


			/**
			 *

			 khaayekile, ramaayekile
			 khayeki, ramaayeki
			 khayekaa, ramaayekaa
			 */

			verbVariations.add(root + "एछ"); //खाएछ, रमाएछ
	        verbVariations.add(root + "येछ"); //खायेछ, रमायेछ

	        verbVariations.add(root + "ए");//खाए, रमाये
	        verbVariations.add(root + "ये");//खाये, रमाये
	        
	        verbVariations.add(root + "ईन");//खाईन, रमाईन //affirmative
	        verbVariations.add(root + "इन");//खाइन, रमाइन //affirmative

			verbVariations.add(root + "ईस"); //खाईस, रमाईस
            verbVariations.add(root + "इस"); //खाइस, रमाइस

			verbVariations.add(root + "ई"); // खाई, रमाई
            verbVariations.add(root + "इ"); // खाइ, रमाइ
            
	        
	        verbVariations.add(root + "इछे"); //खाइछे, रमाइछे

			verbVariations.add(root + "ऊ");//खाऊ, रमाऊ

			/**
			 * खाईछे, रमाईछे
			 */


		}

	    
	    if(wordEndingInNU.endsWith("उनु")) {
	    	System.out.println("Here");
	    	verbVariations.add(root + "उनोस");//ramaunos
	    	verbVariations.add(root + "उनुस"); //Ramaunus
	    	verbVariations.add(root + "उनुहोस"); //ramaunuhos
	    	verbVariations.add(root + "उनेछु"); //ramaunechhu
		    verbVariations.add(root + "उनुहुनेछ");//ramaunuhunechha
		    verbVariations.add(root + "उनेछन");//ramaunechhan
		    verbVariations.add(root + "उँछन");//ramaunechhan
		    verbVariations.add(root + "उँछिन");//ramaunchhin
		    verbVariations.add(root + "उँछु"); //ramaunchhu
		    verbVariations.add(root + "उँछे"); //ramaunchhe
		    verbVariations.add(root + "उँछ"); //ramaunchha
		    verbVariations.add(root + "उनेछौ"); //ramaunechhau
		    verbVariations.add(root + "उनेछिन"); //ramaunechhin
		    verbVariations.add(root + "उनेछ"); //ramaunechha
		    verbVariations.add(root + "उनुभयो"); //रमाउनुभयो

	    } else if(wordEndingInNU.endsWith("नु"))  {
	    	verbVariations.add(root + "नोस"); //खानोस
	    	verbVariations.add(root + "नुस"); //खानुस
	    	verbVariations.add(root + "नुहोस"); //खानुहोस
	    	verbVariations.add(root + "नेछु"); //खानेछु
		    verbVariations.add(root + "नुहुनेछ");//खानुहुनेछ
		    verbVariations.add(root + "नेछन");//खानेछन
		    verbVariations.add(root + "न्छन");//खान्छन
		    verbVariations.add(root + "न्छिन");//खान्छिन
		    verbVariations.add(root + "न्छु"); //खान्छु
		    verbVariations.add(root + "न्छे"); //खान्छे
		    verbVariations.add(root + "न्छ"); //खान्छ
		    verbVariations.add(root + "नेछौ"); //खानेछौ
		    verbVariations.add(root + "नेछिन"); //खानेछिन
		    verbVariations.add(root + "नेछ"); //खानेछ
		    verbVariations.add(root + "नुभयो"); //खानुभयो, रमाउनुभयो
	    }
	    
	    System.out.println(verbVariations);
	    return verbVariations;



		//TODO: Implement the following
		//KHAUNJEL, RAMAUNJEL,
		// CHALUNJEN, THAGUNJEL
		//khainu, ramainu, chalinu, thaginu
		//khayera, ramayera, chalera, thagiyera
		//Khandaina, ramaundaina,, --> other negations
		//khandai, gardai
		//nakhannus, nagarnos, nathaga --> etc.
		//KHAYEKALE, RAMAYEKALE, THAGEKALE
		//NAKHAYEKALE
	    
	}
	
	/**
	 * Given a word that ends in NU (नु), this method returns affirmative (positive) verb variations
	 * @param wordEndingInNU
	 * @return
	 */
	public static List<String> getNegativeVerbVariations(String wordEndingInNU) {
		//TODO: Compelete and Validate this method
	    List<String> verbVariations = new ArrayList<String>();
	    
	    if(wordEndingInNU == null || !wordEndingInNU.endsWith("नु")) {
	        System.out.println("Invalid Input. The Parameter should be non empty and end in नु");
	        return verbVariations;
	    }
	    
	    String root = getVerbRoot(wordEndingInNU); //NU 
	    
	    boolean rootEndsInHalant = root.endsWith('\u094D'+"");
	    
	    verbVariations.add("न" + removeHalant(root)); //नखा, नरमा
	    verbVariations.add("न" + wordEndingInNU); //नखानु,नरमाउनु
	    
	    if(rootEndsInHalant) {
	    	verbVariations.add(root + "एन"); //खाएन, रमाएन, चल्यो//TODO: fix issue with chalena
		    verbVariations.add(root + "एनौ"); //खाएनौ, रमाएनौ //TODO: fix issue with chalenau
		    
	        verbVariations.add(removeHalant(root) + '\u0947'+'\u0901'); //चलेँ
	        
	        verbVariations.add("न" + removeHalant(root) +'\u0947'+"को");  //चलेको
	        verbVariations.add(removeHalant(root) +'\u0947'+"छ"); //चलेछ
	        verbVariations.add(removeHalant(root) +'\u0947'); //चले
	        
	        verbVariations.add(removeHalant(root) + "\u093F" + "नन");//चलिनन
	        verbVariations.add(removeHalant(root) + "\u093F" + "नस"); //चलिनस
	        verbVariations.add(removeHalant(root) + "\u0940" + "न"); //चलीन Wrong Hraswa/Dirgha but possible
	        verbVariations.add(removeHalant(root) + "\u093F"); // चलिन 
	        verbVariations.add(removeHalant(root) + "\u093F" + "छे"); //चलिछे

	        
	    } else {
	    
	    	verbVariations.add("न" + root + "एको");  //नखाएको, नरमाएको
	    	verbVariations.add("न" + root + "येको");  //खायेको, रमायेको
	    	verbVariations.add("न" + root + "ऊ");//नखाऊ, नरमाऊ
	    	verbVariations.add(root + "एन"); //खाएन, रमाएन
		    verbVariations.add(root + "एनौ"); //खाएनौ, रमाएनौ
		    verbVariations.add(root + "इन"); // खाइन, रमाइन
	        verbVariations.add(root + "एनछ"); //खाएनछ, रमानएछ
	        verbVariations.add(root + "येनछ"); //खायेनछ, रमायेनछ
	        verbVariations.add(root + "एनन");//खाएनन, रमायेनन
	        verbVariations.add(root + "येनन");//खायेनन, रमायेनन
	        verbVariations.add(root + "ईनन");//खाईनन, रमाईनन
	        verbVariations.add(root + "ईनस"); //खाईस, रमाईनस
	        verbVariations.add(root + "ईन"); //खाईन, रमाईन
	        verbVariations.add(root + "इनन");//खाइनन, रमाइनन
            verbVariations.add(root + "इनस"); //खाइनस, रमाइनस
	        verbVariations.add(root + "नछे"); //खाइनछे, रमाइनछे
	        
	    }

	    
	    if(wordEndingInNU.endsWith("उनु")) {
	    	System.out.println("Here");
	    	verbVariations.add("न" + root + "उनोस");//naramaunos
	    	verbVariations.add("न" + root + "उनुस"); //naramaunus
	    	verbVariations.add("न" + root + "उनुहोस"); //naramaunuhos
	    	verbVariations.add(root + "उनेछु"); //ramaunechhu
		    verbVariations.add(root + "उनुहुनेछ");//ramaunuhunechha
		    verbVariations.add(root + "उनेछन");//ramaunechhan
		    verbVariations.add(root + "उँछन");//ramaunechhan
		    verbVariations.add(root + "उँछिन");//ramaunchhin
		    verbVariations.add(root + "उँछु"); //ramaunchhu
		    verbVariations.add(root + "उँछे"); //ramaunchhe
		    verbVariations.add(root + "उँछ"); //ramaunchha
		    verbVariations.add(root + "उनेछौ"); //ramaunechhau
		    verbVariations.add(root + "उनेछिन"); //ramaunechhin
		    verbVariations.add(root + "उनेछ"); //ramaunechha
		    verbVariations.add(root + "उनुभएन"); //रमाउनुभएन

	    } else if(wordEndingInNU.endsWith("नु"))  {
	    	verbVariations.add("न" + root + "नोस"); //खानोस
	    	verbVariations.add("न" + root + "नुस"); //खानुस
	    	verbVariations.add("न" + root + "नुहोस"); //खानुहोस
	    	verbVariations.add(root + "नेछु"); //खानेछु
		    verbVariations.add(root + "नुहुनेछ");//खानुहुनेछ
		    verbVariations.add(root + "नेछन");//खानेछन
		    verbVariations.add(root + "न्छन");//खान्छन
		    verbVariations.add(root + "न्छिन");//खान्छिन
		    verbVariations.add(root + "न्छु"); //खान्छु
		    verbVariations.add(root + "न्छे"); //खान्छे
		    verbVariations.add(root + "न्छ"); //खान्छ
		    verbVariations.add(root + "नेछौ"); //खानेछौ
		    verbVariations.add(root + "नेछिन"); //खानेछिन
		    verbVariations.add(root + "नेछ"); //खानेछ
		    verbVariations.add(root + "नुभयो"); //खानुभयो, रमाउनुभयो
	    }

	    

	    
	    
	    System.out.println(verbVariations);
	    return verbVariations;
	    
	}
	

    private static String removeHalant(String root) {
        if (root != null && root.endsWith('\u094D'+"")) {
            root = TextUtils.replaceLast(root, '\u094D'+"", "");
        }
        
        return root;
    }

    public static String getVerbRoot(String wordEndingInNU) {
        if(wordEndingInNU == null || !wordEndingInNU.endsWith("नु") || wordEndingInNU.indexOf(" ") >0) {
            System.out.println("Invalid Word or Word Ending " + wordEndingInNU + " Verb must end in: नु");
            return wordEndingInNU;
        }
        
        String wordWithoutNU ="";
        if(wordEndingInNU.endsWith("उनु")) {
            wordWithoutNU = TextUtils.replaceLast(wordEndingInNU, "उनु", "");
        } else if (wordEndingInNU.endsWith("नु")) {
            wordWithoutNU = TextUtils.replaceLast(wordEndingInNU, "नु", "");
        }
        
        System.out.println(wordWithoutNU);
        
        return wordWithoutNU;
    }

}
