package wordBot;

import java.util.LinkedList;
import java.util.Scanner;

public class WordBot {

	public LinkedList<LinkedList<Word>> goodWords, badWords; //Probability is current / total
	private Scanner s = new Scanner(System.in);
	
	public double offsetPercent = 15.0;
	private final double OFFSET_MAX = 25.0;
	public double goodCount = 0;
	public double badCount = 0;
	boolean updateGoodCount = false;
	boolean updateBadCount = false;
	private FW fileWriter = null;
	
	boolean printStats = true;
	
	public WordBot() {
		
		goodWords = new LinkedList<LinkedList<Word>>();
		badWords = new LinkedList<LinkedList<Word>>();
		
		fileWriter = new FW(this);
		
		fileWriter.read();
		
		if(printStats) {
			printGoodList();
			printBadList();
		}
	}
	
	private void moveUp(String word, LinkedList<LinkedList<Word>> list, int n, boolean neutral) {
		boolean good = false;
		if(n == 0) good = true;
		
		boolean containsWord = false;
		
		if(list.size() == 0) {
			list.add(new LinkedList<Word>());
			list.get(0).add(new Word(word, 0));
			containsWord = true;
		}
		
		int length = list.size();

		for(int i = 0; i < length; i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(equals(word, list.get(i).get(j).getWord())) {
					
					containsWord = true;
					int age = list.get(i).get(j).getAge();
					if(i + 1 == length) {
						list.add(list.size(), new LinkedList<Word>());
						if(list.size() > badWords.size()) badWords.add(new LinkedList<Word>());
						if(list.size() > goodWords.size()) goodWords.add(new LinkedList<Word>());
					}
					double c = Math.round(generateConstant(list.get(i).get(j), list));
					list.get(i).remove(j);
					if(i + c < list.size() && c >= 1) {
						if(!neutral) {
							list.get((int)Math.round(i + c)).add(new Word(word, age)); 
						}
					}
					else {
						list.get(i + 1).add(new Word(word, age));
					}
					break;
				}
			}
		}
		
