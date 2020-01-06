package xuhuimin.test.biohttpserver;


import xuhuimin.test.Annotation.MyRequestMapping;
import xuhuimin.test.Annotation.User;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerTest {

    public static Map<String, Object> beanMap = new HashMap<>();
    public static Map<String, MethodInfo> methodMap = new HashMap<>();

    public static void main(String[] args) throws IOException {

        refreshBeanFactory("xuhuimin.test.biohttpserver");

//        建立服务器端socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(80));
        serverSocketChannel.configureBlocking(false);
//        建立selector
        Selector selector = Selector.open();
//        将服务器端socket注册到selector中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(3000) <= 0) {
                continue;
            }
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                handler(selectionKey);
                keyIterator.remove();
            }
        }
    }

    // 1. 初始化 beanMap
    // 2. 初始化 methodMap
    private static void refreshBeanFactory(String pkg) {
        String path = pkg.replace(".", "/");
        URL url = ServerTest.class.getClassLoader().getResource(path);
        File file = new File(url.getPath());
        beanParse(file);

    }

    private static void beanParse(File file) {
        if (!file.isDirectory()){
            return;
        }
        File[] files = file.listFiles(pathname -> {
            if (pathname.isDirectory()) {
                beanParse(pathname);
                return false;
            }
            return pathname.getName().endsWith(".class");
        });
        for (File file1:files){
            String absolutePath = file1.getAbsolutePath();
          String classname= absolutePath.split("classes\\\\")[1].replace("\\",".").split("\\.class")[0];
            try {
                Class<?> cls = Class.forName(classname);
                MyRestController myRestController = cls.getAnnotation(MyRestController.class);
                if (myRestController!=null){
                    controllerParse(cls);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void controllerParse(Class<?> cls) {
        try {
            beanMap.put(cls.getSimpleName(),cls.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Method[] methods = cls.getDeclaredMethods();
        for (Method m:methods) {
            MyRequestMapping annotation = m.getDeclaredAnnotation(MyRequestMapping.class);
            if (annotation==null){
                continue;
            }
            String url = annotation.value();
            methodMap.put(url,new MethodInfo(m,cls.getSimpleName()));
        }

    }


    private static void handler(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            acceptHandler(key);
        } else if (key.isReadable()) {
            requestHandler(key);
        }
    }

    private static void requestHandler(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
//        clear：设置为写模式
        byteBuffer.clear();
//        从socket中读取内容到byteBuffer
        if (socketChannel.read(byteBuffer) == -1) {
            socketChannel.close();
            return;
        }
//        设置读模式
        byteBuffer.flip();
        String requestHeader = new String(byteBuffer.array());
        String url = requestHeader.split("\r\n")[0].split(" ")[1];

        List<String> urlParams = new ArrayList<>();
        List<Object> Object1=new ArrayList<>();
        urlParamsParse(url, urlParams,Object1);

        System.out.println(url);

        url = url.split("\\?")[0];
        System.out.println(url);


        String content = null;

        try {
//            content=methodInvoke1(url,Object1);
            content = methodInvoke(url, urlParams,Object1);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (content == null)
            content = "404";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 OK\r\n");
        stringBuilder.append("Content-Type:text/html;charset=utf-8\r\n\r\n");
        stringBuilder.append("<html><head><title>HttpTest</title></head><body>\r\n");
        stringBuilder.append(content);
        stringBuilder.append("</body></html>");
        socketChannel.write(ByteBuffer.wrap(stringBuilder.toString().getBytes()));
        socketChannel.close();
    }



    // 调用url所映射的方法
    private static String methodInvoke(String url, List<String> urlParams,List<Object> object1) throws InvocationTargetException, IllegalAccessException {
        MethodInfo methodInfo = methodMap.get(url);
        System.out.println(methodMap.size());

        if (methodInfo==null){
            return "505";
        }
        String className = methodInfo.getClassname();
        Method method = methodInfo.getMethod();
        Object beanObj = beanMap.get(className);

        //得到所有的已知函数的参数
        Parameter[] parameters = method.getParameters();

        Object[] params = new Object[urlParams.size()];
        Object[] objparam=new Object[object1.size()];
        // 如果url中参数个数与方法中参数个数不一致，则返回404
        if (params.length != parameters.length) {
            return "参数个数不匹配";
        }


        // 按照方法中参数的属性，来进行参数转换和填充
        int i=0;
        for (Parameter ps:parameters) {
            //如果传入的是对象，则调用下面的方法
            System.out.println(ps.getType().getSimpleName());
            if (ps.getType().getSimpleName().equals("User")){
            }
            String Type = ps.getType().getSimpleName();
            String paraName1 = ps.getName();
            boolean flag=false;
            for(String p: urlParams) {
                String pp []= p.split("=");
                if (paraName1.equals(pp[0])){
                    Object pValue= null;
                    try {
                        pValue = paramTranslate(Type,pp[1]);
                    } catch (Exception e) {
                        return "参数字段类型不对！";

                    }
                    params[i++]=pValue;
                    flag=true;
                    continue;
                }
            }
            if (!flag)
                return "参数类型不匹配";
        }

        return (String) method.invoke(beanObj, params);
    }

    private static Object paramTranslate(String type, String s) {
        switch (type){
            case "int":
                return Integer.valueOf(s);
            case "double":
                return Double.valueOf(s);
            case "float":
                return Float.valueOf(s);
            default:
                return s;
        }
    }

    // 解析url参数
    private static void urlParamsParse(String url, List<String> urlParams,List<Object> Object1) {
        if (!url.contains("?")) {
            return;
        }
        String[] params = url.replaceFirst(".*?\\?","").split("&");
        if (params[0].contains("|")){
            String[] split = params[0].split("|");
            if (split.length>2){
                return;
            }
            int id=0;
            String name="";
            for (String s:split) {
                if (s.split("=")[0].equals("id")){
                    id=Integer.valueOf(s.split("=")[1]);
                }
                if (s.split("=")[0].equals("name")){
                     name=s.split("=")[1];
                }
            }
            User user = new User(id, name);
            Object1.add(user);
        }
        for (String p:params) {
            if (!p.contains("=")){
                continue;
            }
            urlParams.add(p);
        }

    }

    //  服务器端处理连接请求，将客户端socketChannel注册到selector中
    private static void acceptHandler(SelectionKey key) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }

}
