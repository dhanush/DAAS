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
package com.bbytes.daas.client;

import java.util.concurrent.Callable;

/**
 * 
 * 
 * @author Thanneer
 * 
 * @version
 */
public class DaasClientCallAsyncTask<T> implements Runnable {

	private final Callable<T> task;

	private final AsyncResultHandler<T> callback;

	public DaasClientCallAsyncTask(Callable<T> task, AsyncResultHandler<T> callback) {
		this.task = task;
		this.callback = callback;
	}

	public void run() {
		T t = null;
		try {
			t = (T) task.call();
		} catch (Exception e) {
			callback.onError(e);
		}
		callback.onComplete(t);
	}

}