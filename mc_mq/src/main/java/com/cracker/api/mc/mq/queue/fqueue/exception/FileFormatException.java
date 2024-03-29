/*
 *  Copyright 2011 sunli [sunli1223@gmail.com][weibo.com@sunli1223]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.cracker.api.mc.mq.queue.fqueue.exception;
/**
 *@author sunli
 *@date 2011-5-18
 *@version $Id: FileFormatException.java 2 2011-07-31 12:25:36Z sunli1223@gmail.com $
 */
public class FileFormatException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6950322066714479555L;

	/**
	 * Constructs an {@code FileFormatException} with {@code null} as its error
	 * detail message.
	 */
	public FileFormatException() {
		super();
	}

	public FileFormatException(String message) {
		super(message);
	}

	public FileFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileFormatException(Throwable cause) {
		super(cause);
	}
}
