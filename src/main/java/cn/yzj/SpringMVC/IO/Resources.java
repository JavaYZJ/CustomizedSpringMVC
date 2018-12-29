/**
 * Copyright (C), 2018, 杨智杰
 * FileName: Resources
 * Author:   猪猪
 * Date:     2018/12/29 15:22
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

package cn.yzj.SpringMVC.IO;

import java.io.InputStream;

/**
 * 〈功能简述〉<br> 
 * 〈〉
 *
 * @author 猪猪
 * @create 2018/12/29
 * @since 1.0.0
 */
public class Resources {

    /**
     * 利用类加载器加载配置文件为二进制流
     * @param xmlPath
     * @return
     */
    public static InputStream getResourceAsStream(String xmlPath){

        return Resources.class.getClassLoader().getResourceAsStream(xmlPath);
    }
}
