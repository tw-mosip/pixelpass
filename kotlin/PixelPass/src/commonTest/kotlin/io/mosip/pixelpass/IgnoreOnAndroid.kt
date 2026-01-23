package io.mosip.pixelpass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
expect annotation class IgnoreOnAndroid()
