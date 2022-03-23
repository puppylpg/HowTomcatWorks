package ex13.pyrmont.startup;

//explain Host
import ex13.pyrmont.core.SimpleContextConfig;
import org.apache.catalina.Connector;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Loader;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.loader.WebappLoader;


public final class Bootstrap1 {
  public static void main(String[] args) {
    //invoke: http://localhost:8080/a/b/e/Primitive or http://localhost:8080/a/b/Modern
    System.setProperty("catalina.base", System.getProperty("user.dir"));
    // catalina.base被设为了当前工程根目录（程序从根目录执行的）
    System.out.println("catalina.base is set to: " + System.getProperty("user.dir"));
    Connector connector = new HttpConnector();

    Wrapper wrapper1 = new StandardWrapper();
    wrapper1.setName("Primitive");
    wrapper1.setServletClass("PrimitiveServlet");
    Wrapper wrapper2 = new StandardWrapper();
    wrapper2.setName("Modern");
    wrapper2.setServletClass("ModernServlet");

    Context context = new StandardContext();
    // StandardContext's start method adds a default mapper
    context.setPath("/a/b");
    // 去webapps（下文host设置了app base）下的app1加载文件（包括servlet文件）
    // 这个是加载文件的位置，跟uri没关系
    context.setDocBase("app1");

    context.addChild(wrapper1);
    context.addChild(wrapper2);

    LifecycleListener listener = new SimpleContextConfig();
    ((Lifecycle) context).addLifecycleListener(listener);

    Host host = new StandardHost();
    host.addChild(context);
    host.setName("localhost");
    // <catalina.base>/webapps作为寻找context的目录
    host.setAppBase("webapps");

    Loader loader = new WebappLoader();
    context.setLoader(loader);
    // context.addServletMapping(pattern, name);
    // "/e/Primitive"是除去context path的uri，是servlet的映射路径
    context.addServletMapping("/e/Primitive", "Primitive");
    context.addServletMapping("/Modern", "Modern");

    connector.setContainer(host);
    try {
      connector.initialize();
      ((Lifecycle) connector).start();
      ((Lifecycle) host).start();
  
      // make the application wait until we press a key.
      System.in.read();
      ((Lifecycle) host).stop();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}