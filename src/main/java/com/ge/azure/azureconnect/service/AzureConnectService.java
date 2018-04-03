package com.ge.azure.azureconnect.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;

@Service
public class AzureConnectService {

	@Autowired
	private Environment env;

	private static final int SUCCESS = 0;
	private static final int FAILURE = 1;
	private static final int ERROR_IN_CONNECT = 2;

	public CloudFileShare getFileShare() {

		CloudFileClient fileClient = null;
		CloudStorageAccount storageAccount;
		CloudFileShare share = null;

		try {
			String storageConnectionString = "DefaultEndpointsProtocol=" + env.getProperty("azure_endpoint_protocol")
					+ ";" + "AccountName=" + env.getProperty("azure_account_name") + ";" + "AccountKey="
					+ env.getProperty("azure_account_key");

			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			fileClient = storageAccount.createCloudFileClient();
			share = fileClient.getShareReference(env.getProperty("azure_fileshare"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return share;
	}

	public int deleteAllFiles(String filePath) {

		try {
			
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			CloudFileDirectory sourceDir = rootDir.getDirectoryReference(filePath);
			
			for (ListFileItem item : sourceDir.listFilesAndDirectories()) {

				boolean isDirectory = item.getClass() == CloudFileDirectory.class;
				if (isDirectory) {

					CloudFileDirectory dir = (CloudFileDirectory) item;
					continue;
				}

				else {
					CloudFile file = (CloudFile) item;
					file.delete();
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR_IN_CONNECT;
	}

	public int validateFilePaths(String filePath) {

		try {
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			CloudFileDirectory sourceDir = rootDir.getDirectoryReference(filePath);
			if(sourceDir.exists()) {
				return SUCCESS;
			}else {
				return FAILURE;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR_IN_CONNECT;

	}
	
	public int createNewFile(String filePath) {

		try {
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			CloudFileDirectory sourceDir = rootDir.getDirectoryReference(filePath);
			File file = new File("end.txt");
			CloudFile cloudFile = sourceDir.getFileReference(file.getName());
			cloudFile.uploadText(" ");
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR_IN_CONNECT;

	}
	
	public int copyFiles(String sourcePath, String destinationPath, String fileName) {

		try {
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			CloudFileDirectory sourceDir = rootDir.getDirectoryReference(sourcePath);
			CloudFileDirectory destinationDir = rootDir.getDirectoryReference(destinationPath);
			CloudFile sourceFile = sourceDir.getFileReference(fileName);
			CloudFile destinationFile = destinationDir.getFileReference(fileName);
			destinationFile.startCopy(sourceFile);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR_IN_CONNECT;

	}
	
	public int createDirectories(String sourcePath) {
		try {
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			String[] inspectionFolders = sourcePath.split("/");
			CloudFileDirectory inspectionRootRef = rootDir;
			CloudFileDirectory sampleDir = null;
			for (String directory : inspectionFolders) {
				sampleDir = inspectionRootRef.getDirectoryReference(directory);
				if (sampleDir.createIfNotExists()) {
					inspectionRootRef = inspectionRootRef.getDirectoryReference(directory);
				} else {
					inspectionRootRef = sampleDir;
				}

			}
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ERROR_IN_CONNECT;

	}

	public List<String> getDirectoryFileContents(String filePath) {
		ArrayList<String> fileNames = null;
		try {
			CloudFileDirectory rootDir = getFileShare().getRootDirectoryReference();
			CloudFileDirectory sourceDir = rootDir.getDirectoryReference(filePath);
			Iterable<ListFileItem> results = sourceDir.listFilesAndDirectories();
			fileNames = new ArrayList<String>();
			for (Iterator<ListFileItem> itr = results.iterator(); itr.hasNext();) {
				ListFileItem item = itr.next();
				CloudFile cf = (CloudFile) item;
				fileNames.add(cf.getName());
			}
			System.out.println("Get all file names::::" + fileNames.size());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fileNames;
	}
	
	
	public File createTempNewFile() {
		File tmpFile = null;
		try {
			String toWrite = "Hello";
		    tmpFile = File.createTempFile("test",".txt" , new File(System.getProperty("java.io.tmpdir")));
		    FileWriter writer = new FileWriter(tmpFile);
		    writer.write(toWrite);
		    writer.close();
		 
		    BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
		    reader.close();
		    
			System.out.println("TEMP FILE PATH" + tmpFile.getPath());
			System.out.println("TEMP getAbsolutePath PATH" + tmpFile.getAbsolutePath());
			return tmpFile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tmpFile;

	}
	
}
