package lb.lers.hospital;

import lombok.Data;

@Data
public class RoomRequest {
    private String roomNumber;
    private RoomType type;
}
