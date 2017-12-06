package com.davinryan.common.restservice.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this tag to indicate you want to log using the Mapped Diagnostic Context or MDC. You'll also need to
 * add a bean instance of {@link LogServiceCallWithMDCAspect} to yoru spring application context to make this work.
 *
 * WARNING: this annotation only works with public methods who have only a single parameter of type
 * {@link com.davinryan.common.restservice.domain.request.Request}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogServiceCallWithMDC {
}