		if(!containsWord) {
			if(good) {
				list.get((int)Math.round(list.size()/2)).add(0, new Word(word, 0));
			}
			else {
				if(n == 1) {
					list.get((int)Math.round(list.size()/2)).add(0, new Word(word, 0));
				}
				if(n == 2) list.get(0).add(0, new Word(word, 0));
			}
		}
		
	}
	
	private void moveDown(String word, LinkedList<LinkedList<Word>> list, int n, boolean neutral) {
		boolean good = false;
		if(n == 0) good = true;
		
		boolean containsWord = false;
		
		if(list.size() == 0) {
			list.add(new LinkedList<Word>());
			list.get(0).add(new Word(word, 0));
			containsWord = true;
		}
		
		int length = list.size();
		
		for(int i = 0; i < length; i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(equals(word, list.get(i).get(j).getWord())) {
					int age = list.get(i).get(j).getAge();
					containsWord = true;
					if(i == 0) {
						list.get(i).remove(j);
						list.get(0).add(new Word(word, age));
					} else {
						double c = Math.round(generateConstant(list.get(i).get(j), list));
						list.get(i).remove(j);
						if(i - c > 0 && c >= 1 && !neutral) {
							list.get((int)Math.round(i - c)).add(new Word(word, age));
						}
						else list.get(i - 1).add(new Word(word, age));
						
					}
					break;
				}
				
				
			}
			
		}
		
		if(!containsWord) {
			if(good) {
				list.get((int)Math.round(list.size()/2)).add(0, new Word(word, 0));
				updateGoodCount = true;
			}
			else {
				if(n == 1) {
					list.get((int)Math.round(list.size()/2)).add(0, new Word(word, 0));
					updateBadCount = true;
				}
				if(n == 2) list.get(0).add(0, new Word(word, 0));
			}
			return;
		}
		
	}
	
	public double generatePercentage(String word, LinkedList<LinkedList<Word>> list) {
		if(list.size() == 1) return 100;
		if(list.size() > 0) {
			double num = 0;
			double den = list.size();
			for(int i = 1; i < list.size(); i++) {
				for(int j = 0; j < list.get(i).size(); j++) {
					if(equals(word, list.get(i).get(j).getWord())) {
						num = i + 1;
					}
				}
			}
			return 100 * (num / den);
		} else {
			return 0;
		}
	}
	
	public double generateConstant(Word word, LinkedList<LinkedList<Word>> list) {
		return (list.size() / 5) * (1 / Math.log(word.getAge() + 2));
	}
	
	public void moveUpGoodWord(String word, boolean neutral) {
		moveUp(word, goodWords, 0, neutral);
	}
	
	public void moveUpBadWord(String word, boolean neutral) {
		moveUp(word, badWords, 1, neutral);
	}
	
	public void moveDownGoodWord(String word, boolean neutral) {
		moveDown(word, goodWords, 0, neutral);
	}
	
	public void moveDownBadWord(String word, boolean neutral) {
		moveDown(word, badWords, 1, neutral);
	}
	
	public void printGoodList() {
		System.out.print("Good words (" + goodWords.size() + " levels): [");
		
		for(int i = 0; i < goodWords.size(); i++) {
			System.out.print("[");
			
			for(int j = 0; j < goodWords.get(i).size(); j++) {
				String word = goodWords.get(i).get(j).getWord();
				System.out.print(word + " " + Math.round(generatePercentage(word, goodWords)) + "% x" + goodWords.get(i).get(j).getAge());
				
				if(j < goodWords.get(i).size() - 1) System.out.print(", ");
			}
			
			if(i < goodWords.size() - 1) System.out.print("], ");
			else System.out.print("]");
		}
		
		System.out.print("]");
		System.out.println();
	}
	
	public void printBadList() {
		System.out.print("Bad words (" + badWords.size() + " levels): [");
		
		for(int i = 0; i < badWords.size(); i++) {
			System.out.print("[");
			
			for(int j = 0; j < badWords.get(i).size(); j++) {
				String word = badWords.get(i).get(j).getWord();
				System.out.print(word + " " + Math.round(generatePercentage(word, badWords)) + "% x" + badWords.get(i).get(j).getAge());
				
				if(j < badWords.get(i).size() - 1) System.out.print(", ");
			}
			
			if(i < badWords.size() - 1) System.out.print("], ");
			else System.out.print("]");
		}
		
		System.out.print("]");
		System.out.println();
	}
	
	private boolean equals(String word1, String word2) {
		return word1.equalsIgnoreCase(word2);
	}
	
	private int determineIfGood(String s) { //0 is good, 1 is bad, 2 is neutral
		String[] words = s.split(" ");
		double goodPercent = 0;
		double badPercent = 0;
		int notCount = 0; //if not is even, then same. If not is odd, then reverse
		
		for(int i = 0; i < words.length; i++) {
			if(words[i].equalsIgnoreCase("not")) notCount++;
			
			double g = generatePercentage(words[i], goodWords);
			double b = generatePercentage(words[i], badWords);
			if(Math.abs(g - b) > offsetPercent) {
				goodPercent += g;
				badPercent += b;
			}
			
		}
		if(Math.abs(goodPercent - badPercent) > offsetPercent) {
			if(goodPercent > badPercent) {
				if(notCount % 2 == 0) return 0;
				else return 1;
			}
			if(badPercent > goodPercent) {
				if(notCount % 2 == 0) return 1;
				else return 0;
			}
		}
		return 2;
	}
	
	public void start() {
		while(true) {
			System.out.print("Input a sentence: ");
			String input = s.nextLine();
			
			//Display the connotation of the sentence here
			int n = determineIfGood(input);	
			
			if(n == 0) {
				System.out.println("This sentence has a POSITIVE connotation.");
			} else if(n == 1) {
				System.out.println("This sentence has a NEGATIVE connotation.");
			} else {
				System.out.println("This sentence has a NEUTRAL connotation.");
			}
			
			boolean correct = true;
			
			System.out.println();
			System.out.print("Was I correct? ");
			String goodOrBad = s.nextLine(); //DEFAULT IS CORRECT
			if(goodOrBad.equalsIgnoreCase("Yes") || goodOrBad.equalsIgnoreCase("Y")) correct = true;
			if(goodOrBad.equalsIgnoreCase("No") || goodOrBad.equalsIgnoreCase("N")) correct = false;
			
			System.out.println();
			
			if(correct) {
				organizeWords(input, n);
				if(offsetPercent < OFFSET_MAX) offsetPercent += 0.1;
			}
			else {
				if(n == 0) {
					System.out.print("Was it negative or neutral? ");
					String ans = s.nextLine();
					if(ans.equalsIgnoreCase("Negative") || goodOrBad.equalsIgnoreCase("N")) organizeWords(input, 1);
					else organizeWords(input, 2); //DEFAULT IS NEUTRAL
				} else if(n == 1) {
					System.out.print("Was it positive or neutral? ");
					String ans = s.nextLine();
					if(ans.equalsIgnoreCase("Positive") || goodOrBad.equalsIgnoreCase("P")) organizeWords(input, 0);
					else organizeWords(input, 2); //DEFAULT IS NEUTRAL
				} else {
					System.out.print("Was it positive or negative? ");
					String ans = s.nextLine();
					if(ans.equalsIgnoreCase("Positive") || goodOrBad.equalsIgnoreCase("P")) organizeWords(input, 0);
					else organizeWords(input, 1); //DEFAULT IS NEGATIVE
				}
				System.out.println();
			}
			
			/*if(updateGoodCount) goodCount++;
			updateGoodCount = false;
			if(updateBadCount) badCount++;
			updateBadCount = false; */
			
			if(printStats) {
				System.out.println();
				printGoodList();
				printBadList();
			}
			

			
		}
	}
	
	public int getPosition(String str, LinkedList<LinkedList<Word>> list) {
		for(int i = 0; i < list.size(); i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(equals(str, list.get(i).get(j).getWord())) {
					return i;
				}
			}
		}
		
		return 0;
	}
	
	public int getAge(String str, LinkedList<LinkedList<Word>> list) {
		for(int i = 0; i < list.size(); i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(equals(str, list.get(i).get(j).getWord())) {
					return list.get(i).get(j).getAge();
				}
			}
		}
		
		return 0;
	}
	
	public Word getWord(String str, LinkedList<LinkedList<Word>> list) {
		for(int i = 0; i < list.size(); i++) {
			for(int j = 0; j < list.get(i).size(); j++) {
				if(equals(str, list.get(i).get(j).getWord())) {
					return list.get(i).get(j);
				}
			}
		}
		
		return null;
	}
	
	public void organizeWords(String s, int det) {
		String[] split = s.split(" ");
		int notCount = 0;
		for(String str: split) {
			if(str.equalsIgnoreCase("not")) notCount++;
		}
		
		for(String str: split) {
			
			if(!str.equalsIgnoreCase("not")) {
				if(det == 0) {
					if(notCount % 2 == 0) { //GOOD
						moveUpGoodWord(str, false);
						moveDownBadWord(str, false);
						if(getWord(str, goodWords) != null) getWord(str, goodWords).age++;
						if(getWord(str, badWords) != null) getWord(str, badWords).age++;
					} else {
						moveUpBadWord(str, false);
						moveDownGoodWord(str, false);
						if(getWord(str, goodWords) != null) getWord(str, goodWords).age++;
						if(getWord(str, badWords) != null) getWord(str, badWords).age++;
					}
				} else if(det == 1){ //BAD
					if(notCount % 2 == 0) {
						moveUpBadWord(str, false);
						moveDownGoodWord(str, false);
						if(getWord(str, goodWords) != null) getWord(str, goodWords).age++;
						if(getWord(str, badWords) != null) getWord(str, badWords).age++;
					} else {
						moveUpGoodWord(str, false);
						moveDownBadWord(str, false);
						if(getWord(str, goodWords) != null) getWord(str, goodWords).age++;
						if(getWord(str, badWords) != null) getWord(str, badWords).age++;
					}
				} else { //NEUTRAL
					double difference = generatePercentage(str, goodWords) - generatePercentage(str, badWords);
					double distance = Math.sqrt(Math.abs(difference) * 0.01 * goodWords.size());
					//System.out.println(difference);
					//System.out.println(distance);
					boolean goodMiddle = false;
					boolean badMiddle = false;
					
					boolean containsWord = false;
					for(int i = 0; i < goodWords.size(); i++) {
						for(int j = 0; j < goodWords.get(i).size(); j++) {
							if(str.equalsIgnoreCase(goodWords.get(i).get(j).getWord())) {
								containsWord = true;
							}
						}
					}
					if(!containsWord) {
						goodWords.get((int)Math.floor(goodWords.size() / 2)).add(new Word(str, 0));
						badWords.get((int)Math.floor(badWords.size() / 2)).add(new Word(str, 0));
					}
					
					if(goodWords.size() % 2 == 1) {
						int size = goodWords.size() + 1;
						if(getPosition(str, goodWords) + 1 == size / 2) goodMiddle = true;
					} else {
						if(getPosition(str, goodWords) + 1 == goodWords.size() / 2) goodMiddle = true;
						if(getPosition(str, goodWords) == goodWords.size() / 2) goodMiddle = true;
					}
					if(badWords.size() % 2 == 1) {
						int size = badWords.size() + 1;
						if(getPosition(str, badWords) + 1 == size / 2) badMiddle = true;
						if(getPosition(str, badWords) == size / 2) badMiddle = true;
					} else {
						if(getPosition(str, badWords) + 1 == badWords.size() / 2) badMiddle = true;
						if(getPosition(str, badWords) == badWords.size() / 2) badMiddle = true;
					}
					
					
					if(difference > 0) { //Good word ahead of bad word
						if(difference <= offsetPercent) {
							if(!badMiddle) moveUpBadWord(str, true);
							if(!goodMiddle) moveDownGoodWord(str, true);
						}
						if(difference > offsetPercent) {
							//(distance / 10) * 1 / ln (age + 2)
							double con = Math.floor(Math.abs(distance)) / badWords.size();
							double con1 = Math.floor(Math.abs(distance)) / goodWords.size();
							//System.out.println(con);
							if(con == 0) con = 1;
							if(con1 == 0) con1 = 1;
							for(double i = 0; i < con; i++) {
								if(!badMiddle) moveUpBadWord(str, true);
							}
							for(double i = 0; i < con1; i++) {
								if(!goodMiddle) moveDownGoodWord(str, true);
							}
						}
						goodWords.add(new LinkedList<Word>());
						badWords.add(0, new LinkedList<Word>());
					} else if (difference < 0) { //Bad word ahead of good word
						if(difference >= -offsetPercent) {
							if(!goodMiddle) moveUpGoodWord(str, true);
							if(!badMiddle) moveDownBadWord(str, true);
						}
						if(difference < -offsetPercent) {
							//(distance / 10) * 1 / ln (age + 2)
							double con = Math.floor(Math.abs(distance)) / badWords.size();
							double con1 = Math.floor(Math.abs(distance)) / goodWords.size();
							//System.out.println(con);
							if(con == 0) con = 1;
							if(con1 == 0) con1 = 1;
							for(double i = 0; i < con1; i++) {
								if(!goodMiddle) moveUpGoodWord(str, true);
							}
							for(double i = 0; i < con; i++) {
								if(!badMiddle) moveDownBadWord(str, true);
							}
						}
						badWords.add(new LinkedList<Word>());
						goodWords.add(0, new LinkedList<Word>());
					}
					if(getWord(str, goodWords) != null) getWord(str, goodWords).age++;
					if(getWord(str, badWords) != null) getWord(str, badWords).age++;
				}
			}
		}
		fileWriter.write();
	}
	
	public void inputOptions(String s) {
		
	}

}
