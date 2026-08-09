package com.castlebird.blog.user.mapper;

import com.castlebird.blog.user.dto.request.CreateAdminRequest;
import com.castlebird.blog.user.dto.response.UserResponse;
import com.castlebird.blog.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  UserResponse toUserResponse(User user);

  User toUserFromAdminRequest(CreateAdminRequest adminRequest);
}
