-- USERS
CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email         VARCHAR(255) UNIQUE,
                       phone         VARCHAR(30) UNIQUE,
                       password      VARCHAR(255) NOT NULL,
                       role          VARCHAR(25) NOT NULL,
                       full_name     VARCHAR(100),
                       photo_url     VARCHAR(500),
                       center_id     UUID,
                       language      VARCHAR(5)  DEFAULT 'ENG',
                       theme         VARCHAR(10) DEFAULT 'SYSTEM',
                       status        VARCHAR(20) DEFAULT 'active',
                       created_by    UUID,
                       created_at    TIMESTAMP DEFAULT NOW(),
                       updated_at    TIMESTAMP DEFAULT NOW()
);

-- CENTERS
CREATE TABLE centers (
                         id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         code        VARCHAR(10) UNIQUE NOT NULL,
                         name        VARCHAR(150) NOT NULL,
                         type        VARCHAR(20) NOT NULL,
                         region      VARCHAR(80),
                         address     VARCHAR(255),
                         phone       VARCHAR(30),
                         latitude    DECIMAL(9,6) NOT NULL,
                         longitude   DECIMAL(9,6) NOT NULL,
                         admin_id    UUID,
                         status      VARCHAR(20) DEFAULT 'active',
                         created_at  TIMESTAMP DEFAULT NOW()
);

-- VEHICLES
CREATE TABLE vehicles (
                          id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          code          VARCHAR(20) UNIQUE NOT NULL,
                          plate         VARCHAR(30) UNIQUE NOT NULL,
                          type          VARCHAR(40),
                          model         VARCHAR(100),
                          center_id     UUID,
                          status        VARCHAR(25) DEFAULT 'available',
                          mileage       INT DEFAULT 0,
                          last_service  DATE,
                          created_by    UUID,
                          created_at    TIMESTAMP DEFAULT NOW()
);

-- EMPLOYEE STATUS
CREATE TABLE employee_status (
                                 user_id       UUID PRIMARY KEY,
                                 duty_status   VARCHAR(20) DEFAULT 'off-duty',
                                 vehicle_id    UUID,
                                 shift         VARCHAR(80),
                                 updated_at    TIMESTAMP DEFAULT NOW()
);

-- INCIDENTS
CREATE TABLE incidents (
                           id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           code                VARCHAR(30) UNIQUE NOT NULL,
                           type                VARCHAR(30) NOT NULL,
                           priority            VARCHAR(10) DEFAULT 'HIGH',
                           status              VARCHAR(25) NOT NULL DEFAULT 'NEW',
                           location_text       VARCHAR(255),
                           latitude            DECIMAL(9,6) NOT NULL,
                           longitude           DECIMAL(9,6) NOT NULL,
                           photo_url           VARCHAR(500),
                           transfer_from       VARCHAR(255),
                           transfer_to         VARCHAR(255),
                           transfer_floor      VARCHAR(20),
                           citizen_id          UUID,
                           center_id           UUID,
                           dispatcher_id       UUID,
                           vehicle_id          UUID,
                           driver_id           UUID,
                           paramedic_id        UUID,
                           helper_id           UUID,
                           driver_response     VARCHAR(10),
                           paramedic_response  VARCHAR(10),
                           helper_response     VARCHAR(10),
                           ai_summary          TEXT,
                           ai_risk_level       VARCHAR(10),
                           backup_triggered    BOOLEAN DEFAULT FALSE,
                           backup_roles        VARCHAR(100),
                           backup_center_id    UUID,
                           backup_at           TIMESTAMP,
                           reported_at         TIMESTAMP DEFAULT NOW(),
                           dispatched_at       TIMESTAMP,
                           accepted_at         TIMESTAMP,
                           arrived_at          TIMESTAMP,
                           closed_at           TIMESTAMP
);

-- INCIDENT LOG
CREATE TABLE incident_log (
                              id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              incident_id  UUID,
                              actor_id     UUID,
                              action       VARCHAR(50) NOT NULL,
                              detail       TEXT,
                              created_at   TIMESTAMP DEFAULT NOW()
);

-- EMERGENCY NUMBERS
CREATE TABLE emergency_numbers (
                                   id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   name        VARCHAR(100) NOT NULL,
                                   number      VARCHAR(30) NOT NULL,
                                   category    VARCHAR(50),
                                   sort_order  INT DEFAULT 0
);

