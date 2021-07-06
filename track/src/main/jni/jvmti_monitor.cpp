#include <stdint.h>

//
// Created by admin on 2021/6/2.
//
#include <jni.h>
#include "jvmti.h"
#include <android/log.h>
#include <string>
#include <string.h>

char *TAG = "jvmti";
long long currentTimeInMillis() {
    struct timeval _time;
    gettimeofday(&_time, NULL);
    long long factor = 1;
    long long now = factor * _time.tv_sec * 1000 + _time.tv_usec / 1000;
    return now;
}

bool checkJvmTiError(jvmtiEnv *jvmti, jvmtiError error)
{
    if (error != JVMTI_ERROR_NONE) {
        char *errorName;
        jvmti->GetErrorName(error, &errorName);
        __android_log_print(ANDROID_LOG_ERROR, TAG, "jvm ti error: %d ---> %s", error, (NULL == errorName) ? "" : errorName);
        return true;
    }
    return false;
}

char* createStackInfo(jvmtiEnv *jvmti_env,
                     JNIEnv* jni_env,
                     jthread thread,
                     int stackDepth)
{
    char *info = "";
    jvmtiFrameInfo frames[stackDepth];
    int count;
    jvmtiError error = jvmti_env->GetStackTrace(thread, 0, stackDepth, frames, &count);
    if (checkJvmTiError(jvmti_env, error) || count < 1) {
        return info;
    }
    char *methodName, *className, *sig, *gsig;
    jclass declaringClazz;
    for (int i = 0; i < count; ++i) {
        jvmtiFrameInfo frame = frames[i];
        error = jvmti_env->GetMethodName(frame.method, &methodName, &sig, &gsig);
        if (checkJvmTiError(jvmti_env, error)) {
            continue;
        }
        error = jvmti_env->GetMethodDeclaringClass(frames[i].method, &declaringClazz);
        if (checkJvmTiError(jvmti_env, error)) {
            continue;
        }
        error = jvmti_env->GetClassSignature(declaringClazz, &className, NULL);
        if (checkJvmTiError(jvmti_env, error)) {
            continue;
        }
        if (i == 0) {
            asprintf(&info, "%s(%ld): %s", className, frame.location, methodName);
        } else {
            asprintf(&info, "%s\n%s(%ld): %s", info, className, frame.location, methodName);
        }
    }
    asprintf(&info, "%s\n ", info);
    return info;
}

u_long tag = 0;
u_long objCount = 0;
jvmtiEnv *_jvmtiEnv;

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved)
{
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Agent_OnLoad");
}

JNIEXPORT jint JNICALL
Agent_OnAttach(JavaVM* vm, char* options, void* reserved)
{
    __android_log_print(ANDROID_LOG_ERROR, TAG, "Agent_OnAttach");
    // get jvmti env
    vm->GetEnv(reinterpret_cast<void **>(&_jvmtiEnv), JVMTI_VERSION_1_2);

    jvmtiCapabilities _jvmtiCapabilities;
    _jvmtiEnv->GetPotentialCapabilities(&_jvmtiCapabilities);
    _jvmtiEnv->AddCapabilities(&_jvmtiCapabilities);
    if (NULL == _jvmtiEnv) {
        return JNI_FALSE;
    }
    return JNI_OK;
}

