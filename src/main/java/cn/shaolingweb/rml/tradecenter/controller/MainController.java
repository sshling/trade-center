package cn.shaolingweb.rml.tradecenter.controller;

import cn.shaolingweb.rml.tradecenter.domain.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller //不是 RestController
public class MainController {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping("/{name}")
    //RequestParam,PathVariable
    String name(@PathVariable String name, Model model) {
        //ModelAndView,ThymeleafView,
        logger.info("请求参数[name={}]", name);
        model.addAttribute("name", name);
        User u1=new User();
        u1.setId(1);
        u1.setName("张三");

        User u2=new User();
        u2.setId(2);
        u2.setName("李四");
        List users = new ArrayList();
        users.add(u1);
        users.add(u2);
        model.addAttribute("users",users);//k=v
        return "/index";
    }


    /*
     原生态,随便跳
     */
    @GetMapping("/baidu")
    public void t1(HttpServletResponse res) throws IOException {
        res.sendRedirect("https://baidu.com");
    }
}
