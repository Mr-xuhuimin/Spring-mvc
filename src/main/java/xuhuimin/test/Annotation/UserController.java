package xuhuimin.test.Annotation;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static List<User> userList = new ArrayList<>();

    static {
        userList.add(new User(1, "Jim"));
        userList.add(new User(2, "Lily"));
    }

    @MyRequestMapping(value = "/get")
    public String get(int id) {
        String name=null;
        for (User user:userList){
            if (user.id==id){
                name= user.name;
            }
        }
        return name;
    }


    @MyRequestMapping("/getAll")
    public String getAll() {
        String msg="";
        for (User user:userList){
            msg+= user.name+":"+user.id+";";
        }
        System.out.println("ff");
        return msg;
    }

}