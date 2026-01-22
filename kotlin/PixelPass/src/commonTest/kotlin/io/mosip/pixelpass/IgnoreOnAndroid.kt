package io.mosip.pixelpass

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
expect annotation class IgnoreOnAndroid()
