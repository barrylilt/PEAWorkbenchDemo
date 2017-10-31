package com.saama.workbench.bean;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadVersion {

	private CommonsMultipartFile uploadVersion;
	private String importVersionId;
	private String fileNameFormat;
	private String sourceType;
	private String fileName;
	private boolean checkOverwrite;

	
	
	public String getFileNameFormat() {
		return fileNameFormat;
	}

	public void setFileNameFormat(String fileNameFormat) {
		this.fileNameFormat = fileNameFormat;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public CommonsMultipartFile getUploadVersion() {
		return uploadVersion;
	}

	public void setUploadVersion(CommonsMultipartFile uploadVersion) {
		this.uploadVersion = uploadVersion;
	}

	public String getImportVersionId() {
		return importVersionId;
	}

	public void setImportVersionId(String importVersionId) {
		this.importVersionId = importVersionId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isCheckOverwrite() {
		return checkOverwrite;
	}

	public void setCheckOverwrite(boolean checkOverwrite) {
		this.checkOverwrite = checkOverwrite;
	}

	

}
