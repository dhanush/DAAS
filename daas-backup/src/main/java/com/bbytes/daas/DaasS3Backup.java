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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.orientechnologies.common.io.OIOUtils;
import com.orientechnologies.common.log.OLogManager;
import com.orientechnologies.common.parser.OSystemVariableResolver;
import com.orientechnologies.common.parser.OVariableParser;
import com.orientechnologies.common.parser.OVariableParserListener;
import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.command.OCommandOutputListener;
import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.db.tool.ODatabaseExport;
import com.orientechnologies.orient.core.exception.OConfigurationException;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.handler.OServerHandlerAbstract;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasS3Backup extends OServerHandlerAbstract {
	public enum VARIABLES {
		DBNAME, DATE
	}

	private Date firstTime = null;
	private long delay = -1;
	private String targetDirectory = "backup-s3";
	private String targetFileName;
	private Set<String> includeDatabases = new HashSet<String>();
	private Set<String> excludeDatabases = new HashSet<String>();
	private OServer serverInstance;

	private String bucketName = "endure_backup";

	@Override
	public void config(final OServer iServer, final OServerParameterConfiguration[] iParams) {
		serverInstance = iServer;

		for (OServerParameterConfiguration param : iParams) {
			if (param.name.equalsIgnoreCase("enabled")) {
				if (!Boolean.parseBoolean(param.value))
					// DISABLE IT
					return;
			} else if (param.name.equalsIgnoreCase("delay"))
				delay = OIOUtils.getTimeAsMillisecs(param.value);
			else if (param.name.equalsIgnoreCase("firsttime")) {
				try {
					firstTime = OIOUtils.getTodayWithTime(param.value);
				} catch (ParseException e) {
					throw new OConfigurationException("Parameter 'firstTime' has invalid format, expected: HH:mm:ss", e);
				}
			} else if (param.name.equalsIgnoreCase("target.directory"))
				targetDirectory = param.value;
			else if (param.name.equalsIgnoreCase("db.include") && param.value.trim().length() > 0)
				for (String db : param.value.split(","))
					includeDatabases.add(db);
			else if (param.name.equalsIgnoreCase("db.exclude") && param.value.trim().length() > 0)
				for (String db : param.value.split(","))
					excludeDatabases.add(db);
			else if (param.name.equalsIgnoreCase("target.fileName"))
				targetFileName = param.value;
		}

		if (delay <= 0)
			throw new OConfigurationException("Cannot find mandatory parameter 'delay'");
		if (!targetDirectory.endsWith("/"))
			targetDirectory += "/";

		// add a folder with date to the target directory

		final String todayDate = getTodaysDateAsString();
		targetDirectory = targetDirectory + "/" + todayDate + "/";

		final File filePath = new File(targetDirectory);
		if (filePath.exists()) {
			if (!filePath.isDirectory())
				throw new OConfigurationException("Parameter 'path' points to a file, not a directory");
		} else
			// CREATE BACKUP FOLDER(S) IF ANY
			filePath.mkdirs();

		OLogManager.instance().info(this,
				"Automatic backup plugin installed and active: delay=%dms, firstTime=%s, targetDirectory=%s", delay,
				firstTime, targetDirectory);

		final TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				OLogManager.instance().info(this, "[OAutomaticBackup] Scanning databases to backup...");

				int ok = 0, errors = 0;

				final Map<String, String> databaseNames = serverInstance.getAvailableStorageNames();
				for (final java.util.Map.Entry<String, String> dbName : databaseNames.entrySet()) {
					boolean include;

					if (includeDatabases.size() > 0)
						include = includeDatabases.contains(dbName.getKey());
					else
						include = true;

					if (excludeDatabases.contains(dbName.getKey()))
						include = false;

					if (include) {
						final String fileName = (String) OVariableParser.resolveVariables(targetFileName,
								OSystemVariableResolver.VAR_BEGIN, OSystemVariableResolver.VAR_END,
								new OVariableParserListener() {
									public String resolve(final String iVariable) {
										if (iVariable.equalsIgnoreCase(VARIABLES.DBNAME.toString()))
											return dbName.getKey();
										else if (iVariable.startsWith(VARIABLES.DATE.toString())) {
											return new SimpleDateFormat(iVariable.substring(VARIABLES.DATE.toString()
													.length() + 1)).format(new Date());
										}

										// NOT FOUND
										throw new IllegalArgumentException("Variable '" + iVariable + "' wasn't found");
									}
								});

						final String exportFilePath = targetDirectory + fileName;
						ODatabaseDocumentTx db = null;
						ODatabaseExport databaseExport = null;
						try {

							db = new ODatabaseDocumentTx(dbName.getValue());

							db.setProperty(ODatabase.OPTIONS.SECURITY.toString(), Boolean.FALSE);
							db.open("admin", "aaa");

							final long begin = System.currentTimeMillis();

							try {
								databaseExport = new ODatabaseExport(db, exportFilePath, new OCommandOutputListener() {
									public void onMessage(final String iText) {
									}
								});

								databaseExport.exportDatabase();

							} catch (Exception e) {
								OLogManager.instance().error(
										this,
										"[OAutomaticBackup] - Error on exporting database '" + dbName.getValue()
												+ "' to file: " + exportFilePath, e);
							} finally {
								if (databaseExport != null)
									databaseExport.close();
							}

							OLogManager.instance().info(
									this,
									"[OAutomaticBackup] - Backup of database '" + dbName.getValue() + "' completed in "
											+ (System.currentTimeMillis() - begin) + "ms");
							ok++;

						} catch (Exception e) {

							OLogManager.instance().error(
									this,
									"[OAutomaticBackup] - Error on exporting database '" + dbName.getValue()
											+ "' to file: " + exportFilePath, e);
							errors++;

						} finally {
							if (db != null)
								db.close();
						}
					}
				}
				OLogManager.instance().info(this, "[OAutomaticBackup] Backup finished: %d ok, %d errors", ok, errors);

				try {
					AWSCredentials awsCredentials = Credentials.loadAWSCredentials();

					// To communicate with S3 use the RestS3Service.
					RestS3Service s3Service = new RestS3Service(awsCredentials);

					File targetDir = new File(targetDirectory);

					uploadFolder(s3Service, targetDir, bucketName + "-" + todayDate);

					deleteDirectory(targetDir);

				} catch (Exception e) {
					OLogManager.instance().error(this, "[OAutomaticBackup-S3] - Error on uploading to S3  '" + e);
					e.printStackTrace();
				}
			}
		};

		if (firstTime == null)
			Orient.instance().getTimer().schedule(timerTask, delay, delay);
		else
			Orient.instance().getTimer().schedule(timerTask, firstTime, delay);
	}

	public String getName() {
		return "automaticBackup";
	}

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
		}
		return (directory.delete());
	}

	public S3Object[][] listAllfiles(S3Service s3Service) throws S3ServiceException {
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

	private void uploadFolder(S3Service s3Service, File folder, String bucketName) throws NoSuchAlgorithmException,
			IOException, S3ServiceException {
		uploadFolderContents(s3Service, folder, bucketName);
	}

	private void uploadFolderContents(S3Service s3Service, File folder, String bucketName) throws S3ServiceException,
			NoSuchAlgorithmException, IOException {
		for (File child : folder.listFiles()) {
			uploadData(s3Service, child, bucketName);
		}
	}

	public void uploadData(S3Service s3Service, File fileData, String bucketName) throws NoSuchAlgorithmException,
			IOException, S3ServiceException {
		S3Object fileObject = new S3Object(fileData);
		S3Bucket bucket = s3Service.getOrCreateBucket(bucketName);
		s3Service.putObject(bucketName, fileObject);
	}

	public void deleteOldData(S3Service s3Service, S3Object s3Object, int daysOlder) throws ServiceException {
		int days = Days.daysBetween(new DateTime(s3Object.getLastModifiedDate()), new DateTime(new Date())).getDays();

		if (days >= daysOlder) {
			// Delete all the objects in the bucket
			s3Service.deleteObject(s3Object.getBucketName(), s3Object.getKey());
		}
	}

	public String getTodaysDateAsString() {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// Get the date today using Calendar object.
		Date today = Calendar.getInstance().getTime();
		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String todaysDate = df.format(today);

		return todaysDate;
	}
}
