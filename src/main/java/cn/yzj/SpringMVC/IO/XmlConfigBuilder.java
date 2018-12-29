/**
 * Copyright (C), 2018, 杨智杰
 * FileName: XmlConfigBuilder
 * Author:   猪猪
 * Date:     2018/12/29 15:22
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

package cn.yzj.SpringMVC.IO;

import cn.yzj.SpringMVC.Annotation.MyController;
import cn.yzj.SpringMVC.utils.ClassUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈功能简述〉<br> 
 * 〈springmvc配置文件解析〉
 *
 * @author 猪猪
 * @create 2018/12/29
 * @since 1.0.0
 */
public class XmlConfigBuilder {
     //springmvc配置文件类路径
     private String xmlPath;
     //springmvc容器（用于存放带有@MyController注解的类）
     private Map<String,Object> springMVCBean = new ConcurrentHashMap<>();


    public XmlConfigBuilder(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    /**
     * 解析springmvc配置文件
     * @return
     */
    public Map<String,Object> loadSpringMVCXml()  {
        //将配置文件转换为二进制流
         InputStream is = Resources.getResourceAsStream(xmlPath);
         //利用dom4j和xpath解析springmvc配置文件
         SAXReader reader = new SAXReader();
         Document document = null;
        try {
            document = reader.read(is);
            //读取指定扫描包标签
            List<Element> nodes = document.selectNodes("//context:component-scan");
            if (!nodes.isEmpty() || nodes != null){
                //获取到指定扫描包（controller）
                String basepackage = nodes.get(0).attributeValue("base-package");
                //获取指定扫描包下所有类
                List<Class<?>> classes = ClassUtil.getClasses(basepackage);
                //检验指定扫描包下所有类是否带有带有指定注解，若有将其放入springmvc容器
                hasExitAnnotation(classes);
                //返回springmvc容器
                return springMVCBean;
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

         return null;
     }

    /**
     * 检验指定扫描包下所有类是否带有带有指定注解，若有将其放入springmvc容器
     * @param classes
     */
     public void hasExitAnnotation(List<Class<?>> classes){
         //遍历
         for (Class<?> classInfo : classes) {
             //判断是否含有指定注解
             MyController declaredAnnotation = classInfo.getDeclaredAnnotation(MyController.class);
             if (declaredAnnotation != null) {
                 //获取带有注解的该类的名称
                 String simpleName = classInfo.getSimpleName();
                 try {
                     //将类名首字母小写为key，该类实例化为value，存入springMVC容器
                     springMVCBean.put(ClassUtil.toLowerCaseFirstOne(simpleName),ClassUtil.newInstance(classInfo));
                 } catch (Exception e) {
                     e.printStackTrace();
                     throw new RuntimeException();
                 }
             }
         }
     }


}
