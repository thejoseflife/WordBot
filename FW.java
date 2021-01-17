package wordBot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class FW {

	File goodWords = null;
	File badWords = null;
	File numbers = null;
	private WordBot bot;
	public double[] accessedNumbers = new double[3];
	private LinkedList<LinkedList<Word>> goodList;
	private LinkedList<LinkedList<Word>> badList;
	private LinkedList<Integer> goodAges;
	private LinkedList<Integer> badAges;
	
	public FW(WordBot bot) {
		this.bot = bot;
		
		goodList = new LinkedList<LinkedList<Word>>();
		badList = new LinkedList<LinkedList<Word>>();
		goodAges = new LinkedList<Integer>();
		badAges = new LinkedList<Integer>();
		
		/*for(int i = 0; i < 100; i++) {
			badAges.add(1);
			goodAges.add(1);
		}*/
	}
	
	public void write() {
		writeGoodWords();
		writeBadWords();
		writeNumbers();
	}
	
	public void read() {
		readNumbers();
		readGoodWords();
		readBadWords();
		
		bot.goodCount = accessedNumbers[0];
		bot.badCount = accessedNumbers[1];
		bot.offsetPercent = accessedNumbers[2];
	}
	
	private void readGoodWords() {
		goodWords = new File("good_words.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(goodWords));
			String line = null;
			
			int count = 0;
			int index = 0;
			int ageCount = 0;
			String tempWord = "";
			while((line = reader.readLine()) != null) {
				
				if(count % 2 == 1) {
					if(index < Integer.parseInt(line.trim())) {
						index = Integer.parseInt(line.trim());
					}
					if(index + 1 > goodList.size()) goodList.add(new LinkedList<Word>());
					if(!tempWord.equalsIgnoreCase("[+++]")) {
						goodList.get(index).add(new Word(tempWord, goodAges.get(ageCount)));
						ageCount++;
					}
					
				} else {
					tempWord = line.trim();
					
				}
				
				count++;
			}
			reader.close();
			bot.goodWords = goodList;
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeGoodWords() {
		goodWords = new File("good_words.txt");
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(goodWords));
			for(int i = 0; i < bot.goodWords.size(); i++) {
				for(int j = 0; j < bot.goodWords.get(i).size(); j++) {
					output.append(bot.goodWords.get(i).get(j).getWord());
					output.newLine();
					output.append(String.valueOf(i));
					output.newLine();
				}
				if(bot.goodWords.get(i).size() == 0) { //If level is blank, a "[+++]" is written
					output.append("[+++]");
					output.newLine();
					output.append(String.valueOf(i));
					output.newLine();
				}
			}
			output.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readBadWords() {
		badWords = new File("bad_words.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(badWords));
			String line = null;
			
			int count = 0;
			int index = 0;
			int ageCount = 0;
			String tempWord = "";
			while((line = reader.readLine()) != null) {
				
				if(count % 2 == 1) {
					if(index < Integer.parseInt(line.trim())) {
						index = Integer.parseInt(line.trim());
					}
					if(index + 1 > badList.size()) badList.add(new LinkedList<Word>());
					if(!tempWord.equalsIgnoreCase("[+++]")) {
						badList.get(index).add(new Word(tempWord, badAges.get(ageCount)));
						ageCount++;
					}
					
				} else {
					tempWord = line.trim();
					
				}
				
				count++;
			}
			reader.close();
			bot.badWords = badList;
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeBadWords() {
		badWords = new File("bad_words.txt");
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(badWords));
			for(int i = 0; i < bot.badWords.size(); i++) {
				for(int j = 0; j < bot.badWords.get(i).size(); j++) {
					output.append(bot.badWords.get(i).get(j).getWord());
					output.newLine();
					output.append(String.valueOf(i));
					output.newLine();
				}
				if(bot.badWords.get(i).size() == 0) { //If level is blank, a " " is written
					output.append("[+++]");
					output.newLine();
					output.append(String.valueOf(i));
					output.newLine();
				}
			}
			output.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readNumbers() {
		numbers = new File("numbers.txt");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(numbers));
			String line = reader.readLine();
			accessedNumbers[0] = Double.parseDouble(line.trim());
			line = reader.readLine();
			accessedNumbers[1] = Double.parseDouble(line.trim());
			line = reader.readLine();
			accessedNumbers[2] = Double.parseDouble(line.trim());
			
			while((line = reader.readLine()) != null) {
				//System.out.println(line);
				if(line.equalsIgnoreCase("[+++]")) break;
				goodAges.add(Integer.parseInt(line.trim()));
			}
			while((line = reader.readLine()) != null) {
				badAges.add(Integer.parseInt(line.trim()));
				//System.out.println(line);
			}
	    	reader.close();
	    	
	    	
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeNumbers() { //goodCount, badCount, offsetPercent
		numbers = new File("numbers.txt");
		try {
			BufferedWriter output = new BufferedWriter(new FileWriter(numbers));
			output.append(String.valueOf(bot.goodCount));
			output.newLine();
			output.append(String.valueOf(bot.badCount));
			output.newLine();
			output.append(String.valueOf(bot.offsetPercent));
			output.newLine();
			
			for(int i = 0; i < bot.goodWords.size(); i++) {
				for(int j = 0; j < bot.goodWords.get(i).size(); j++) {
					output.append(String.valueOf(bot.goodWords.get(i).get(j).getAge()));
					output.newLine();
				}
			}
			
			output.append("[+++]");
			output.newLine();
			
			
			for(int i = 0; i < bot.badWords.size(); i++) {
				for(int j = 0; j < bot.badWords.get(i).size(); j++) {
					output.append(String.valueOf(bot.badWords.get(i).get(j).getAge()));
					output.newLine();
				}
			}
			
			
			output.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printGoodList() {
		System.out.print("Good words: [");
		
		for(int i = 0; i < goodList.size(); i++) {
			System.out.print("[");
			
			for(int j = 0; j < goodList.get(i).size(); j++) {
				String word = goodList.get(i).get(j).getWord();
				System.out.print(word);
				
				if(j < goodList.get(i).size() - 1) System.out.print(", ");
			}
			
			if(i < goodList.size() - 1) System.out.print("], ");
			else System.out.print("]");
		}
		
		System.out.print("]");
		System.out.println();
	}
	
	public void printBadList() {
		System.out.print("Bad words: [");
		
		for(int i = 0; i < badList.size(); i++) {
			System.out.print("[");
			
			for(int j = 0; j < badList.get(i).size(); j++) {
				String word = badList.get(i).get(j).getWord();
				System.out.print(word);
				
				if(j < badList.get(i).size() - 1) System.out.print(", ");
			}
			
			if(i < badList.size() - 1) System.out.print("], ");
			else System.out.print("]");
		}
		
		System.out.print("]");
		System.out.println();
	}
	
}
