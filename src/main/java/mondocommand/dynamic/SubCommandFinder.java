package mondocommand.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mondocommand.CallInfo;
import mondocommand.MondoCommand;
import mondocommand.MondoFailure;
import mondocommand.SubCommand;
import mondocommand.SubHandler;

public class SubCommandFinder {
    private MondoCommand base;

    public SubCommandFinder(MondoCommand base) {
        this.base = base;
    }

    public void registerMethods(Object handler) {
        for (Method method: handler.getClass().getDeclaredMethods()) {
            Sub subInfo = method.getAnnotation(Sub.class);
            if (subInfo == null) continue;
            // TODO check arguments and throws for correctness.
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
    }
    
    public SubHandler buildHandler(final Object handler, final Method method) {
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
        };
    }
}
