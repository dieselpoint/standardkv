/*******************************************************************************
 * Copyright 2010 Dieselpoint, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.dieselpoint.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.util.Scanner;

/**
 * Contains static methods for manipulating files
 */
public class FileUtil {
	
	// prevent instantiation
	private FileUtil() {} 

	/**
	 * Performs a recursive delete of the contents of a directory.
	 * @throws IOException if delete fails
	 */
	public static void deleteDir(File file) throws IOException {
		deleteDir(file.toString());
	}
	
	
	/**
	 * Performs a recursive delete of the contents of a directory.
	 * @throws IOException if delete fails
	 */
	public static void deleteDir(String dir) throws IOException {
		Path directory = Paths.get(dir);
		if (!Files.exists(directory)) {
			return;
		}
		
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
		   @Override
		   public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		       Files.delete(file);
		       return FileVisitResult.CONTINUE;
		   }

		   @Override
		   public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		       Files.delete(dir);
		       return FileVisitResult.CONTINUE;
		   }
		});
	}
	
	
	
	/**
	 * Copy files from one dir to another recursively.
	 */
	public static void copyDir(String fromDir, String toDir) throws IOException {
		File from = new File(fromDir);
		File to = new File(toDir);
		copyDir(from, to);
	}
	
	
	
	
	/**
	 * Copy files from one dir to another recursively.
	 */
	public static void copyDir(File from, File to) throws IOException {
		copyDir(from, to, false);
	}
	
	private static void copyDir(File from, File to, boolean makeSubDir) throws IOException {

		if (from.getName().startsWith(".svn"))
			return;
		
		if (from.isDirectory()) {
			File toDir;
			if (makeSubDir) {
				toDir = new File(to, from.getName());
			} else {
				toDir = to;
			}
			
			File[] files = from.listFiles();
			if (files == null)
				return;
			for (File file : files) {
				copyDir(file, toDir, true);
			}

		} else {

			File toFile = new File(to, from.getName());

			File toFileParentDir = toFile.getParentFile();
			if (!toFileParentDir.exists())
				toFileParentDir.mkdirs();

			// copy the file if it doesn't exist or the timestamp is different
			if (!toFile.exists()
					|| from.lastModified() != toFile.lastModified()) {

				if (toFile.exists())
					toFile.delete();

				RandomAccessFile randFrom = new RandomAccessFile(from, "r");
				RandomAccessFile randTo = new RandomAccessFile(toFile, "rw");

				appendFile(randTo, randFrom);

				randFrom.close();
				randTo.close();

				toFile.setLastModified(from.lastModified());
			}
		}
	}

	/**
	 * Copy a file from one place to another.
	 * @param from source file
	 * @param to destination file
	 * @throws IOException
	 */
	public static void copyFile(File from, File to) throws IOException {
		RandomAccessFile randFrom = new RandomAccessFile(from, "r");
		RandomAccessFile randTo = new RandomAccessFile(to, "rw");
		appendFile(randTo, randFrom);
		
		randFrom.close();
		randTo.close();

		to.setLastModified(from.lastModified());
	}

	/**
	 * Copy a file from one place to another.
	 * @param from source file
	 * @param to destination file
	 * @throws IOException
	 */
	public static void copyFile(String from, String to) throws IOException {
		copyFile(new File(from), new File(to));
	}	
	
	/**
	 * Copies the content of one file to another file.
	 * @param src source file.
	 * @param dest	destination file.
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static void copyFileNIO(File src, File dest) throws IOException {
		if (!dest.exists()) {
			dest.createNewFile();
		}

		final FileChannel srcChannel = new FileInputStream(src).getChannel();
		final FileChannel destChannel = new FileOutputStream(dest).getChannel();
		//64MB Buffer.
		final int bufferSize = (64 * 1024 * 1024);
		final long size = srcChannel.size();
		long position = 0;
		try {
			while (position < size) {
				position += srcChannel.transferTo(position, bufferSize, destChannel);
			}
			dest.setLastModified(src.lastModified());
		} finally {
			if (srcChannel != null) {
				srcChannel.close();
			}
			if (destChannel != null) {
				destChannel.close();
			}
		}
	}
	
	/**
	 * Append the contents of extra file to main.
	 */
	public static void appendFile(RandomAccessFile main, RandomAccessFile extra) throws IOException {
		
	    // this method uses NIO direct transfer. It delegates the task
		// to the operating system. Only works under 1.4+
		// Unfortunately, can't use this because it crashes with an out of memory error on big files
		//extra.getChannel().transferTo(0, Long.MAX_VALUE, mainFile.getChannel());
		
		byte [] buf = new byte [1024 * 1024]; // 1 mb
		main.seek(main.length());
		extra.seek(0);
		
		while (true) {
			int count = extra.read(buf);
			if (count == -1)
				break;
			main.write(buf, 0, count);
		}
	}
	
	/**
	 * Compare two (short) text files to see if they are identical.
	 * @param file1 name of the first file
	 * @param file2 name of the second file
	 * @return true if the files are the same
	 * @throws Exception
	 */
    public static boolean compareFiles(String file1, String file2) throws Exception {
        String f1 = getFileAsString(file1);
        String f2 = getFileAsString(file2);
        if (!f1.equals(f2)){
        	return false;
        } else{
        	return true;
        }
    }
    
    /**
	 * Read the file and return it as a String, using the standard encoding.
	 * @param filename the name of the file
	 * @return the content of the file as a String or String[0] if the file is empty
	 * @throws java.io.IOException
	 */
	public static String getFileAsString(String filename)
			throws java.io.IOException {
		return getFileAsString(new File(filename));
	}
	
    /**
	 * Read the file and return it as a String, using the specified encoding.
	 * @param filename the name of the file
	 * @param encoding the charset name, for example, "UTF-8"
	 * @return the content of the file as a String or String[0] if the file is empty
	 * @throws java.io.IOException
	 * /
	public static String getFileAsString(String filename, String encoding)
			throws java.io.IOException {
		File file = new File(filename);
		return getFileAsString(file, encoding);
	}
	*/

	/**
	 * Returns a file as a String. Assumes UTF-8.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static String getFileAsString(File file)
			throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.toString());
		}
		if (file.length() == 0) {
			return "";
		}
		Scanner scanner = new Scanner(file, "UTF-8");
		String out = scanner.useDelimiter("\\A").next();
		scanner.close();
		return out;
	}
	
	
	
	/**
	 * Write a String to disk using the specified filename and the default encoding.
	 * @param filename name of the file to write
	 * @param contents contents of the file
	 * @throws IOException 
	 */
	public static void writeFileAsString(String filename, String contents) throws IOException {
		writeFileAsString(filename, contents, null);
	}

	/**
	 * Write a String to disk using the specified filename and optional encoding.
	 * @param filename name of the file to write
	 * @param contents contents of the file
	 * @param encoding the charset, for example, "UTF-8"
	 * @throws IOException 
	 */
	public static void writeFileAsString(String filename, String contents,
			String encoding) throws IOException {

		File file = new File(filename);
		writeFileAsString(file, contents, encoding);
	}


	public static void writeFileAsString(File file, String contents, String encoding) throws IOException {
		file.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter writer;
		if (encoding == null) {
			writer = new OutputStreamWriter(fos);
		} else {
			writer = new OutputStreamWriter(fos, encoding);
		}
		writer.write(contents);
		writer.close();
		fos.close();
	}
	
	public static void writeFileAsString(File file, String contents) throws IOException {
		writeFileAsString(file, contents, null);
	}
	
	

	/**
	 * Write the contents of an InputStream out to a file on disk.
	 * @param in the InputStream with the data
	 * @param file the output file
	 * @throws IOException
	 */
	public static void writeStreamToFile(InputStream in, File file) throws IOException {
		Files.copy(in, file.toPath());
	}

	/**
	 * Replaces characters that are not allowed in filenames with underscores. The 
	 * specific characters that are disallowed are operating system-dependent, 
	 * but this handles the ones that are commonly a problem in Windows and Linux.
	 * <p>
	 * Replaces ? % * : | " < >
	 * </p>
	 * Does not touch slashes, forward or back; java.util.File is usually smart
	 * enough to handle them in a system-specific way. 
	 * @param filename the filename to fix
	 * @return a filename with the offending characters replaced
	 */
	public static String fixFilename(String filename) {
		
		StringBuilder buf = new StringBuilder(filename.length());
		for (int i = 0; i < filename.length(); i++) {
			char ch = filename.charAt(i);
			switch (ch) {
			case '/':
			case '?':
			case '%':
			case '*':
			case ':':
			case '|':
			case '"':
			case '<':
			case '>':
				buf.append('_');
				break;
			default:
				buf.append(ch);
				break;
			}
		}
		return buf.toString();
	}
	

	/**
	 * Return the size in bytes of a file or directory.
	 */
	public static long directorySize(File directory) {
	    long length = 0;
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += directorySize(file);
	    }
	    return length;
	}

	
	public static String getTempDir() throws IOException {
		return getTempDir("./temp");
	}

	public static String getTempDir(String parentDir) throws IOException {
		String dirName = ZonedDateTime.now().toLocalDateTime().toString().replace(':', '-');
		//String dirName = Instant.now().toString().replace(':', '-');
		File file = new File(parentDir + "/" + dirName); // Math.abs(random.nextLong()));
		file.mkdirs();
		return file.toString();
	}
}
