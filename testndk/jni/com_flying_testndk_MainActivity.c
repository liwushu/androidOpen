#include<jni.h>
#include<stdio.h>
#include<android/log.h>


#define  LOG_TAG    "testjni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define ONE_M 1024*1024

JNIEXPORT jstring JNICALL Java_com_flying_testndk_MainActivity_getString
  (JNIEnv *env, jclass clz){
        return (*env)->NewStringUTF(env,"hello form jni");
}

/*
 * Class:     com_flying_testndk_bean_TestJni
 * Method:    invokeClick
 * Signature: ()V
 */
JNIEXPORT jobject JNICALL Java_com_flying_testndk_MainActivity_invokeClick
  (JNIEnv *env, jobject clz,jobject jobj){
  invokeAaa(env,clz,jobj);
  char *p = (char *)malloc(ONE_M);
  memset(p,1,ONE_M);
  return clz;
}

void invokeAaa(JNIEnv *env, jobject clz,jobject jobj) {
    LOGI("JNI work !");
      jclass clazz11 = (*env)->GetObjectClass(env,jobj); //通过类的对象
      LOGI("clazz2222222: %d\n",clazz11);
      //jmethodID  methodId = (*env)->GetMethodID(env,clazz11,"invokeTest","()V");
      jmethodID methodId = (*env)->GetMethodID(env,clazz11,"invokeTest","()V");
      LOGI("methodId22222: %d\n ",methodId);
      if(methodId == NULL){
              __android_log_print(ANDROID_LOG_INFO,"HGY", "method show ID not found");
              return; //如果方法ID没有找到
       }
       //jmethodID constructor = (*env)->GetMethodID(env, clz, "<init>", "()V");
      //(*env)->CallStaticVoidMethod(env,clz,methodId);
       //jobject obj = (*env)->NewObject(env, clz, constructor);
       //LOGI("obj: %d\n",obj);
      (*env)->CallVoidMethod(env,jobj,methodId);
}