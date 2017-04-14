package org.craftsmenlabs.gareth.validator

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Assume(val glueLine: String,
                        val humanReadable: String = "",
                        val description: String = "")

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Baseline(val glueLine: String,
                          val humanReadable: String = "",
                          val description: String = "")

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Failure(val glueLine: String,
                         val humanReadable: String = "",
                         val description: String = "")

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Success(val glueLine: String,
                         val humanReadable: String = "",
                         val description: String = "")

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Time(val glueLine: String,
                      val humanReadable: String = "",
                      val description: String = "")