package com.ge.azure.azureconnect.controller;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ge.azure.azureconnect.service.AzureConnectService;

@RestController
@RequestMapping("azureConnect")

public class AzureConnectController {

	@Autowired
	private AzureConnectService azureConnectService;

	@PostMapping
	@RequestMapping("/deleteAllFiles")
	public int deleteAllFiles(@RequestParam("folderName") String filePath) {
		return azureConnectService.deleteAllFiles(filePath);
	}

	@PostMapping
	@RequestMapping("/validatePath")
	public int validatePath(@RequestParam("folderName") String filePath) {
		return azureConnectService.validateFilePaths(filePath);
	}
	
	@PostMapping
	@RequestMapping("/createNewFile")
	public int createNewFile(@RequestParam("folderName") String filePath) {
		return azureConnectService.createNewFile(filePath);
	}
	
	@PostMapping
	@RequestMapping("/copyFiles")
	public int copyFiles(@RequestParam("sourcePath") String sourcePath,	@RequestParam("destinationPath") String destinationPath, @RequestParam("fileName") String fileName) {
		return azureConnectService.copyFiles(sourcePath, destinationPath, fileName);
	}
	
	@PostMapping
	@RequestMapping("/createDirectories")
	public int createDirectories(@RequestParam("folderName") String filePath) {
		return azureConnectService.createDirectories(filePath);
	}
	
	@PostMapping
	@RequestMapping("/getDirectoryFileContents")
	public List<String> getDirectoryFileContents(@RequestParam("folderName") String filePath) {
		return azureConnectService.getDirectoryFileContents(filePath);
	}
	
	@GetMapping
	@RequestMapping("/testTempFile")
	public File testTempFile() {
		return azureConnectService.createTempNewFile();
	}
	
}
