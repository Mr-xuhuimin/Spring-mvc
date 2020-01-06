package xuhuimin.test.biohttpserver;


import xuhuimin.test.Annotation.MyRequestMapping;
import xuhuimin.test.Annotation.UserController;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioHttpServer {
    public static void main(String[] args) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {


        //创建一个Server的Socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(80));
        serverSocketChannel.configureBlocking(false);
        //创建一个选择器
        Selector selector = Selector.open();
        //将server注册到选择器中作监听作用！
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //打印一下，服务器开启！
        System.out.println("服务器开启！！");
        //死循环监听选择器里面有没有，请求。有就把请求拿出来处理！
        while (true){
            if (selector.select(3000)==0){
                continue;
            }
            //得到需要处理的所有请求（socket）
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){

                SelectionKey next = iterator.next();
                //处理每一个请求
                httpHandle(next);

                iterator.remove();

            }
        }
    }

    private static void httpHandle(SelectionKey next) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        //发送请求，注册到选择器里面
        if (next.isAcceptable()){
            accepHandle(next);
        }else if (next.isReadable()){
            //实际处理请求
            requesHaandle(next);
        }
    }

    private static void requesHaandle(SelectionKey next) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        //将key实例成socket操作
        SocketChannel channel = (SocketChannel) next.channel();
        //处理我们已经在注册阶段，放入缓冲区的数据
        ByteBuffer attachment = (ByteBuffer)next.attachment();
        attachment.clear();
        //返回值为-1，说明缓冲区没有数据，就关闭了！
        if (channel.read(attachment)==-1){
            channel.close();
            return;
        }
        //将缓冲区打开。操作缓存区数据!读模式！
        attachment.flip();
        String requestMsg = new String(attachment.array());
        String url= requestMsg.split("\r\n")[0].split(" ")[1];
        System.out.println(requestMsg);

        //反射调用
        System.out.println(url);
        String url1=url.split("\\?")[0];
        System.out.println(url1);
        String para1="";
        if (url.split("\\?").length>1){
            if (!url.split("\\?")[1].equals(" ")){
                String para=url.split("\\?")[1];
                para1=para.split("=")[1];
                System.out.println(para1);
            }
        }





        String content=null;
        Method[] declaredMethods = UserController.class.getDeclaredMethods();
        for (Method m : declaredMethods) {
            if (m.getAnnotation(MyRequestMapping.class) != null) {
                MyRequestMapping annotation = m.getAnnotation(MyRequestMapping.class);
                System.out.println(annotation.value() + " " + m.getName());
                m.getTypeParameters();
                Parameter[] parameters = m.getParameters();

                if (annotation.value().equals(url1)&&parameters.length>0){
                    content  =(String) m.invoke(new UserController(), Integer.valueOf(para1));
                }else if (annotation.value().equals(url1)&&parameters.length==0){
                    content=(String) m.invoke(new UserController());
                    System.out.println(11);
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("HTTP/1.1 200 ok \r\n");
        stringBuilder.append("Content-Type:text/html;charset=utf-8\r\n");
        stringBuilder.append("\r\n");
        stringBuilder.append("<html><head><title>requesthead</title></head><body>");
//        String content = HttpServerBs.map.get(url);


        stringBuilder.append(content!=null? content:"404");
        stringBuilder.append("</body></html>");

        //将数据写出
        channel.write(ByteBuffer.wrap(stringBuilder.toString().getBytes()));
        channel.close();


    }

    private static void accepHandle(SelectionKey next) throws IOException {
        SocketChannel socketChannel = ((ServerSocketChannel) next.channel()).accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(next.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(1024));
    }
}
