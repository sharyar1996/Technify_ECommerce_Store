package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.dto.ApiResponseMessage;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.UserRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.services.FileService;
import com.sharyar.Electrify.ElectronicsShop.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private FileService fileService;
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    //create
      @PostMapping("/createUser")
      @Operation(
              summary = "Create a new user account by sending UserRequestDto object as " +
                      "parameter. " ,
              description = "This method only requires userName, email and password. It " +
                      "doesn't set the Billing address of " +
                      " the user. For setting the billing address related details of " +
                      "this user  , use API endpoint 'orders/setBillingAddress/ ." +
                      "Currently , this user's cart will be null but" +
                      " but the cart will be automatically created once the user " +
                      "first time hits the API endpoint '/cart/addToCart' i.e " +
                      "first time adds something to the cart "
      )
      public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserRequestDto
                                                                      userRequestDto)
      {
          UserResponseDto savedUserResponseDto = userService.createUser(userRequestDto);
//          logger.info("savedUserDto's roles size in controller = {}" , savedUserDto.getRoles().size());
          return new ResponseEntity<>(savedUserResponseDto, HttpStatus.CREATED);
      }

    //update
      @PutMapping("/{userId}")
      @PreAuthorize("hasRole('ADMIN')")
      @Operation(
              summary = "Update an existing user by sending its Id as a " +
                      "parameter.(ONLY FOR ADMINS)",
              description = "This method doesn't update the Billing address of " +
                      " the user. For updating billing address , " +
                      "use the API endpoint '/orders/setBillingAddress' If the user doesn't exist , then a Runtime exception " +
                      " will be thrown"
      )
      public ResponseEntity<UserResponseDto> updateUser(
              @RequestBody @Valid UserRequestDto userRequestDto,
              @PathVariable("userId") String userId)
      {
           UserResponseDto updatedUserResponseDto = userService.updateUser(userRequestDto, userId );
           return  new ResponseEntity<>(updatedUserResponseDto, HttpStatus.OK);
      }

      @PutMapping("/updateMyAccount")
      @Operation(
              summary = "Only For currently logged-in user. It updates the currently logged in account",
              description = "This method doesn't update the Billing address of " +
                      " the user. For updating billing address related details" +
                      " of the user, " +
                      "use the API endpoint '/orders/setBillingAddress'."
      )
      public ResponseEntity<UserResponseDto> updateMyAccount(
              @RequestBody @Valid UserRequestDto userRequestDto)
      {
           UserResponseDto updatedUserResponseDto = userService.updateMyAccount(userRequestDto);
           return  new ResponseEntity<>(updatedUserResponseDto, HttpStatus.OK);
      }

     // Only for admins
     @PutMapping("/setAsAdmin")
     @PreAuthorize("hasRole('ADMIN')")
     @Operation(
             summary = "Set an existing user as an Admin by sending the user's Id as a " +
                     "parameter.(ONLY FOR ADMINS)",
             description = "If the user doesn't exist , then a Runtime exception " +
                     " will be thrown"
     )
     public ResponseEntity<UserResponseDto> setAnotherUserAsAdmin(@RequestParam String userId)
     {
        UserResponseDto userResponseDto = userService.setAnotherUserAsAdmin(userId);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
     }

      //get Single User By Id
    @GetMapping(value = "/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get any existing user details by sending its Id as a " +
                    "parameter.(ONLY FOR ADMINS)",
            description = "If the user doesn't exist , then a Runtime exception " +
                    " will be thrown"
    )
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId)
    {
        return new ResponseEntity<>(userService.getUserById(userId) , HttpStatus.OK);
    }

    //getAll
    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all existing uses.(ONLY FOR ADMINS)",
            description = "PageNumber , page size , " +
                    "sortBy('asc' for ascending or 'desc' for descending order ) " +
                    "are required as a parameter." +
                    " If the user doesn't exist , then a Runtime exception " +
                    " will be thrown"
    )
    public ResponseEntity<PageableResponse<UserResponseDto>> getAllUsers(
            @RequestParam(value="pageNumber" , defaultValue = "1" , required = false) int pageNumber,
            @RequestParam(value = "pageSize" , defaultValue = "10" , required = false) int pageSize,
            @RequestParam(value="sortBy" , defaultValue = "userName" , required = false) String sortBy,
            @RequestParam(value = "sortDir" , defaultValue = "asc" , required = false) String sortDir
    )
    {
        PageableResponse<UserResponseDto> userDtoPageable = userService.getAllUsers( pageNumber ,  pageSize , sortBy , sortDir);

        return new ResponseEntity<>(userDtoPageable,HttpStatus.OK );
    }

    //getByEmail
    @GetMapping("/email/{userEmail}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get an existing user by sending the user's email as a " +
                    "parameter.(ONLY FOR ADMINS)",
            description = "If the user doesn't exist , then a Runtime exception " +
                    " will be thrown"
    )
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String userEmail)
    {
        return new ResponseEntity<>(userService.getUserByEmail(userEmail) , HttpStatus.OK);
    }


    //searchUserByKeyword

    @GetMapping("/search/{keyword}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Search an existing user by sending any keyword related to the " +
                    "user details as a parameter.(ONLY FOR ADMINS)",
            description = "If the user doesn't exist , then a Runtime exception " +
                    " will be thrown"
    )
    public ResponseEntity<List<UserResponseDto>> searchUserByKeyword(@PathVariable String keyword)
    {
        return new ResponseEntity<>(userService.searchUser(keyword) , HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete an existing user by sending its Id as a " +
                    "parameter.(ONLY FOR ADMINS)",
            description = "All the orderhistory along with its billing address" +
                    " of the user will also " +
                    " be deleted. If the user doesn't exist , then a Runtime exception " +
                    " will be thrown"
    )
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId)
    {
          ApiResponseMessage message = null;
          if(userService.deleteUser(userId) )
          {
               message = ApiResponseMessage.builder()
                      .message("User with id :" + userId +
                              "is successfully deleted.")
                      .success(true)
                      .status(HttpStatus.OK)
                      .build();
          }
          else {
              message = ApiResponseMessage.builder()
                      .message("User with id: "+ userId +
                              "cannot be deleted.")
                      .success(false)
                      .status(HttpStatus.NOT_FOUND)
                      .build();
          }
           return  new ResponseEntity<>( message , message.getStatus());
    }

    @DeleteMapping("/deleteMyAccount")
    @Operation(
            summary = "Only For currently logged-in user. It deletes the currently logged-in" +
                    " user",
            description = "All the orderhistory along with its billing address" +
                    " of the user will also be deleted."
    )
    public ResponseEntity<ApiResponseMessage> deleteMyAccount(Principal principal)
    {
        userService.deleteMyAccount(principal);
        ApiResponseMessage message = null;
        message = ApiResponseMessage.builder()
                .message("User is successfully deleted.")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return  new ResponseEntity<>( message , message.getStatus());
    }


    //user upload image
