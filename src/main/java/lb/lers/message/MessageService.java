package lb.lers.message;

import lb.lers.FirebaseMessagingService;
import lb.lers.user.User;
import lb.lers.user.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FirebaseMessagingService fcmService;

    public MessageService(MessageRepository messageRepository,
                          UserRepository userRepository,
                          FirebaseMessagingService fcmService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    public Message sendMessage(UUID senderId, String senderRole, UUID centerId, String content, UUID receiverId) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderRole(senderRole);
        message.setCenterId(centerId);
        message.setContent(content);
        if (receiverId != null) message.setReceiverId(receiverId);
        // Set sender name
        userRepository.findById(senderId).ifPresent(u -> message.setSenderName(u.getFullName()));
        messageRepository.save(message);

        if (senderRole.equals("CITIZEN")) {
            userRepository.findAll().stream()
                    .filter(u -> u.getRole().equals("CENTER_ADMIN") && centerId.equals(u.getCenterId()))
                    .findFirst()
                    .ifPresent(admin -> fcmService.sendNotification(
                            admin.getFcmToken(), "New Message", "A citizen sent you a message"
                    ));
        } else {
            if (receiverId != null) {
                userRepository.findById(receiverId).ifPresent(citizen ->
                        fcmService.sendNotification(
                                citizen.getFcmToken(), "New Message from Center", content
                        ));
            }
        }
        return message;
    }

    public List<Message> getConversation(UUID centerId, UUID citizenId) {
        return messageRepository.findConversation(centerId, citizenId);
    }

    public List<Message> getCenterMessages(UUID centerId) {
        return messageRepository.findByCenterIdOrderByCreatedAtAsc(centerId)
                .stream()
                .filter(m -> !Boolean.TRUE.equals(m.getDeletedForCenter()))
                .collect(java.util.stream.Collectors.toList());
    }

    public void deleteConversation(UUID centerId, UUID citizenId) {
        List<Message> msgs = messageRepository.findConversation(centerId, citizenId);
        msgs.forEach(m -> {
            m.setDeletedForCenter(true);
            messageRepository.save(m);
        });
    }

    public void markRead(UUID messageId) {
        messageRepository.findById(messageId).ifPresent(m -> {
            m.setRead(true);
            messageRepository.save(m);
        });
    }
}