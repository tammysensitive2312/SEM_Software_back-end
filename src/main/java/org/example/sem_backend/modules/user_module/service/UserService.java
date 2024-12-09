package org.example.sem_backend.modules.user_module.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sem_backend.common_module.exception.ResourceNotFoundException;
import org.example.sem_backend.main_service.middleware.logging.LoggingFilter;
import org.example.sem_backend.modules.user_module.domain.dto.UpdateUserRequest;
import org.example.sem_backend.modules.user_module.domain.dto.UserDto;
import org.example.sem_backend.modules.user_module.domain.entity.User;
import org.example.sem_backend.modules.user_module.domain.mapper.UserMapper;
import org.example.sem_backend.modules.user_module.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private static final Logger logger = LogManager.getLogger(LoggingFilter.class);
    /**
     * @return class UserDto
     */
    @Override
    public UserDto getUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException
                        ("không tìm thấy user với id:" + userId, "USER-MODULE"));
        //logger.info("Username retrieved: {}" + user.getUsername());
        return userMapper.toDto(user);
    }

    /**
     * @param userDto
     * @return
     */
    @Override
    public void addNewUser(UserDto userDto) {
        User userEntity = userMapper.toEntity(userDto);
        Random random = new Random();
        userEntity.setPassword(String.valueOf(random.nextInt(20) + 1));
        try {
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new RuntimeException("error while saving the user with error" + e);
        }
    }

    /**
     * @param userDto, userId
     * @return
     */
    @Transactional
    @Override
    public void updateUserInfo(Long userId, UpdateUserRequest userDto) {
        User userEntity = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user với id: " + userId, "USER-MODULE"));

        try {
            userMapper.partialUpdate(userDto, userEntity);
            userRepository.save(userEntity);
        } catch (Exception e) {
            logger.error("Error while saving the user: ", e);
            throw new RuntimeException("Error while saving the user: " + e.getMessage(), e);
        }
    }

}
