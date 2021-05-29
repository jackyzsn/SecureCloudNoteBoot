#include <string.h>
#include <jni.h>
#include "load-env.h"

JNIEXPORT jstring JNICALL
Java_com_jszsoft_securecloudnote_common_util_CommonUtils_tokenFromJNI( JNIEnv* env,
                                                  jobject thiz ) {
    return (*env)->NewStringUTF(env, "xxxxx");
}

JNIEXPORT jstring JNICALL
Java_com_jszsoft_securecloudnote_common_util_CommonUtils_systemIdFromJNI( JNIEnv* env,
                                                  jobject thiz ) {
    return (*env)->NewStringUTF(env, "xxxxx");
}