package org.example.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.walletservice.Model.Wallet;
import org.example.walletservice.Repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static org.example.walletservice.constant.KafkaConstant.Transaction_Initiated_Topic;
import static org.example.walletservice.constant.TransactionInitiatedConstant.*;

@Slf4j
@Service
public class TransactionInitiatedConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = Transaction_Initiated_Topic,groupId = "wallet-group")
    public void transactionInitiated(String message) throws JsonProcessingException {
        log.info("Transaction initiated message received: {}", message);
        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String senderPhoneNo = node.get(SENDERPHONENO).textValue();
        String recieverPhoneNo = node.get(RECIEVERPHONENO).textValue();
        Double amount = node.get(AMOUNT).doubleValue();

        Wallet senderWallet = walletRepository.findByPhoneNo(senderPhoneNo);
        Wallet receiverWallet = walletRepository.findByPhoneNo(recieverPhoneNo);

        String status;
        String statusMessage;

        if (senderWallet == null) {
            log.info("sender wallet is not present");
            status = "FAILED";
            statusMessage = "Sender wallet does not exist in our system";
        } else if (receiverWallet == null) {
            log.info("receiver wallet is not present");
            status = "FAILED";
            statusMessage = "Receiver wallet does not exist in our system";
        } else if (amount > senderWallet.getBalance()) {
            log.info("Amount sent is greater");
            status = "FAILED";
            statusMessage = "Amount sent is greater than the amount in the sender's wallet";
        } else{ //successs
            log.info("Successful");
            status = "SUCCESSFUL";
            statusMessage = "Transaction is successful";
            updateWallets(senderWallet, receiverWallet, amount);
            log.info("Wallet updated");
            //publish message to kafka

        }
    }
    @Transactional
    public void updateWallets(Wallet senderWallet,
                              Wallet receiverWallet,
                              Double amount) {
        walletRepository.updateWallet(senderWallet.getPhoneNo(), -amount);
        walletRepository.updateWallet(receiverWallet.getPhoneNo(), amount);

    }

}
