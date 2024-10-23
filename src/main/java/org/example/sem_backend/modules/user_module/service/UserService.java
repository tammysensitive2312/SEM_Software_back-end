package org.example.sem_backend.modules.user_module.service;

import lombok.RequiredArgsConstructor;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.domain.mapper.UserMapper;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    /**
     * @param userId
     * @return class UserDto
     */
    @Override
    public UserDto getUserById(Long userId) throws ResourceNotFoundException{
        Optional<User> userEntity = userRepository.findById(userId);
        UserDto userDto = userMapper.toDto(userEntity.orElseThrow(() -> new ResourceNotFoundException("không tìm thấy user với id:" + userId, "USER-MODULE")));
        return userDto;
    }

    /**
     * @param user
     * @return
     */
    @Override
    public Boolean addNewUser(UserDto user) {
        return null;
    }

    /**
     * @param user
     * @return
     */
    @Override
    public Boolean updateUserInfo(UserDto user) {
        return null;
    }
}
