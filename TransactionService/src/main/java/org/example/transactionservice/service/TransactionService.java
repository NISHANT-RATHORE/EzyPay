package org.example.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.example.transactionservice.Model.Transaction;
import org.example.transactionservice.client.UserServiceClient;
import org.example.transactionservice.dto.InitiateTrxnRequest;
import org.example.transactionservice.enums.TransactionStatus;
import org.example.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import static org.example.transactionservice.constant.KafkaConstant.Transaction_Initiated_Topic;
import static org.example.transactionservice.constant.TransactionInitiatedConstant.*;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        String auth = "txn-service:txn-service";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authValue = "Basic "+ new String(encodedAuth);

        ObjectNode node = userServiceClient.getUser(phoneNo,authValue);
        log.info("user fetched: {}", node);

        if (node == null) {
            throw new UsernameNotFoundException("user does not exist");
        }

        ArrayNode authorities = (ArrayNode) node.get("authorities");

        final List<GrantedAuthority> authorityList = new ArrayList<>();

        authorities.iterator().forEachRemaining(jsonNode -> {
            authorityList.add(new SimpleGrantedAuthority(jsonNode.get("authority").textValue()));
        });

        User user = new User(node.get("phoneNo").textValue(), node.get("password").textValue(), authorityList);
        return user;
    }

    public String initiateTransaction(String senderPhoneno,InitiateTrxnRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderPhoneNo(senderPhoneno)
                .receiverPhoneNo(request.getReceiverPhoneNo())
                .amount(request.getAmount())
                .status(TransactionStatus.INITIATED)
                .purpose(request.getPurpose())
                .build();
        transactionRepository.save(transaction);
        log.info("transaction saved");
        // send message to kafka
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(SENDERPHONENO,transaction.getSenderPhoneNo());
        objectNode.put(RECIEVERPHONENO,transaction.getReceiverPhoneNo());
        objectNode.put(AMOUNT,transaction.getAmount());
        objectNode.put(TRANSACTIONID,transaction.getTransactionId());
        String kafkamessage = objectNode.toString();
        kafkaTemplate.send(Transaction_Initiated_Topic,kafkamessage);
        log.info("message published to kafka {}",kafkamessage);

        return transaction.getTransactionId();
    }
}
