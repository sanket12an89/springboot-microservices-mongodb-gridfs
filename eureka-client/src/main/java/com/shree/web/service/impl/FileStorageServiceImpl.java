package com.shree.web.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.shree.web.exception.FileStorageException;
import com.shree.web.exception.MyFileNotFoundException;
import com.shree.web.property.FileStorageProperties;
import com.shree.web.repository.FileStorageRepository;
import com.shree.web.service.FileStorageService;
import com.shree.web.util.CommonUtil;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	FileStorageRepository fileStoreRepositoryObj;

	@Autowired
	public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public Map<String,String> storeFile(MultipartFile file,String location) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		Map<String,String> returnMap=new HashMap<>();
		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			String extension = FilenameUtils.getExtension(file.getOriginalFilename());

			String uniqueFileName=CommonUtil.uniqueNumberGenerators()+extension;
			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
			String imageFileId = fileStoreRepositoryObj.saveFileGridFs(targetLocation,fileName,location);

			
			//Files.delete(targetLocation);
			
			loadFileAsResource(imageFileId,fileName);
			
			returnMap.put(imageFileId, fileName);
			
			return returnMap;
			
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	public Resource loadFileAsResource(String fileId,String imageFileId) throws IOException {
		String fileName=null;
		try {
			String targetLocation=this.fileStorageLocation.toAbsolutePath().toString();
			
			fileName=fileStoreRepositoryObj.retrieveImageFile(targetLocation,fileId);
			
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new MyFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new MyFileNotFoundException("File not found " + fileName, ex);
		}
	}
}