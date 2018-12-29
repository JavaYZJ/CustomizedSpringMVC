/**
 * Copyright (C), 2018, 杨智杰
 * FileName: TestController
 * Author:   猪猪
 * Date:     2018/12/29 15:15
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */

package cn.yzj.SpringMVC.Controller;

import cn.yzj.SpringMVC.Annotation.MyController;
import cn.yzj.SpringMVC.Annotation.MyRequestMapping;

/**
 * 〈功能简述〉<br> 
 * 〈自定义springmvc框架测试类〉
 *
 * @author 猪猪
 * @create 2018/12/29
 * @since 1.0.0
 */

@MyController
@MyRequestMapping("/Test")
public class TestController {

    @MyRequestMapping("/index")
    public String springMVCTest(){
        System.out.println("我是手写springMVC框架实现的......");
        return "index";
    }
}
