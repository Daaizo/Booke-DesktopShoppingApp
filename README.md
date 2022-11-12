<h1 id="start">Brief introduction</h1>
Bokke is a desktop project that mimics a shopping application. It can be run on any computer with an oracle server installed but. Since the database is local, there was no need to share it, but it has all the functionality to do so. This is my biggest project and I have devoted all my free time to it. Running the application on another computer is possible, but it is not that easy. A person must have a database created, all the products and a link to the database in the code, for this reason it is mainly my local project for educational purposes and at the bottom are videos of the use of the application.

<a href="#videoSection"> go to the videos, which shows how the application works </a>

<h1>Features</h1>
All screenshots were taken from an existing account that I used for testing.
<ul>
    <li>
      <h2>Logging</h2> the home page that opens with the application. 
      <img src=https://user-images.githubusercontent.com/84875747/167001170-4f09670b-2e0e-4ed6-8eee-4adca30ad953.png>
   </li>
    <li>
      <h2>Creating account</h2> Any user of app can create accout with uniqe login and password that met requirements.
      <img src=https://user-images.githubusercontent.com/84875747/166999227-a5231518-fff5-4f12-930f-20b0c467d8ae.png>
    </li>
    <li>
      <h2>Home page after successful login</h2>
      <img src=https://user-images.githubusercontent.com/84875747/167003299-80fa9625-f452-455e-85fd-0f04c8f83203.png>
    </li>
    <li>
      <h2>E-books section</h2>
      <img src=https://user-images.githubusercontent.com/84875747/167003703-cd118e29-ba1c-4299-ac2a-641e650975ba.png>
    </li>
    <li>
      <h2>List of all products from shop</h2>
      <img src=https://user-images.githubusercontent.com/84875747/167004149-e2bd89b9-a9d3-42b3-8d33-db99f083d3d5.png>
    </li>
  <li>
      <h2>Shopping cart</h2> 
      <img src=https://user-images.githubusercontent.com/84875747/167004221-240fd885-65a9-44eb-849c-fa8fb6af1868.png>
    </li>
   <li>
      <h2>User profile</h2> 
      <img src=https://user-images.githubusercontent.com/84875747/167004670-ae796000-2833-477d-9a2a-f758ce6ce3e0.png>
    </li>
    <li>
      <h2>Account details</h2> 
       <img src=https://user-images.githubusercontent.com/84875747/167004886-17e6506f-baba-4017-9929-976310ae7b73.png>
    </li>
   <li>
      <h2>List of user's orders</h2> 
      <img src=https://user-images.githubusercontent.com/84875747/167005047-55823546-40ab-407c-94f7-0a2a8bbf3080.png>
    </li>
  <li>
      <h2>List of user's favourites</h2> 
      <img src=https://user-images.githubusercontent.com/84875747/167005571-852ec66b-e46d-44fe-970f-32e6ffd371ad.png>
    </li>
     <li>
      <h2>Admin page</h2> 
      <p> He can manage orders (accept and reject) and basically see everything, including orders of a specific user, all orders, products, users, etc. He can also change a user's password, add a new product, and even cancel an order</p>
      <img src=https://user-images.githubusercontent.com/84875747/167005571-852ec66b-e46d-44fe-970f-32e6ffd371ad.png>
    </li>
     <li>
      <h2>And much more</h2> 
        <a href="#videoSection"> go to the videos, which shows how the application works </a>
    </li>
 </ul>

<h1>Technology used</h1>
<p>
The project is completely my idea, the look and code are built from scratch by me. The idea of the project was to build an application using a real relational database. All code was written in Java using javaFX framework.To write code i used IntelliJ IDEA. The graphical user interface was done using SceneBuilder, but many parts were done without it, just from code. For styling I used java fx CSS, which is very simple to normal CSS, but all properties have -fx at the beginning, for example :-fx-background-color: red; All database stuff was done in Oracle using a free local server and Oracle SQL developer for database management. The data in the application is updated in real time, so any new data entered or changed in the database is visible in the application immediately or after re-entering each section, for example: if a user changes their password or personal information, it will be changed immediately and the user will see it and be able to use it to log in without having to restart the application; if new books are added, the user will see it after going to another section and then returning to that section.All icons I used are free from : www.flaticon.com
and most of them are made by :<a href="https://www.flaticon.com/authors/juicy-fish" title="icons"> juicy_fish - Flaticon</a>
some of them I changed using GIMP, added background, some text or just changed resolution.<p>

 <h1>Database info </h1>
 <p>
 database made in Oracle SQL developer. Used with Oracle local server.
entity-relationship diagram </p>
<img src= https://user-images.githubusercontent.com/84875747/159306732-acc3b33d-9f51-4a5e-94e1-2aaf892c316a.png>

Relational model
<img src= https://user-images.githubusercontent.com/84875747/159305512-baec01bc-53e0-4540-ba61-279801a83d97.png>

<h1 id="videoSection"> Videos </h1>
<ul>
   <li>
   <p>Login with bad data and account creation.
   
https://user-images.githubusercontent.com/84875747/177515013-718b4710-425b-4216-ba9b-8a9a763b9ae2.mp4

</p>
    </li>
    <li>
   <p> Account usage


https://user-images.githubusercontent.com/84875747/177517663-56807c98-b871-43ee-9053-8de07d02910c.mp4


</p>
    </li>
     <li>
   <p> Account management

https://user-images.githubusercontent.com/84875747/201477412-a5fb1807-731e-4e62-b47b-e47d9836e28c.mp4


</p>
    </li>
      <li>
   <p>Admin - product management

https://user-images.githubusercontent.com/84875747/177519363-818868a6-61b3-4fd6-a427-17cb8c582b30.mp4

</p>
    </li>
       <li>
   <p>Admin - users and orders management


https://user-images.githubusercontent.com/84875747/177519893-e8c6910c-9346-407f-a188-f203e3006253.mp4


</p>
    </li>
 </ul>


  <a href="#start">back to the top </a> 
