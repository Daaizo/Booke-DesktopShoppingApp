package users;
abstract class User {
    protected String userName ;
    protected String password;
    protected String name ;
    protected String lastName;

    public void setUserName(String un){
        this.userName = un;
    }
    public void setPassword(String pass){
        this.password = pass;
    }
    public void setName(String n){
        this.name = n;
    }
    public void setLastName(String ln){
        this.lastName = ln;
    }
    public String getUserName(){
        return this.userName;
    }
    public String getPassword(){
        return this.password;
    }
    public String getName(){
        return this.name;
    }
    public String getLastName(){
        return  this.lastName;
    }

    public abstract void changeData();

}
