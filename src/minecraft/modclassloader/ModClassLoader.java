package modclassloader;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javassist.*;

public class ModClassLoader {

	private static Integer id;

	public static void main(String[] args) throws Exception {
		System.err.println("ModClass Loading...");


		// Block Class Load
		blockLoader();

	}

	/**
	 * define Class to ByteCode ContextClassLoader add definition Class
	 * 
	 * @param className
	 * @param classCode
	 */
	public static void defineClass(String className, byte[] classCode) {
		try {
			ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
			Object[] params = new Object[] { className, classCode, new Integer(0), new Integer(classCode.length) };
			Class[] types = new Class[] { String.class, byte[].class, int.class, int.class };

			Method defineMethod = ClassLoader.class.getDeclaredMethod("defineClass", types);
			defineMethod.setAccessible(true);
			defineMethod.invoke(currentLoader, params);
		} catch (Throwable th) {
			th.getCause().printStackTrace();
//			th.printStackTrace();
		}
	}

	private static int getID() {
		id++;
		return id;
	}

	private static void defineValue(CtClass target, String valName, String defVal) throws Exception {
		CtField dummyList = target.getDeclaredField(valName);
		dummyList.setName("dummy_" + valName);
		CtField blocksList = CtField.make(defVal, target);
		target.addField(blocksList);
	}

	/**
	 * redefine net.minecraft.src.Block Class
	 * 
	 * @param cp
	 * @throws Exception
	 */
	private static void blockLoader() throws Exception {
		String targetClassName = "net.minecraft.src.Block";
		id = 136;
		
		ClassPool cp = ClassPool.getDefault();
		String process = new String("");

		CtClass blockClass = cp.get(targetClassName);

		// add Field & Method

		List<String> list = TargetClasslassList.getClassList(targetClassName);
		if (list != null) {

			for (String cl : list) {
				String[] classPkg = cl.split("\\.");
				String className = classPkg[classPkg.length-1].toLowerCase();
				CtField blockInst = CtField.make("public static final "+targetClassName+" " + className + ";", blockClass);
				process = process.concat(className + " = new " + cl + "(" + getID() + ",1);");
				blockClass.addField(blockInst);
			}

			CtMethod initBlockMethod = CtMethod.make("public static void initClass() {System.err.println(\"execute : " + process + "\");try{" + process + "}catch(Exception ex){ex.printStackTrace();}}", blockClass);
			blockClass.addMethod(initBlockMethod);

			CtConstructor initBlock = blockClass.getConstructor("(ILnet/minecraft/src/Material;)V");
			initBlock.insertBefore("if($1==1)initClass();");

			defineClass(blockClass.getName(), blockClass.toBytecode());
			blockClass.detach();
		}
	}
}