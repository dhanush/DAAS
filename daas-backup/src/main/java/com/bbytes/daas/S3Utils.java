/*
 * Copyright (C) 2013 The Daas Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bbytes.daas;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class S3Utils {

	public static S3Object[][] listAllfiles(S3Service s3Service) throws S3ServiceException {
		// List all your buckets.
		S3Bucket[] buckets = s3Service.listAllBuckets();
		S3Object[][] objects = new S3Object[buckets.length][];
		int i = 0;
		// List the object contents of each bucket.
		for (int b = 0; b < buckets.length; b++) {
			System.out.println("Bucket '" + buckets[b].getName() + "' contains:");
			// List the objects in this bucket.
			objects[i] = s3Service.listObjects(buckets[b].getName());
		}
		return objects;

	}

	public static void uploadFolder(S3Service s3Service, File folder, String bucketName)
			throws NoSuchAlgorithmException, IOException, S3ServiceException {
		uploadFolderContents(s3Service, folder, bucketName);
	}

	public static void uploadFolderContents(S3Service s3Service, File folder, String bucketName)
			throws S3ServiceException, NoSuchAlgorithmException, IOException {
		for (File child : folder.listFiles()) {
			uploadData(s3Service, child, bucketName);
		}
	}

	public static void uploadData(S3Service s3Service, File fileData, String bucketName)
			throws NoSuchAlgorithmException, IOException, S3ServiceException {
		S3Object fileObject = new S3Object(fileData);
		// create bucket if not available 
		s3Service.getOrCreateBucket(bucketName);
		s3Service.putObject(bucketName, fileObject);
	}

	public static void deleteOldData(S3Service s3Service, String s3Bucket, int daysOlder) throws ServiceException {

		S3Object[] s3Objects = s3Service.listObjects(s3Bucket);
		for (int i = 0; i < s3Objects.length; i++) {
			deleteOldData(s3Service, s3Objects[i], daysOlder);
		}
	}

	public static void deleteOldData(S3Service s3Service, S3Object s3Object, int daysOlder) throws ServiceException {
		int days = Days.daysBetween(new DateTime(s3Object.getLastModifiedDate()), new DateTime(new Date())).getDays();

		if (days >= daysOlder) {
			// Delete all the objects in the bucket
			s3Service.deleteObject(s3Object.getBucketName(), s3Object.getKey());
		}
	}

}
