package org.example.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.example.walletservice.Model.Wallet;
import org.example.walletservice.Repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.example.walletservice.constant.KafkaConstant.User_Created_Topic;
import static org.example.walletservice.constant.UserCreationTopicConst.PHONENO;
import static org.example.walletservice.constant.UserCreationTopicConst.USERID;

@Slf4j
@Service
public class UserCreationConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    WalletRepository walletRepository;

    @Value("${wallet.initial.amount}")
    private Double balance;

    @KafkaListener(topics = User_Created_Topic, groupId = "wallet-group")
    public void userCreated(String message) throws JsonProcessingException {
        log.info("User created message received: {}", message);
        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String phoneno = node.get(PHONENO).textValue();
        Integer userid = node.get(USERID).intValue();

        Wallet wallet = Wallet.builder()
                .phoneNo(phoneno)
                .userId(userid)
                .balance(balance)
                .build();

        walletRepository.save(wallet);

        log.info("wallet saved for user {}", userid);
    }



}
