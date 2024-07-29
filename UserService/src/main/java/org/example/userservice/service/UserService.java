package org.example.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.Model.User;
import org.example.userservice.dto.AddUserRequest;
import org.example.userservice.enums.UserType;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.userservice.constant.KafkaConstant.User_Created_Topic;
import static org.example.userservice.constant.UserCreationTopicConst.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaTemplate<String,Object> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user =  userRepository.findByPhoneNo(phoneNo);
        if(user==null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User createUser(AddUserRequest request) {
        User user = UserMapper.MapToUser(request);
        user.setUserType(UserType.USER);
        user.setAuthorities("USER");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        log.info("user create {}",user);
        userRepository.save(user);
        log.info("user saved {}",user);

        //publish data to kafka
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(EMAIL,user.getEmail());
        objectNode.put(NAME,user.getName());
        objectNode.put(PHONENO,user.getPhoneNo());
        objectNode.put(USERID,user.getId());
        String kafkamessage = objectNode.toString();
        kafkaTemplate.send(User_Created_Topic,kafkamessage);
        log.info("message published to kafka {}",kafkamessage);

        return user;
    }
}
