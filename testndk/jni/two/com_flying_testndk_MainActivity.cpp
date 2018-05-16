#include<jni.h>
#include<stdio.h>
#include<android/log.h>
#include <malloc.h>
#include "elfhook/elfhook.h"
#include "elfhook/Nodes.h"
#include "string.h"


#define  LOG_TAG    "testjni"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define ONE_M 1024*1024

void (*__free_hook_old) (void *__ptr) = NULL;
void *(*__malloc_hook_old)(size_t __size) = NULL;
void *(*__realloc_hook_old)(void *__ptr, size_t __size) = NULL;
void *(*__memalign_hook_old)(size_t __alignment, size_t __size) = NULL;
void (*__after_morecore_hook_old) (void) = NULL;
static size_t s_size=0;
static size_t free_total_size = 0;
static int malloc_count = 0;
static int free_count = 0;

extern "C" {
JNIEXPORT void JNICALL Java_com_flying_testndk_MainActivity_hook(JNIEnv *env, jobject clz);
JNIEXPORT jobject JNICALL Java_com_flying_testndk_MainActivity_invokeClick(JNIEnv *env, jobject clz,jobject jobj);
JNIEXPORT jstring JNICALL Java_com_flying_testndk_MainActivity_getString(JNIEnv *env, jobject clz);
JNIEXPORT void JNICALL Java_com_flying_testndk_MainActivity_free(JNIEnv *env, jobject clz);
void addNode(void *p,size_t __size);
void removeNode(void *p);
void print();
}

struct MallocNodeList mallocNodeList = {{NULL,0},NULL};
JNIEnv *saveEnv;
jobject saveObj;
char *p;


void getTrace() {
    if(saveEnv != NULL) {
        jclass ClassCJM =saveEnv->FindClass("com/flying/testndk/MainActivity");

        jmethodID MethodDisplayMessage = saveEnv->GetStaticMethodID(ClassCJM, "printStack",
                                                                    "()V");
        LOGI("ClassCJM:%d,%d\n",ClassCJM,MethodDisplayMessage);
        saveEnv->CallStaticVoidMethod(ClassCJM, MethodDisplayMessage,NULL);
    }
}


void* __malloc_hook(size_t __size)
{
    s_size += __size;
    void *p = __malloc_hook_old(__size);
    addNode(p,__size);

    LOGI("============__size:%d,total_size:%d,%ld,%d\n",__size,s_size,p,malloc_count);
    return p;
}

void __free_hook(void *__ptr) {
    free_count++;
   // getTrace();
    LOGI("============__free_hook: %ld,%d\n",__ptr,free_count);
    removeNode(__ptr);
    LOGI("============__free_hook: %d\n",free_total_size);
    __free_hook_old(__ptr);
}

void invokeAaa(JNIEnv *env, jobject clz,jobject jobj) {
    LOGI("JNI work !");
    jclass clazz11 = env->GetObjectClass(jobj); //通过类的对象
    jmethodID methodId = (env)->GetMethodID(clazz11,"invokeTest","()V");
    LOGI("methodId22222: %d\n ",methodId);
    if(methodId == NULL){
        __android_log_print(ANDROID_LOG_INFO,"HGY", "method show ID not found");
        return; //如果方法ID没有找到
    }
    (env)->CallVoidMethod(jobj,methodId);
}

JNIEXPORT jstring JNICALL Java_com_flying_testndk_MainActivity_getString
  (JNIEnv *env, jobject clz){
        return (env)->NewStringUTF("hello form jni");
}

/*
 * Class:     com_flying_testndk_bean_TestJni
 * Method:    invokeClick
 * Signature: ()V
 */
JNIEXPORT jobject JNICALL Java_com_flying_testndk_MainActivity_invokeClick
  (JNIEnv *env, jobject clz,jobject jobj){
  invokeAaa(env,clz,jobj);
  p = (char *)malloc(ONE_M);
  memset(p,1,ONE_M);
  return clz;
}

JNIEXPORT void JNICALL Java_com_flying_testndk_MainActivity_hook
        (JNIEnv *env, jobject clz){
    saveEnv = env;
    saveObj = clz;
    elfHook("libHello-ndk.so","malloc",(void *)__malloc_hook,(void **)&__malloc_hook_old);
    elfHook("libHello-ndk.so", "free",(void *)__free_hook,(void **)&__free_hook_old);
}

JNIEXPORT void JNICALL Java_com_flying_testndk_MainActivity_free
        (JNIEnv *env, jobject clz){
    if(p != NULL) {
        free(p);
        p = NULL;
    }
}

void addNode(void *p,size_t __size) {
    if(saveEnv != NULL) {
        jclass ClassCJM =saveEnv->FindClass("com/flying/testndk/MainActivity");

        jmethodID mallocCallback = saveEnv->GetStaticMethodID(ClassCJM, "mallocCallback",
                                                                    "(JI)V");
        jlong addr = reinterpret_cast<jlong>(p);
        LOGI("=== ClassCJM:%d,%d,%ld,%d\n",ClassCJM,mallocCallback,addr,__size);
        saveEnv->CallStaticVoidMethod(ClassCJM, mallocCallback,addr,__size);
    }
}

void removeNode(void *pointer) {
    if(saveEnv != NULL) {
        jclass ClassCJM =saveEnv->FindClass("com/flying/testndk/MainActivity");

        jmethodID freeCallback = saveEnv->GetStaticMethodID(ClassCJM, "freeCallback",
                                                        "(J)V");

        jlong addr = reinterpret_cast<jlong>(pointer);
        LOGI("===== ClassCJM:%d,%d,%ld \n",ClassCJM,freeCallback,addr);
        saveEnv->CallStaticVoidMethod(ClassCJM, freeCallback,addr);
    }
}


void print() {
    MallocNodeList *p = &mallocNodeList;
    while(p != NULL) {
        LOGI("==++++++ addr: %ld,%d\n",p->node.addr,p->node.size);
        p = p->next;
    }
}
