package com.shree.web.repository;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageRepository {
	public String saveFileGridFs(Path targetLocation,String orignalFilename,String location) throws IOException;

	public String retrieveImageFile(String filePath,String imageFileId) throws IOException;

}
