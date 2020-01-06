package xuhuimin.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringTest {
    @RequestMapping("/")
    public String hello(){

    System.out.println("你好！！！！");
    return "11";
}

}
