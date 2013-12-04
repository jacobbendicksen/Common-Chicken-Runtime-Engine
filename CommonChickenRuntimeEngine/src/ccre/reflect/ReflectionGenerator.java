/*
 * Copyright 2013 Colby Skeggs
 * 
 * This file is part of the CCRE, the Common Chicken Runtime Engine.
 * 
 * The CCRE is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * The CCRE is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the CCRE.  If not, see <http://www.gnu.org/licenses/>.
 */
package ccre.reflect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author skeggsc
 */
public class ReflectionGenerator implements InterfaceReflectionGenerator {

    private static Comparator<Object> toStringComparator = new Comparator<Object>() {
        public int compare(Object o1, Object o2) {
            return o1.toString().compareTo(o2.toString());
        }
    };

    static String makeObjectTypeString(Class<?> c) {
        if (c.isArray()) {
            Class comp = c.getComponentType();
            if (comp.isPrimitive()) {
                return comp.getName() + "[]";
            } else {
                return makeObjectTypeString(comp) + "[]";
            }
        } else if (c.isPrimitive()) {
            if (c == boolean.class) {
                return "Boolean";
            } else if (c == byte.class) {
                return "Byte";
            } else if (c == char.class) {
                return "Character";
            } else if (c == short.class) {
                return "Short";
            } else if (c == int.class) {
                return "Integer";
            } else if (c == long.class) {
                return "Long";
            } else if (c == float.class) {
                return "Float";
            } else if (c == double.class) {
                return "Double";
            } else {
                return "Object /* bad primitive " + c.getName() + "*/";
            }
        } else {
            return c.getName();
        }
    }
    private HashMap<String, ArrayList<Object>> methods = new HashMap<String, ArrayList<Object>>();
    private HashMap<Object, Integer> idLookup = new HashMap<Object, Integer>();
    private HashMap<Class, Method[]> lookup = new HashMap<Class, Method[]>();
    private int nextId = 0;
    private ArrayList<String> mainLines = new ArrayList<String>();
    public ArrayList<String> output;
    private LinkedList<Class> toProcess = new LinkedList<Class>();
    private HashSet<Class> processed = new HashSet<Class>();
    private ArrayList<String> toIgnore = new ArrayList<String>();

    public ReflectionGenerator() {
        mainLines.add("package ccre.reflect;");
        mainLines.add("// Autogenerated file. Do not edit!");
        mainLines.add("public class ReflectionEngineImpl extends ReflectionEngine {");
        mainLines.add("\tpublic Object invoke(int id, Object aThis, Object[] args) throws Throwable {");
        mainLines.add("\t\tswitch (id) {");
    }

