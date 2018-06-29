package com.icbc.efrs.app.exception;
/**
 * 与约定不符造成的报错
 *
 */
public class CodeException extends RuntimeException {
   public CodeException(String message) {
		super(message);
   }
}
