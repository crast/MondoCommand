package mondocommand.dynamic;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;

import mondocommand.CallInfo;
import mondocommand.MondoCommand;
import mondocommand.MondoFailure;
import mondocommand.SubCommand;
import mondocommand.SubHandler;

public class SubCommandFinder {
    protected PrintStream logOutput = System.err;
    private MondoCommand base;

    public SubCommandFinder(MondoCommand base) {
        this.base = base;
    }

    public void registerMethods(Object handler) {
        for (Method method : sortedMethods(handler)) {
            Sub subInfo = method.getAnnotation(Sub.class);
            if (subInfo == null) continue;
            Class<?> paramTypes[] = method.getParameterTypes();
            if (paramTypes.length != 1 || !paramTypes[0].equals(CallInfo.class)) {
                logOutput.println(String.format(
                    "MondoCommand: @Sub marked on  '%s' from class %s, must receive only one argument of type CallInfo.",
                    method.getName(), handler.getClass().getName()
                ));
                continue;
            }
            registerMethod(handler, method, subInfo);
        }
    }

    private void registerMethod(Object handler, Method method, Sub subInfo) {
        String name = subInfo.name();
        if (name.equals("")) {
            name = method.getName();
        }
        String permission = subInfo.permission();
        if (permission.equals("")) {
            permission = null;
        }
        SubCommand sub = base.addSub(name, permission)
            .setMinArgs(subInfo.minArgs())
            .setDescription(subInfo.description())
            .setUsage(subInfo.usage());

        if (subInfo.allowConsole()) {
            sub = sub.allowConsole();
        }
        sub.setHandler(buildHandler(handler, method));
    }

    private static SubHandler buildHandler(final Object handler, final Method method) {
        return new SubHandler() {
            @Override
            public void handle(CallInfo call) throws MondoFailure {
                try {
                    method.invoke(handler, call);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof MondoFailure) {
                        throw (MondoFailure) e.getCause();
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * Reflect the methods on this object, sorted by name.
     * @param handler
     * @return an ArrayList of methods.
     */
    private ArrayList<Method> sortedMethods(Object handler) {
        TreeMap<String, Method> methodMap = new TreeMap<String, Method>();
        for (Method method : handler.getClass().getDeclaredMethods()) {
            methodMap.put(method.getName(), method);
        }
        return new ArrayList<Method>(methodMap.values());
    }
}
