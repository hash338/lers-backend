-- Add hospital_id to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS hospital_id BIGINT REFERENCES hospitals(id);

-- Add admin_email and admin_user_id to hospitals
ALTER TABLE hospitals ADD COLUMN IF NOT EXISTS admin_email VARCHAR(255);
ALTER TABLE hospitals ADD COLUMN IF NOT EXISTS admin_user_id UUID REFERENCES users(id);
