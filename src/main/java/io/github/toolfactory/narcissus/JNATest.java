package io.github.toolfactory.narcissus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import com.sun.jna.JNIEnv;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class JNATest {

    static interface JNI extends Library {
        // Method defined here:
        // https://github.com/openjdk-mirror/jdk7u-hotspot/blob/master/src/share/vm/prims/jvm.cpp#L4071
        public Object JVM_InvokeMethod(JNIEnv env, Method method, Object obj, Object... args);

        // https://github.com/openjdk-mirror/jdk7u-hotspot/blob/master/src/share/vm/prims/jvm.cpp#L4162
        public Object[] JVM_GetAllThreads(JNIEnv env, Class<?> dummy);

        // https://github.com/openjdk-mirror/jdk7u-hotspot/blob/master/src/share/vm/prims/jvm.cpp#L720
        public Class<?> JVM_FindClassFromBootLoader(JNIEnv env, byte[] name);

        // https://github.com/openjdk-mirror/jdk7u-hotspot/blob/master/src/share/vm/prims/jvm.cpp#L1111
        public Object JVM_DoPrivileged(JNIEnv env, Class<?> cls, Object action, Object context,
                boolean wrapException);
    }

    void print(String s) {
        System.out.println(s);
    }

    public static void main(String[] args) throws Exception {
        // Load the JNI methods using JNA.
        // Also works with libName set to null, which might be more portable:
        // http://java-native-access.github.io/jna/5.9.0/javadoc/overview-summary.html
        // "If the library name is null, your mappings will apply to the current process instead of a separately
        // loaded library."
        String libName = null;
        // String libName = "jvm";
        JNI jni = Native.load(libName, JNI.class,
                Collections.singletonMap(Library.OPTION_ALLOW_OBJECTS, Boolean.TRUE));

        // This works
        System.out.println(Arrays.toString(jni.JVM_GetAllThreads(JNIEnv.CURRENT, null)));

        // This works
        System.out.println(jni.JVM_FindClassFromBootLoader(JNIEnv.CURRENT, "java/lang/Integer".getBytes()));

        //        // This doesn't work for some reason, it causes the VM to crash
        //        Method method = null;
        //        for (Method m : JNATest.class.getDeclaredMethods()) {
        //            if (m.getName().equals("print")) {
        //                method = m;
        //                break;
        //            }
        //        }
        //        if (method == null) {
        //            throw new NoSuchMethodException();
        //        }
        //        jni.JVM_InvokeMethod(JNIEnv.CURRENT, method, new JNATest(), "hello");

        //        // UnsatisfiedLinkError: Error looking up function 'JVM_DoPrivileged': /usr/java/jdk-16.0.1/bin/java: undefined symbol: JVM_DoPrivileged
        //        final PrivilegedAction<Object> action = new PrivilegedAction<Object>() {
        //            @Override
        //            public Object run() {
        //                for (Method m : Method.class.getDeclaredMethods()) {
        //                    System.out.println(m.getName());
        //                }
        //                return null;
        //            }
        //        };
        //        jni.JVM_DoPrivileged(JNIEnv.CURRENT, JNATest.class, action, new JNATest(), false);
    }
}