    public void end() {
        mainLines.add("\t\tdefault: throw new IllegalArgumentException(\"Invalid method ID!\");");
        mainLines.add("\t\t}");
        mainLines.add("\t}");
        mainLines.add("\tprotected void fillLookup() {");
        Map.Entry<String, ArrayList<Object>>[] tarray = methods.entrySet().toArray(new Map.Entry[methods.size()]);
        Arrays.sort(tarray, new Comparator<Map.Entry<String, ArrayList<Object>>>() {
            public int compare(Map.Entry<String, ArrayList<Object>> o1, Map.Entry<String, ArrayList<Object>> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        for (Map.Entry<String, ArrayList<Object>> mdata : tarray) {
            Collections.sort(mdata.getValue(), toStringComparator);
            Class[][] paramTypes = new Class[mdata.getValue().size()][];
            Class[] results = new Class[paramTypes.length];
            int[] invokeIds = new int[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                Object o = mdata.getValue().get(i);
                if (o instanceof Method) {
                    Method m = (Method) o;
                    paramTypes[i] = m.getParameterTypes();
                    results[i] = m.getReturnType();
                    invokeIds[i] = idLookup.get(m);
                } else if (o instanceof Field) {
                    Field f = (Field) o;
                    invokeIds[i] = idLookup.get(mdata.getKey());
                    if (mdata.getKey().endsWith("?")) {
                        paramTypes[i] = new Class[0];
                        results[i] = f.getType();
                    } else if (mdata.getKey().endsWith("!")) {
                        paramTypes[i] = new Class[]{f.getType()};
                        results[i] = Void.TYPE;
                    } else {
                        throw new RuntimeException("Should never happen.");
                    }
                } else {
                    Constructor c = (Constructor) o;
                    paramTypes[i] = c.getParameterTypes();
                    results[i] = c.getDeclaringClass();
                    invokeIds[i] = idLookup.get(c);
                }
            }
            ReflectionMethod m = new ReflectionMethod(mdata.getKey(), paramTypes, results, invokeIds, null);
            mainLines.add("\t\tfillLookup(\"" + m.serialize() + "\");");
        }
        mainLines.add("\t}");
        mainLines.add("}");
        for (int i = 0; i < mainLines.size(); i++) {
            String line = mainLines.get(i);
            if (line.indexOf('$') != -1) {
                mainLines.set(i, line.replace('$', '.'));
            }
        }
        output = mainLines;
        mainLines = null;
    }

    public void printAll(OutputStream out) throws IOException {
        for (String line : output) {
            out.write(line.getBytes());
            out.write('\n');
        }
    }

    private Method[] getForClass(Class cls) {
        Method[] mthds = lookup.get(cls);
        if (mthds == null) {
            mthds = cls.getMethods();
            Arrays.sort(mthds, toStringComparator);
            lookup.put(cls, mthds);
        }
        return mthds;
    }

    private boolean classHasMethod(Class cls, Method m, boolean ignoreTop) {
        if (!ignoreTop) {
            String n = m.getName();
            Class<?> rt = m.getReturnType();
            Class<?>[] ps = m.getParameterTypes();
            for (Method o : getForClass(cls)) {
                if (n.equals(o.getName()) && rt == o.getReturnType() && Arrays.equals(ps, o.getParameterTypes())) {
                    return true;
                }
            }
        }
        Class sup = cls.getSuperclass();
        if (sup != null && classHasMethod(sup, m, false)) {
            return true;
        }
        for (Class itf : cls.getInterfaces()) {
            if (classHasMethod(itf, m, false)) {
                return true;
            }
        }
        return false;
    }

    public void generateForClass(Class cls) throws SecurityException {
        Class sc = cls.getSuperclass();
        if (sc != null) {
            toProcess.add(sc);
        }
        toProcess.addAll(Arrays.asList(cls.getInterfaces()));
        if (!Modifier.isPublic(cls.getModifiers())) {
            return;
        }
        String path = cls.getName();
        Field[] flds = cls.getDeclaredFields();
        Arrays.sort(flds, toStringComparator);
        for (Field f : flds) {
            toProcess.add(f.getType());
            if (!Modifier.isPublic(f.getModifiers())) {
                continue;
            }
            String mkey = path + "." + f.getName() + "?";
            ArrayList<Object> arr = methods.get(mkey);
            if (arr == null) {
                methods.put(mkey, arr = new ArrayList<Object>());
            }
            arr.add(f);
            int id = generateForField(Modifier.isStatic(f.getModifiers()), cls.getName(), f.getName(), f.getType(), false);
            idLookup.put(mkey, id);
            if (!Modifier.isFinal(f.getModifiers())) {
                mkey = path + "." + f.getName() + "!";
                arr = methods.get(mkey);
                if (arr == null) {
                    methods.put(mkey, arr = new ArrayList<Object>());
                }
                arr.add(f);
                id = generateForField(Modifier.isStatic(f.getModifiers()), cls.getName(), f.getName(), f.getType(), true);
                idLookup.put(mkey, id);
            }
        }
        for (Method m : getForClass(cls)) {
            if (classHasMethod(cls, m, true)) {
                continue;
            }
            toProcess.add(m.getReturnType());
            toProcess.addAll(Arrays.asList(m.getParameterTypes()));
            String mkey = path + "." + m.getName();
            ArrayList<Object> arr = methods.get(mkey);
            if (arr == null) {
                methods.put(mkey, arr = new ArrayList<Object>());
            }
            arr.add(m);
            int id = generateForMethod(Modifier.isStatic(m.getModifiers()), cls.getName(), m.getName(), m.getReturnType(), m.getParameterTypes());
            idLookup.put(m, id);
        }
        if (!Modifier.isAbstract(cls.getModifiers())) {
            Constructor[] ctrs = cls.getConstructors();
            Arrays.sort(ctrs, toStringComparator);
            for (Constructor c : ctrs) {
                toProcess.addAll(Arrays.asList(c.getParameterTypes()));
                String mkey = path + ".__new__";
                ArrayList<Object> arr = methods.get(mkey);
                if (arr == null) {
                    methods.put(mkey, arr = new ArrayList<Object>());
                }
                arr.add(c);
                int id = generateForConstructor(cls.getName(), c.getParameterTypes());
                idLookup.put(c, id);
            }
        }
    }

    private void recurseFinding(File f, String base, ClassLoader ldr) throws ClassNotFoundException {
        if (f.isDirectory()) {
            File[] fls = f.listFiles();
            Arrays.sort(fls);
            for (File i : fls) {
                recurseFinding(i, base, ldr);
            }
        } else if (f.getName().endsWith(".class")) {
            String bf = f.toString();
            if (bf.length() <= base.length() || !bf.substring(0, base.length()).equalsIgnoreCase(base)) {
                System.err.println("Ignoring: " + bf);
            }
            String cname = bf.substring(base.length(), bf.length() - 6).replace('/', ' ').replace('\\', ' ').trim().replace(' ', '.');
            this.toProcess.add(Class.forName(cname, false, ldr));
        }
    }

    private void runAll() {
        outer:
        while (!toProcess.isEmpty()) {
            Class c = toProcess.removeFirst();
            if (processed.contains(c)) {
                continue;
            }
            for (String s : toIgnore) {
                if (c.getName().matches(s)) {
                    continue outer;
                }
            }
            processed.add(c);
            if (c.isPrimitive()) {
                continue;
            }
            if (c.isArray()) {
                toProcess.add(c.getComponentType());
                continue;
            }
            String name = c.getName();
            if (name.startsWith("sun") || name.startsWith("java.util.concurrent") || name.startsWith("java.nio") || name.startsWith("java.net") || name.startsWith("java.security") || name.startsWith("java.util.regex") || name.startsWith("java.lang.ref") || name.contains("Locale")) {
                continue;
            }
            generateForClass(c);
        }
    }

    public void mainV(String[] args) throws ClassNotFoundException, IOException {
        File f = new File("build/classes");
        if (args.length > 1) {
            ArrayList<URL> urls = new ArrayList<URL>();
            for (int i = 1; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    toIgnore.add(args[i].substring(1));
                    continue;
                }
                for (String p : args[i].split(";")) {
                    urls.add(new File(p).toURI().toURL());
                }
            }
            recurseFinding(f, f.toString(), new URLClassLoader(urls.toArray(new URL[urls.size()]), null));
        } else {
            recurseFinding(f, f.toString(), ReflectionGenerator.class.getClassLoader());
        }

        runAll();
        end();
        if (args.length > 0) {
            printAll(new FileOutputStream(args[0]));
        } else {
            printAll(System.out);
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        ReflectionGenerator test = new ReflectionGenerator();
        test.mainV(args);
    }

    private int generateForMethod(boolean isStatic, String className, String methodName, Class<?> returnType, Class<?>[] parameterTypes) {
        int id = nextId++;
        StringBuilder paramlist = new StringBuilder();
        int i = 0;
        for (Class<?> param : parameterTypes) {
            if (param.isPrimitive()) {
                paramlist.append("(").append(param.getName()).append(")");
            }
            paramlist.append("(").append(makeObjectTypeString(param)).append(")args[").append(i++).append("], ");
        }
        if (paramlist.length() > 0) {
            paramlist.setLength(paramlist.length() - 2);
        }
        String fetchPath = isStatic ? className : "((" + className + ")aThis)";
        if (returnType == Void.TYPE) {
            mainLines.add("\t\tcase " + id + ": " + fetchPath + "." + methodName + "(" + paramlist + "); return null;");
        } else {
            mainLines.add("\t\tcase " + id + ": return " + fetchPath + "." + methodName + "(" + paramlist + ");");
        }
        return id;
    }

    private int generateForConstructor(String className, Class<?>[] parameterTypes) {
        int id = nextId++;
        StringBuilder paramlist = new StringBuilder();
        int i = 0;
        for (Class<?> param : parameterTypes) {
            paramlist.append("(").append(makeObjectTypeString(param)).append(")args[").append(i++).append("], ");
        }
        if (paramlist.length() > 0) {
            paramlist.setLength(paramlist.length() - 2);
        }
        mainLines.add("\t\tcase " + id + ": return new " + className + "(" + paramlist + ");");
        return id;
    }

    private int generateForField(boolean isStatic, String className, String fieldName, Class<?> type, boolean isWrite) {
        int id = nextId++;
        String fetchPath = isStatic ? className : "((" + className + ")aThis)";
        if (isWrite) {
            mainLines.add("\t\tcase " + id + ": " + fetchPath + "." + fieldName + " = (" + makeObjectTypeString(type) + ")args[0]; return null;");
        } else {
            mainLines.add("\t\tcase " + id + ": return " + fetchPath + "." + fieldName + ";");
        }
        return id;
    }
}
