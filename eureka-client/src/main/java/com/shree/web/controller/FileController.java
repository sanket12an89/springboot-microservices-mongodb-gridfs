package com.shree.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.shree.web.constants.HTTPConstants;
import com.shree.web.helper.pojo.GenericResponse;
import com.shree.web.response.pojo.FileResponsePojo;
import com.shree.web.service.impl.FileStorageServiceImpl;

@RestController
@RequestMapping("api/v1/")
public class FileController {

	@Autowired
	private FileStorageServiceImpl fileStorageServiceObj;

	@PostMapping("/uploadFile")
	public GenericResponse<Object> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("location") String location) {
		GenericResponse<Object> responseObj = new GenericResponse<Object>();
		Map<String, String> customMessageMapObj = new HashMap<String, String>();

		try {
			Map<String, String> map = new HashMap<>();
			map = fileStorageServiceObj.storeFile(file, location);

			FileResponsePojo fileResponseObj = processFileResponse(file, map, location);

			responseObj.setStatus(HTTPConstants.HTTP_STATUS_MSG_SUUCESS);
			responseObj.setResponsecode(HTTPConstants.HTTP_STATUS_200);
			responseObj.setResponse(fileResponseObj);

		} catch (Exception e) {
			customMessageMapObj.put("Error Code", e.getMessage());
			responseObj.setCustommessages(customMessageMapObj);
			responseObj.setStatus(HTTPConstants.HTTP_STATUS_MSG_ERROR);
			responseObj.setResponsecode(HTTPConstants.HTTP_STATUS_400);
		}
		return responseObj;
	}

	@GetMapping("/downloadFile/{fileId}/fileName/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, @PathVariable String fileName,
			HttpServletRequest request) throws IOException {
		// Load file as Resource
		Resource resource = fileStorageServiceObj.loadFileAsResource(fileId, fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			// logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	private FileResponsePojo processFileResponse(MultipartFile file, Map<String, String> map, String location) {
		Map.Entry<String, String> entry = map.entrySet().iterator().next();
		String fileId = entry.getKey();
		String fileName = entry.getValue();
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("api/v1/downloadFile/" + fileId + "/fileName/").path(fileName).toUriString();

		FileResponsePojo fileResponseObj = new FileResponsePojo(fileId, fileName, fileDownloadUri,
				file.getContentType(), file.getSize(), location);
		return fileResponseObj;
	}
}
