/**
 * Copyright (C), 2018, 杨智杰
 * FileName: MyDispatcherServlet
 * Author:   猪猪
 * Date:     2018/12/29 15:19
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

package cn.yzj.SpringMVC.Servlet;

import cn.yzj.SpringMVC.Annotation.MyRequestMapping;
import cn.yzj.SpringMVC.IO.XmlConfigBuilder;
import cn.yzj.SpringMVC.utils.ClassUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈功能简述〉<br> 
 * 〈自定义springmvc中央控制器〉
 *
 * @author 猪猪
 * @create 2018/12/29
 * @since 1.0.0
 */
public class MyDispatcherServlet extends HttpServlet{
     //springmvc容器（用于存放带有@MyController注解的类）
     private Map<String,Object> springMVCBeans = new ConcurrentHashMap<>();
     //urlBeans容器，用于存放请求URL和带有注解类的映射关系
     private Map<String,Object> urlBeans = new ConcurrentHashMap<>();
    //urlMethos，用于存放请求URL和相应controller方法的映射关系
     private Map<String,Method> urlMethos = new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {
        //读取springmvc配置文件，并初始化springmvc容器
        XmlConfigBuilder builder = new XmlConfigBuilder("springmvc.xml");
        springMVCBeans = builder.loadSpringMVCXml();
        //初始化请求url和相应controller方法绑定
        myHandlerMapping();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求url
        String requestURI = req.getRequestURI();
        //根据url去urlBeans检查是否存在
        Object bean = urlBeans.get(requestURI);
        if (bean == null) {
            //不存在，证明该url无效，返回404
            resp.getWriter().print("not found the url --404");
            return;
        }
        //存在 根据url获取相应的方法
        Method method = urlMethos.get(requestURI);
        //反射执行该方法
        String resultPage = (String) ClassUtil.methodInvoke(bean, method);
        //利用视图解析器解析，渲染
        myResourceViewResolver(resultPage,req,resp);
    }

    /**
     * 视图解析器
     * @param resultPage
     * @param req
     * @param resp
     */
    public void myResourceViewResolver(String resultPage,HttpServletRequest req,HttpServletResponse resp){
        //前缀（可在springMVC配置文件配置）
        String prefix = "/";
        //后缀（可在springMVC配置文件配置）
        String suffix = ".jsp";
        try {
            //转发
            req.getRequestDispatcher(prefix + resultPage +suffix).forward(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * 初始化请求url和相应controller方法绑定
     */
    public void myHandlerMapping() {
        //遍历springMVC容器
        for (Map.Entry<String, Object> bean : springMVCBeans.entrySet()) {
            Object springMVCBean = bean.getValue();
            Class<?> claszz = springMVCBean.getClass();
            //判断该类是否带有相应注解
            MyRequestMapping declaredAnnotation = claszz.getDeclaredAnnotation(MyRequestMapping.class);
            String baseUrl = "";
            if (declaredAnnotation != null) {
                //获取该注解的值
                baseUrl = declaredAnnotation.value();
            }
            //获取该类中的所有方法
            Method[] declaredMethods = claszz.getDeclaredMethods();
            //遍历
            for (Method declaredMethod : declaredMethods) {
                //判断方法是否带有指定注解
                MyRequestMapping annotation = declaredMethod.getAnnotation(MyRequestMapping.class);
                //获取方法名
                String methodName = declaredMethod.getName();
                String methodUrl = "";
                if (annotation != null) {
                    //获取注解的值
                    methodUrl = annotation.value();
                }
                //存入容器中（请求url为key,value为该类，用于检查请求url是否有效）
                urlBeans.put(baseUrl + methodUrl,springMVCBean);
                //存入容器中（请求url为key,value为该方法，用于后面反射执行该方法）
                urlMethos.put(baseUrl + methodUrl,declaredMethod);
            }
        }

    }
}
