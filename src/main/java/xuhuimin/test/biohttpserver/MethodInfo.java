package xuhuimin.test.biohttpserver;

import java.lang.reflect.Method;

public class MethodInfo {
   private Method method;
   private String  classname;

    public MethodInfo(Method method, String classname) {
        this.method = method;
        this.classname = classname;
    }

    public MethodInfo() {
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
