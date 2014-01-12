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

import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.security.AWSCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class OrientDbS3BackupDeleteOldDataTest {

	@Before
	public void SetUp() throws Exception {
	}

	@Test
	public void deleteOldataTest() throws Exception {
		String bucketName = "orientdb_backup";
		AWSCredentials awsCredentials;
		try {
			awsCredentials = Credentials.loadAWSCredentials();
			// To communicate with S3 use the RestS3Service.
			RestS3Service s3Service = new RestS3Service(awsCredentials);
			S3Utils.deleteOldData(s3Service, bucketName, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@After
	public void cleanUp() {

	}
}
