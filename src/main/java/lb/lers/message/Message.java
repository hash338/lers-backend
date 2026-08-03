package lb.lers.message;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id")
    private UUID receiverId;

    @Column(name = "center_id", nullable = false)
    private UUID centerId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "sender_role", nullable = false)
    private String senderRole;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "deleted_for_center")
    private Boolean deletedForCenter = false;

    @Column(name = "sender_name")
    private String senderName;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() { return id; }
    public UUID getSenderId() { return senderId; }
    public void setSenderId(UUID senderId) { this.senderId = senderId; }
    public UUID getReceiverId() { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }
    public UUID getCenterId() { return centerId; }
    public void setCenterId(UUID centerId) { this.centerId = centerId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public Instant getCreatedAt() { return createdAt; }
    public Boolean getDeletedForCenter() { return deletedForCenter; }
    public void setDeletedForCenter(Boolean deletedForCenter) { this.deletedForCenter = deletedForCenter;}
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName;}
    }