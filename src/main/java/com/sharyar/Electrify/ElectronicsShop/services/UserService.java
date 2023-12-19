package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.UserRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.User;

import java.security.Principal;
import java.util.List;

public interface UserService {

    //create
    UserResponseDto createUser(UserRequestDto userRequestDto);

    //update
    UserResponseDto updateUser(UserRequestDto userRequestDto, String userId);

    //update my account. for logged-in users.
    UserResponseDto updateMyAccount(UserRequestDto userRequestDto);

    UserResponseDto setAnotherUserAsAdmin(String userId);

    void updateUser(User user);

    //getSingleUserById
    UserResponseDto getUserById(String userId);

    //getSingleUserByEmail
    UserResponseDto getUserByEmail(String email);

    //Search User
    List<UserResponseDto> searchUser(String keyword);

    //getAllUsers
    PageableResponse<UserResponseDto> getAllUsers(int pageNumber , int pageSize , String sortBy , String sortDir);

    public User checkUserExists(String userId);
    //delete
    boolean deleteUser(String userId);

    void deleteMyAccount(Principal principal);
}
