package users;

public class Admin extends User{
    private static Admin instance = null;
    private Admin(){
        this.lastName = "admin";
        this.name = "admin";
        this.password = "admin";
        this.userName = "admin";
    }
    public static Admin createAdmin(){
        try {
            if (instance == null) {
                instance = new Admin();
            } else {
                System.out.println("admin already exists");
            }
            return instance;

        }
        catch( Exception e ){
            System.out.println(e.getMessage() + "problem with admin creation");
        }
        return null;
    }


    @Override
    public void changeData() {

    }
}
