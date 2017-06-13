//
// Created by liwu shu on 2017/6/13.
//
#include "com_flying_testndk2_jni_TestJni.h"
#include "android/log.h"

/*
 * Class:     com_flying_testndk2_jni_TestJni
 * Method:    getJniString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_flying_testndk2_jni_TestJni_getJniString
  (JNIEnv *env, jobject obj){

        return (*env)->NewStringUTF(env,"hello from jni");
  }

/*
 * Class:     com_flying_testndk2_jni_TestJni
 * Method:    testClassJava
 * Signature: ()Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_flying_testndk2_jni_TestJni_testClassJava
  (JNIEnv *env, jobject obj){
        return obj;
  }
