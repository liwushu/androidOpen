package com.flying.libinject;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by shuliwu on 2018/1/19.
 */

public class ViewInjetUtils {
    public static void inject(Activity activity) {
        injectContentView(activity);
        injectView(activity);
        injectEvent(activity);
    }

    private static void injectContentView(Activity activity) {
        Class<? extends Activity> activityClz = activity.getClass();
        ContentView contentView = activityClz.getAnnotation(ContentView.class);
        if(contentView != null) {
            int layoutResId = contentView.value();
            try {
                Method setContentViewMethod = activityClz.getMethod("setContentView", int.class);
                setContentViewMethod.invoke(activity,layoutResId);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectView(Activity activity) {
        Class<? extends Activity> activityClz = activity.getClass();
        Field[] fields = activityClz.getDeclaredFields();
        for(Field field:fields) {
            ViewInject viewInject = field.getAnnotation(ViewInject.class);
            if(viewInject != null) {
                int value = viewInject.value();
                View view = activity.findViewById(value);
                try {
                    field.setAccessible(true);
                    field.set(activity,view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private static void injectEvent(final Activity activity) {
//        Class<? extends Activity> activityClz = activity.getClass();
//        Method[] methods = activityClz.getMethods();
//        for(final Method method:methods) {
//            EventInject eventInject = method.getAnnotation(EventInject.class);
//            if(eventInject != null) {
//                int[] values = eventInject.value();
//                for(int value:values) {
//                    View view = activity.findViewById(value);
//                    view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            try {
//                                method.invoke(activity,v);
//                            } catch (IllegalAccessException e) {
//                                e.printStackTrace();
//                            } catch (InvocationTargetException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        }
//    }

    private static void injectEvent(final Activity activity) {
        Class<? extends Activity> activityClz = activity.getClass();
        Method[] methods = activityClz.getMethods();
        for(final Method method:methods) {
            EventInject eventInject = method.getAnnotation(EventInject.class);
            if(eventInject != null) {
                int[] values = eventInject.value();
                View.OnClickListener listener = (View.OnClickListener) Proxy.newProxyInstance(activity.getClassLoader(), new Class[]{View.OnClickListener.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method1, Object[] args) throws Throwable {
                        method.invoke(activity,args);
                        return null;
                    }
                });
                for(int value:values) {
                    final View view = activity.findViewById(value);
                   try{
                       Method setOnClickMethod = view.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                       if(method != null) {
                           setOnClickMethod.invoke(view,listener);
                       }
                   }catch (Exception e) {
                       e.printStackTrace();
                   }
                }
            }
        }
    }

}
