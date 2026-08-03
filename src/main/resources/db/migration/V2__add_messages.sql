CREATE TABLE messages (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          sender_id UUID NOT NULL,
                          receiver_id UUID,
                          center_id UUID NOT NULL,
                          content VARCHAR(1000) NOT NULL,
                          sender_role VARCHAR(50) NOT NULL,
                          is_read BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT NOW()
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS fcm_token VARCHAR(500);