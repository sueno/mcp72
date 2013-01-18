package modclassloader;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.ClassPool;
import javassist.CtClass;

public class TargetClasslassList {

	private static Map<String, List<String>> classMap;

	/**
	 * 
	 */
	static {

		classMap = new HashMap<String, List<String>>();

		try {

			String str = System.getProperty("java.class.path");
			String[] path = str.split(File.pathSeparator);
			URI[] classPath = new URI[path.length];
			for (int i = 0; i < path.length; ++i) {
				classPath[i] = new URI(path[i]);
			}

			String pkg = new String("");
			for (int i = 0; i < classPath.length; i++) {
				File file = new File(classPath[i].toString());
				Pattern ptt = Pattern.compile(".*\\.jar$");
				Matcher mcc = ptt.matcher(file.getName());
				if (mcc.find()) {
				} else {
//					classSearch(file, "");
					readFolder(file, "");
				}
			}

			print(classMap);

		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/**
	 * 
	 * @param folderPath
	 */
	private static void readFolder(File dir, String pkg) {

		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (!file.exists()) {
				continue;
			} else if (file.isDirectory()) {
//				System.err.println("#DIR : "+file);
				readFolder(file, pkg + file.getName() + ".");
			} else if (file.isFile()) {
				execute(file, pkg);
			}
		}
	}

	/**
	 * 
	 * @param filePath
	 */
	private static void execute(File file, String pkg) {
//		System.out.println(pkg+file.getName());
		String fl = file.getName();
		Pattern pt = Pattern.compile(".*\\.class$");
		Matcher mc = pt.matcher(fl);
		if (mc.matches()) {
			fl = fl.replaceAll("\\.class", "");
			try {
				ClassPool classPool = ClassPool.getDefault();
				CtClass targetClass = classPool.get(pkg + fl);

				Object[] annList = targetClass.getAnnotations();
				for (Object ann : annList) {
					if (ann instanceof TargetClass) {
						TargetClass target = (TargetClass) ann;
						String annValue = target.value();
						// add list
						if (classMap.containsKey(annValue)) {
							classMap.get(annValue).add(targetClass.getName());
						} else {
							List<String> list = new ArrayList<String>();
							list.add(targetClass.getName());
							classMap.put(annValue, list);
						}
					} else {
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	public static List<String> getClassList(String className) {
		if (classMap.containsKey(className)) {
			return classMap.get(className);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public static Map<String, List<String>> getList() {
		return classMap;
	}

	private static void print(Map<String, ?> map) {
		System.err.println("+++++++++TargetClass+++++++++");
		for (Entry m : map.entrySet()) {
			System.err.println("+  "+m.getKey()+" : "+m.getValue());
		}
		System.err.println("+++++++++++++++++++++++++++++\n");
	}

}
