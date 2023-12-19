package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.config.GetPrincipal;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.UserRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.Role;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import com.sharyar.Electrify.ElectronicsShop.exceptions.DuplicateValueException;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.helpers.Helper;
import com.sharyar.Electrify.ElectronicsShop.repositories.RoleRepository;
import com.sharyar.Electrify.ElectronicsShop.repositories.UserRepository;
import com.sharyar.Electrify.ElectronicsShop.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);
    @Value("${role.user.id}")
    private String userRoleId;
    @Value(("${role.admin.id}"))
    private String adminRoleId;
    @Autowired
    private GetPrincipal getPrincipal;

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        // manually set Id
        String userId = UUID.randomUUID().toString();
        // encode password
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        //dto->entity
        User userEntity = modelMapper.map(userRequestDto , User.class);
        User checkUser = userRepository.findByEmail(userEntity.getEmail()).orElse(null);
       if( checkUser == null)
       {
           userEntity.setUserId(userId);
           userEntity.setPassword(password);
           Role userRole = roleRepository.findById(userRoleId).orElse(null);
           logger.info("User role is null or not = {}" , userRole );
           userEntity.getRoles().add(userRole);
           User savedUser = userRepository.save(userEntity);
           //entity->dto
           logger.info("savedUser.getRoles = {} " ,savedUser.getRoles());
           UserResponseDto newDto = modelMapper.map(savedUser , UserResponseDto.class);

           return newDto;
       }

       throw new DuplicateValueException("A user already exists with this email.", HttpStatus.BAD_REQUEST);
    }

    @Override
    public UserResponseDto updateUser(UserRequestDto userRequestDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("No User exists with given id : " + userId));
        user.setUserName(userRequestDto.getUserName());
        user.setPassword( passwordEncoder.encode(userRequestDto.getPassword()) );
        //save data to database
        User updatedUser = userRepository.save(user);
        UserResponseDto updatedUserResponseDto = modelMapper.map(updatedUser, UserResponseDto.class);

        return updatedUserResponseDto;
    }
    public UserResponseDto updateMyAccount(UserRequestDto userRequestDto)
    {
        Principal principal = getPrincipal.getPrincipal();
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        user.setUserName(userRequestDto.getUserName());
        user.setEmail(userRequestDto.getEmail());
        String newPassword = passwordEncoder.encode(userRequestDto.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);

        return modelMapper.map(user, UserResponseDto.class);
    }

    @Override
    public void updateUser(User user)
    {
        userRepository.save(user);
    }

    @Override
    public UserResponseDto getUserById(String userId) {

       User user= userRepository.findById(userId).orElseThrow(()->
               new ResourceNotFoundException("No user exists with given Id!"));
           UserResponseDto userResponseDto = modelMapper.map(user , UserResponseDto.class);

           return userResponseDto;
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
       User user = userRepository.findByEmail(email).orElseThrow(()->
                new ResourceNotFoundException("No user exists with given Email!"));
       UserResponseDto userResponseDto = modelMapper.map(user , UserResponseDto.class);

       return userResponseDto;
    }

    @Override
    public List<UserResponseDto> searchUser(String keyword) {

        List<User> users = userRepository.findByUserNameContaining(keyword);
       List<UserResponseDto> userResponseDtoList =  users.stream().map(
               user->{
                   return modelMapper.map(user , UserResponseDto.class);
        }).collect(Collectors.toList());

        return userResponseDtoList;
    }

    @Override
    public PageableResponse<UserResponseDto> getAllUsers(int pageNumber , int pageSize , String sortBy , String sortDir) {

        //NOW FOR SORTING:
        Sort sort = (sortDir.equalsIgnoreCase("desc"))?
                (Sort.by(sortBy).descending()):(Sort.by(sortBy));

        //pageNumber default starts with 0
        Pageable pageableObj = PageRequest.of(pageNumber-1,pageSize ,sort);
        logger.info("before userRepository.findAll() call.");
        Page<User> pageOfUsers = userRepository.findAll(pageableObj);
        logger.info("after userRepository.findAll() call.");

        return Helper.getPageableResponse(pageOfUsers, UserResponseDto.class);
    }

    @Override
    public UserResponseDto setAnotherUserAsAdmin(String userId)
    {
        User user= checkUserExists(userId);
        Role adminRole = roleRepository.findById(adminRoleId).orElse(null);
        user.getRoles().add(adminRole);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser , UserResponseDto.class);
    }

    @Override
    public boolean  deleteUser(String userId) {

        User user = userRepository.findById(userId).orElseThrow(()->
               new ResourceNotFoundException("Can't delete!No user exists with given id : " + userId));
        user.setRoles(null);
        //       if( this.deleteFileFromServer(user)){
//           userRepository.delete(user);
//           return true;
        userRepository.delete(user);
       return true;
    }

    @Override
    public void deleteMyAccount(Principal principal)
    {
         User user = (User) userDetailsService.loadUserByUsername(principal.getName());
         user.setRoles(null);

         userRepository.delete(user);
    }

//    private boolean deleteFileFromServer(User user)
//    {
//        String userImageName = user.getUserImageName();
//        String completePath = imageUploadPath + userImageName;
//        Path path = Path.of(completePath);
//        try{
//            boolean deletedFromServer = Files.deleteIfExists(path);
//            if(deletedFromServer){
//                logger.info("file on path {} deleted from server." , completePath);
//                return  true;
//            }
//            else{
//                logger.info("Can't delete!File not found on the server.It doesnt exist");
//                return false;
//            }
//        }
//        catch (java.io.IOException e)
//        {
//            logger.info("IOException occured . File could not be deleted from the server.");
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    public User checkUserExists(String userId)
    {
        User user = userRepository.findById(userId).orElseThrow(()->
                new ResourceNotFoundException("No User exists with given id : " + userId));
        return user;
    }

}