-- FEEDBACK
CREATE TABLE feedback (
                          id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          incident_id     UUID,
                          citizen_id      UUID,
                          rating          SMALLINT CHECK (rating BETWEEN 1 AND 5),
                          message         TEXT,
                          read_by_admin   BOOLEAN DEFAULT FALSE,
                          submitted_at    TIMESTAMP DEFAULT NOW()
);

-- RISK ZONES
CREATE TABLE risk_zones (
                            id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            region_name   VARCHAR(100) NOT NULL,
                            fire_risk     DECIMAL(5,2),
                            accident_risk DECIMAL(5,2),
                            drowning_risk DECIMAL(5,2),
                            updated_at    TIMESTAMP DEFAULT NOW()
);

-- DEVICE TOKENS
CREATE TABLE device_tokens (
                               id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id     UUID,
                               token       TEXT NOT NULL,
                               platform    VARCHAR(10),
                               created_at  TIMESTAMP DEFAULT NOW()
);

-- FOREIGN KEYS
ALTER TABLE users ADD CONSTRAINT fk_users_center
    FOREIGN KEY (center_id) REFERENCES centers(id);
ALTER TABLE users ADD CONSTRAINT fk_users_created_by
    FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE centers ADD CONSTRAINT fk_centers_admin
    FOREIGN KEY (admin_id) REFERENCES users(id);

ALTER TABLE vehicles ADD CONSTRAINT fk_vehicles_center
    FOREIGN KEY (center_id) REFERENCES centers(id);
ALTER TABLE vehicles ADD CONSTRAINT fk_vehicles_created_by
    FOREIGN KEY (created_by) REFERENCES users(id);

ALTER TABLE employee_status ADD CONSTRAINT fk_emp_status_user
    FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE employee_status ADD CONSTRAINT fk_emp_status_vehicle
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id);

ALTER TABLE incidents ADD CONSTRAINT fk_inc_citizen
    FOREIGN KEY (citizen_id) REFERENCES users(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_center
    FOREIGN KEY (center_id) REFERENCES centers(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_dispatcher
    FOREIGN KEY (dispatcher_id) REFERENCES users(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_vehicle
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_driver
    FOREIGN KEY (driver_id) REFERENCES users(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_paramedic
    FOREIGN KEY (paramedic_id) REFERENCES users(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_helper
    FOREIGN KEY (helper_id) REFERENCES users(id);
ALTER TABLE incidents ADD CONSTRAINT fk_inc_backup_center
    FOREIGN KEY (backup_center_id) REFERENCES centers(id);

ALTER TABLE incident_log ADD CONSTRAINT fk_log_incident
    FOREIGN KEY (incident_id) REFERENCES incidents(id);
ALTER TABLE incident_log ADD CONSTRAINT fk_log_actor
    FOREIGN KEY (actor_id) REFERENCES users(id);

ALTER TABLE feedback ADD CONSTRAINT fk_feedback_incident
    FOREIGN KEY (incident_id) REFERENCES incidents(id);
ALTER TABLE feedback ADD CONSTRAINT fk_feedback_citizen
    FOREIGN KEY (citizen_id) REFERENCES users(id);

ALTER TABLE device_tokens ADD CONSTRAINT fk_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id);

-- INDEXES
CREATE INDEX idx_incidents_center   ON incidents(center_id);
CREATE INDEX idx_incidents_status   ON incidents(status);
CREATE INDEX idx_incidents_citizen  ON incidents(citizen_id);
CREATE INDEX idx_incidents_reported ON incidents(reported_at DESC);
CREATE INDEX idx_emp_duty           ON employee_status(duty_status);
CREATE INDEX idx_vehicles_center    ON vehicles(center_id);
CREATE INDEX idx_vehicles_status    ON vehicles(status);
CREATE INDEX idx_centers_coords     ON centers(latitude, longitude);
CREATE INDEX idx_feedback_read      ON feedback(read_by_admin);

-- SEED: Super Admin
INSERT INTO users (id, email, password, role, full_name, status)
VALUES (
           gen_random_uuid(),
           'admin@lers.lb',
           '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.',
           'SUPER_ADMIN',
           'Super Admin',
           'active'
       );