JNICALL void objectAlloc
        (jvmtiEnv *jvmti_env,
         JNIEnv* jni_env,
         jthread thread,
         jobject object,
         jclass object_klass,
         jlong size)
{
    objCount++;
    char *classSignature;
    /**
     * Z -> boolean
     * J -> long
     */
    jvmti_env->GetClassSignature(object_klass, &classSignature, nullptr);
    size_t len = strlen(classSignature);
    if (len <= 2
        || strstr(classSignature, "Ljava/lang/Long") != NULL
        || strstr(classSignature, "Ljava/lang/String") != NULL
        || strstr(classSignature, "Ljava/lang/Float") != NULL
        || strstr(classSignature, "Ljava/lang/Char") != NULL
        || strstr(classSignature, "Ljava/lang/Double") != NULL
        || strstr(classSignature, "Ljava/lang/Short") != NULL
        || strstr(classSignature, "Ljava/lang/String") != NULL
        || strstr(classSignature, "Ljava/lang/Integer") != NULL
        || strstr(classSignature, "Ljava/lang/Class") != NULL
        || strstr(classSignature, "Ljava/util/ArrayList") != NULL
        || strstr(classSignature, "Ljava/util/HashMap") != NULL
        || strstr(classSignature, "Ljava/util/HashSet") != NULL
        || strstr(classSignature, "Ljava/util/Hashtable") != NULL
        || strstr(classSignature, "Ljava/util/LinkedHashMap") != NULL
        || strstr(classSignature, "Ljava/util/LinkedHashSet") != NULL
        || strstr(classSignature, "Ljava/util/LinkedList") != NULL
        || strstr(classSignature, "Lsun/misc/") != NULL
        || strstr(classSignature, "Llibcore/util/NativeAllocationRegistry") != NULL
        || strstr(classSignature, "Landroid/os/MessageMonitor") != NULL
        ) {
        jvmti_env->Deallocate(reinterpret_cast<unsigned char *>(classSignature));
        return;
    }
    // time
    long long millis = currentTimeInMillis();
    // set object tag
    jvmti_env->SetTag(object, tag);

    jvmtiThreadInfo threadInfo;
    jvmti_env->GetThreadInfo(thread, &threadInfo);

//    char* stackInfo = createStackInfo(jvmti_env, jni_env, thread, 10);
    char* stackInfo = "null";
    char *allocInfo;
    asprintf(&allocInfo, "time = %lld, obj count: %lld, tag = %lld, class = %s, thread = %s, size = %d\n stack: \n %s \n"
            , millis
            , objCount
            , tag
            , classSignature
            , threadInfo.name
            , size
            , stackInfo);
    jvmti_env->Deallocate(reinterpret_cast<unsigned char *>(classSignature));

    free(allocInfo);
    __android_log_print(ANDROID_LOG_WARN, TAG, "objectAlloc: %s", allocInfo);
    tag += 1;

}

JNICALL void objectFree
        (jvmtiEnv *jvmti_env,
         jlong tag)
{
    objCount--;
    __android_log_print(ANDROID_LOG_ERROR, TAG, "objectFree: obj count: %lld, tag = %lld", objCount, tag);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_xsw_track_jvmti_impl_JVMTIImpl_nativeAttach(JNIEnv *env, jobject thiz) {
    __android_log_print(ANDROID_LOG_ERROR, TAG, "nativeAttach");
    if (NULL == _jvmtiEnv) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "jvmti env is null!");
        return JNI_FALSE;
    }
    jvmtiEventCallbacks eventCallbacks;
    eventCallbacks.VMObjectAlloc = &objectAlloc;
    eventCallbacks.ObjectFree = &objectFree;
    _jvmtiEnv->SetEventCallbacks(&eventCallbacks, sizeof(eventCallbacks));

    _jvmtiEnv->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_VM_OBJECT_ALLOC, NULL);
    _jvmtiEnv->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_OBJECT_FREE, NULL);

    return JNI_TRUE;
}

extern "C" JNIEXPORT void JNICALL
Java_com_xsw_track_jvmti_impl_JVMTIImpl_nativeDetach(JNIEnv *env, jobject thiz) {
    if (NULL == _jvmtiEnv) {
        return;
    }
    _jvmtiEnv->SetEventNotificationMode(JVMTI_DISABLE, JVMTI_EVENT_VM_OBJECT_ALLOC, NULL);
    _jvmtiEnv->SetEventNotificationMode(JVMTI_DISABLE, JVMTI_EVENT_OBJECT_FREE, NULL);
}