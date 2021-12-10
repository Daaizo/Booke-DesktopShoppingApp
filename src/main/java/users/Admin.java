package users;

public class Admin extends User{
    private Admin instance = null;
    private Admin(){
        this.userName = "admin";
        this.lastName = "admin";
        this.name = "admin";
        this.password = "admin";
    }
    public Admin createAdmin(){
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
    public boolean isAdminCreated(){
        return instance == null ? false : true;
    }

    @Override
    public void changeData() {

    }
}
