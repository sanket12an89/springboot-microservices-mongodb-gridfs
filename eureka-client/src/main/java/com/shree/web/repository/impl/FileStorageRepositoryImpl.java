package com.shree.web.repository.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.shree.web.repository.FileStorageRepository;

@Repository
public class FileStorageRepositoryImpl implements FileStorageRepository {

	@Autowired
	GridFsOperations gridOperations;

	@Autowired
	GridFSBucket gridFSBucket;

	@SuppressWarnings("static-access")
	public String saveFileGridFs(Path targetLocation, String orignalFileName,String location) throws IOException {
		// Define metaData
		DBObject metaData = new BasicDBObject();
		metaData.put("location", location);

		/**
		 * 1. save an image file to MongoDB
		 */

		// Get input file
		InputStream iamgeStream = new FileInputStream(targetLocation.toAbsolutePath().toString());
		String mimeType = Files.probeContentType(targetLocation);
		System.out.println(mimeType);
		metaData.put("type", "image");
		metaData.put("location", "Raigad");

		// Store file to MongoDB
		String imageFileId = gridOperations.store(iamgeStream, orignalFileName, mimeType, metaData).toStringMongod();
		System.out.println("ImageFileId = " + imageFileId);

		return imageFileId;
	}

	@Override
	public String retrieveImageFile(String targetLocation, String imageFileId) throws IOException {
		// read file from MongoDB
		GridFSFile imageFile = gridOperations
				.findOne(new Query(Criteria.where("_id").is(new ObjectId(imageFileId))));
		String uniqueFileName=null;
		try {
			String extension = FilenameUtils.getExtension(imageFile.getFilename());

			 uniqueFileName = imageFileId +"." +extension;

			File file = new File(targetLocation+"\\"+ uniqueFileName);

			FileOutputStream streamToDownloadTo = new FileOutputStream(file);

			gridFSBucket.downloadToStream(imageFile.getId(), streamToDownloadTo);

			streamToDownloadTo.close();
		} catch (IOException e) {
			// handle exception
			System.out.println("error: " + e.getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return uniqueFileName;
	}

}
