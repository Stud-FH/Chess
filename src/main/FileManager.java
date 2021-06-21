package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class FileManager<T> {
	
	//modifier
	public static final int SORT_BY_DATE = 1;
	public static final int SORT_BY_NAME = 2;

	
	protected final String PATH;			//file path
	protected final String BASIC_NAME;		//default filename
	protected final String EXTENSION;		//file type

	protected int sortingModifier;			//modifier
	protected boolean autoDelete;			//delete corrupted files automatically?
	
	//Constructor
	public FileManager(String path, String basicName) {
		
		PATH = path;
		BASIC_NAME = basicName;
		EXTENSION = initExtension();
		
	}
	
	//possible to override in order to use different extensions
	protected String initExtension() {
		return ".ser";
	}
	
	//possible to override for priority JFrame
	protected JFrame getJFrame() {
		return null;
	}
	
	
	//save file with given name
	public void save(T object, String filename) throws IOException {
		FileOutputStream fileOut = new FileOutputStream(extendedFilename(filename));
		ObjectOutputStream objOut= new ObjectOutputStream(fileOut);
		
		objOut.writeObject(object);
		
		objOut.close();
		fileOut.close();
	}
	
	//load file with given name
	public T open(String filename) throws FileNotFoundException, ClassNotFoundException {
		try {
			FileInputStream fileIn = new FileInputStream(extendedFilename(filename));
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			
			@SuppressWarnings("unchecked")
			T object = (T) objIn.readObject();
			
			fileIn.close();
			objIn.close();
			
			return object;
			
		} catch (FileNotFoundException e) {
			throw e;
		} catch (ClassNotFoundException e) {
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			deleteFileDecision(filename, corruptedFileMessage(filename));
		}
		return null;
	}
	
	//get list of existing files names
	public ArrayList<String> getFilenames() {

		ArrayList<File> fileList = new ArrayList<File>();
		ArrayList<String> nameList = new ArrayList<String>();
		
		File folder = new File(PATH);
		
		if (folder.listFiles() == null) {
			return nameList;
		}
		
		for (File file : folder.listFiles()) {
			if (fileCondition(file)) {
				fileList.add(file);
			}
		}
		
		fileList = sort(fileList);
		
		for (File file : fileList) {
			nameList.add(cleanFilename(file.getName()));
		}
		
		return nameList;
	
	}
	
	//generate name with basic name and calculated number
	public String generateName() {
		return BASIC_NAME + (highestFileNumber()+ 1);
	}
	
	//rename file by name
	public void rename(String filename, String newName) {
		File file = new File(extendedFilename(filename));
		File renamed = new File(extendedFilename(newName));
		
		file.renameTo(renamed);
	}
	
	//delete file by name
	public void deleteFile(String filename) {
		File file = new File(extendedFilename(filename));
		
		if (file.exists() && !file.delete()) {
			file.deleteOnExit();
		}
	}
	
	//set modifier
	public void modify(int modifier) {
		switch(modifier) {
		case SORT_BY_DATE:		
		case SORT_BY_NAME:		sortingModifier = modifier;
								break;
		}
	}
	
	//is file?
	private boolean fileCondition(File file) {
		return file.isFile();
	}
	
	//get name without path or extension
	private String cleanFilename(String filename) {
		if (filename.startsWith(PATH)) {
			filename = filename.substring(PATH.length());
		}
		
		if (filename.endsWith(EXTENSION)) {
			filename = filename.substring(0, filename.length() - EXTENSION.length());
		}
		return filename;
	}
	
	//get name with path and extension
	private String extendedFilename(String filename) {
		if (!filename.startsWith(PATH)) {
			filename = PATH + filename;
		}
		
		if (!filename.endsWith(EXTENSION)) {
			filename = filename + EXTENSION;
		}
		return filename;
	}
	
	//sort files by modifier
	private ArrayList<File> sort(ArrayList<File> list) {
		ArrayList<File> sorted = new ArrayList<File>();
		File first = null;
		
		for (int i = 0; i < list.size(); i++) {

			for (File file : list) {
				
				if (first == null || testPriority(file, first)) {
					first = file;
				}
			}
			
			list.remove(first);
			sorted.add(first);
			first = null;
		}
		return sorted;
	}
	
	//will first parameter file be listed first?
	private boolean testPriority(File file, File comparison) {
		switch(sortingModifier) {
		case SORT_BY_NAME:			return testStringPriority(cleanFilename(file.getName()), cleanFilename(comparison.getName()));
									
		case SORT_BY_DATE:			return file.lastModified() > comparison.lastModified();
		}
		return true;
	}

	//will first parameter string be listed first?
	private boolean testStringPriority(String s1, String s2) {
		
		int number1 = getPrefixNumber(s1);
		int number2 = getPrefixNumber(s2);
		
		if (number1 < number2) {
			return true;
		}
		if (number1 > number2) {
			return false;
		}
		
		if (Character.toLowerCase(s1.charAt(0)) == Character.toLowerCase(s2.charAt(0))) {
			return testStringPriority(s1.substring(1), s2.substring(1));
		} else {
			return Character.toLowerCase(s1.charAt(0)) < Character.toLowerCase(s2.charAt(0));
		}
		
	}
	
	//get number at string front
	private int getPrefixNumber(String str) {
		int index = 0;
		int number = 0;
		
		while (str.length() > index && Character.isDigit(str.charAt(index))) {
			String sub = str.substring(index, index + 1);
			int digit = Integer.parseInt(sub);
			number *= 10;
			number += digit;
			index++;
		}
		return number;
	}
	
	//get number at string end
	private int getSuffixNumber(String str) {
		int index = str.length() - 1;
		int number = 0;
		int multiplier = 1;
		
		while (index >= 0 && Character.isDigit(str.charAt(index))) {
			String sub = str.substring(index, index + 1);
			int digit = Integer.parseInt(sub);
			number += digit * multiplier;
			multiplier *= 10;
			index--;
		}
		return number;
	}
	
	//get highest number of all default-named files
	private int highestFileNumber() {
		int highestNumber = 0;
		
		for (String filename : getFilenames()) {
			
			if (filename.contains(BASIC_NAME)) {
				
				int gameNumber = getSuffixNumber(filename);
				if (gameNumber > highestNumber) {
					highestNumber = gameNumber;
				}
			}
		}
		return highestNumber;
	}
	
	//generate error message
	private String corruptedFileMessage(String filename) {
		filename = cleanFilename(filename);
		return "We're sorry! \"" + filename + "\" is corrupted. Delete it?";
	}
	
	//ask user if corrupted file should be deleted or delete automatically
	private void deleteFileDecision(String filename, String message) {
		File file = new File(extendedFilename(filename));
		
		if (file.exists()) {
			filename = cleanFilename(filename);
			if (autoDelete) {
				deleteFile(filename);
			} else if (getJFrame() != null
					&& JOptionPane.showConfirmDialog(getJFrame(), message, "Delete file \"" + filename + "\"?",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

				deleteFile(filename);
			} 
		}
	}
	
	
	
}
