package com.biz.credit.controller;

import org.springframework.stereotype.Controller;

@Controller
public class CommonController {
    /**
     * 首页访问配置
     * http://localhost:8762/report/
     * http://localhost:8762/report/index
     * http://localhost:8762/report/index.html
     * 用户访问以上3个路径都会跳转到http://localhost:8762/report/dist/index.html#/
     * @return
     */
//   @GetMapping({"/","/index"})
//    public String index() {
//        return "redirect:/index.html";
//    }

    /**
     * 404页面
     * resources/credit-paper/dist/error_404.html
     * @return
     */
//    @GetMapping(value = "/error/404")
//    public String error_404() {
//        return "redirect:/error_404.html";
//    }

    /**
     * 500页面
     * resources/credit-paper/dist/error_500.html
     * @return
     */
//    @GetMapping(value = "/error/500")
//    public String indexHtml() {
//        return "redirect:/error_500.html";
//    }
}
