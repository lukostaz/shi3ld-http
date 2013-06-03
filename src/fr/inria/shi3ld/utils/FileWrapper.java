package fr.inria.shi3ld.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;

public class FileWrapper {
	public static void writeToFile(InputStream sourceInputStream, String targetFileLocation) throws IOException {
		try {
			OutputStream out = new FileOutputStream(new File(targetFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(targetFileLocation));
			while ((read = sourceInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			throw e;
		}
	}
	
	public static File[] listOfFiles(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		return listOfFiles;
	}
	
	public static String readFile(String fileName) throws Exception{
		String result = "";
		try {
			FileInputStream file = new FileInputStream(fileName);
			DataInputStream in = new DataInputStream(file);
			byte[] b = new byte[in.available()];
			in.readFully(b);
			in.close();
			result = new String(b, 0, b.length, "Cp850");
			System.out.println(result);
		} catch (FileNotFoundException e) {
			throw new WebApplicationException(404);
		} catch (Exception e){
			throw e;
		}
		return result;
	}
	
	public static void saveTextFile(String content, String path) throws IOException {
		FileOutputStream fop = null;
		File file;
		try {
			file = new File(path);
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
			System.out.println("Done");
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void deleteFile(String fileName) throws FileNotFoundException, IllegalArgumentException {
	    // A File object to represent the filename
	    File f = new File(fileName);

	    // Make sure the file or directory exists and isn't write protected
	    if (!f.exists())
	      throw new FileNotFoundException();

	    if (!f.canWrite())
	      throw new IllegalArgumentException("Delete: write protected: "
	          + fileName);

	    // If it is a directory, make sure it is empty
	    if (f.isDirectory()) {
	      String[] files = f.list();
	      if (files.length > 0)
	        throw new IllegalArgumentException(
	            "Delete: directory not empty: " + fileName);
	    }

	    // Attempt to delete it
	    boolean success = f.delete();

	    if (!success)
	      throw new IllegalArgumentException("Delete: deletion failed");
	  }
	
	public static boolean fileExists(String filePath) {
		File f = new File(filePath);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}
}