//    @PostMapping(value = "/uploadImage/{userId}")
//    public ResponseEntity<ImageResponse> uploadUserImage(
//           @ImageNameValid @RequestParam("userImage")MultipartFile image,
//            @PathVariable("userId") String userId
//            ) throws IOException
//    {
//        UserDto userDto =  userService.getUserById(userId);
//        List<MultipartFile> files = new ArrayList<>();
//        files.add(image);
//        String pathName = imageUploadPath + userDto.getUserId() + "\\";
//        List<String> paths = fileService.uploadFile( pathName ,  files );
//        userService.updateUser(userDto , userId);
//
//       ImageResponse imageResponse = ImageResponse.builder()
//               .imageName(paths.get(0))
//               .success(true)
//               .status(HttpStatus.CREATED)
//               .message("Your image has been uploaded")
//               .build();
//
//
//       return new ResponseEntity<>(imageResponse , HttpStatus.CREATED);
//
//    }


    //serve user Image
//    @GetMapping(value="/getUserImage/{userId}")
//    public void serveImage(@PathVariable("userId") String userId , HttpServletResponse response)
//    {
//        UserDto userDto = userService.getUserById(userId);
//        String userImageName = userDto.getUserImageName();
//        logger.info("user Image name = {} " , userImageName );
//        response.setContentType(String.valueOf(MediaType.IMAGE_JPEG));
//        InputStream resourceStream = fileService.getResource( imageUploadPath  , userImageName );
//        try
//        {
//            StreamUtils.copy(resourceStream , response.getOutputStream() );
//        }
//        catch (IOException e)
//        {
//            logger.error("IOException occured in serveImage() in UserController:{}"
//                           , e.getMessage());
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

}
