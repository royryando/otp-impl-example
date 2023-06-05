package id.getsabi;

import id.getsabi.service.VerihubsFC;
import id.getsabi.service.VerihubsWA;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("App starting..");

        // Run all service in a thread
        List<Class<?>> classes = getClasses("id.getsabi.service");
        for (Class<?> clazz : classes) {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                if (!Modifier.isPublic(constructor.getModifiers())) constructor.setAccessible(true);
                Object instance = constructor.newInstance();
                new Thread((Runnable) instance).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        new VerihubsFC().run();
    }

    public static List<Class<?>> getClasses(String packageName) {
        List<Class<?>> classes = new ArrayList<>();

        try {
            String path = packageName.replace('.', '/');
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(path);
            if (resource != null) {
                File directory = new File(resource.getFile());
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName();
                            if (fileName.endsWith(".class")) {
                                String className = packageName + '.' + fileName.substring(0, fileName.length() - 6);
                                Class<?> clazz = Class.forName(className);
                                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                                    classes.add(clazz);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}