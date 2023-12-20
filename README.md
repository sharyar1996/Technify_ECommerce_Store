<h1>TECHNIFY ECommerce Electronics Store</h1>

<img height = '100px' src = "https://discovertemplate.com/wp-content/uploads/2020/12/DT_G65_Electronic-Animated-GIF-Icon-Pack-2.gif">
<p> An E-Commerce online application based on real world Electronics store. This is a backend web application using Springboot and other Spring modules for tackling different challenges associated with server-side programming. It solves all the problems of modern E-Commerce web application. </p>

 <h2>ABOUT PROJECT</h2>
 
 <h3>Users / Admins:</h3>
 <li>This application allows users from Pakistan to register/login.</li> 
 <li>Admins can create or modify everything in DataBase (product, user, category, sub categories, images saved on server)</li>
 <li> JWT authentication and authorizations based on roles are implemented so no user can access the data of another user or modify any resources in the database except for his own 
  details like password or billing address,etc.</li>
 <li>Validation constraints are applied on every user request object which contains DTOs to ensure no user / admin can
 pass invalid data.</li>
 <li> User's passwords are encoded using BCryptPasswordEncoder.
 <li>User can add multiple products to the shopping cart and delete products from the shopping cart and can view his  orders.</li>

 <h3>Category / Sub-category</h3>
 <li>A category can only have sub categories if it directly doesn't contains products.</li>
 <li>Similarly no category can have products if it is a parent category of any sub-category</li>
 <li>Each category can have its 1 image saved on the server which will be deleted once the category is deleted.</li>
 <li>If the admin chooses to delete a category, then  all of its own products
  (if no sub-categories) will be deleted.</li> 
  <li>If the category has sub-caegories, then those sub categories and all of their products along with their images saved on the 
   server will also be deleted based on a <b>RECURSIVE</b> funtion I implemented. (NOTE: Firstly I used orphanRemoval = true 
  but that didn't delete the files saved on the server hence I switched to my custom recursive method.</li>
  
 <h3>Products</h3>
 <li>Users can browse and search for products based on their price,category,brand and many other criterias with pagination and sorting.(See the APIs for
 more details)</li>
 <li>A user can <b>RATE A PRODUCT</b> only if he has bought that product before from this store. </li>
 <li>A maximum of 4 images are allowed per product</li>
 <li>If any admin adds images to either category or products , then all those images should be in one these formats: <br>
     'image/jpg' , 'image/jpeg' , 'image/png' , 'image/webp' , 'image/gif' .</li>

 <h2>USED TECHNOLOGIES:</h2>
 <ul>
   <li>SpringBoot</li>
   <li>Spring Data JPA / Hibernate </li>
   <li>Spring MVC </li>
   <li>JPQL </li>
   <li>MySQL</li>
   <li>Spring Security / JWT </li>
   <li>Swagger / OpenAPI Documentation </li>
   <li>Object Oriented Programming (OOP concepts) </li>
   <li>Maven</li>
 </ul>

  <h2> DOCUMENTATION </h2>
 <p> For complete documentation of the project and all of its APIs , I used OpenAPI documentation(previously SWAGGER).
 You have to download the source code , run the project and then visit :</p>
 <p><a href = "http://localhost:9090/swagger-ui/index.html"> Technify Project Documentation</p>
 <h2>FUTURE UPDATES</h2>
 <p>In coming days , I will add complete secure payment options for this project and integrate GPS based location & 
 shipment services.</p>
 
 
