CREATE TABLE IF NOT EXISTS vehicle_maintenance (
                                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID NOT NULL REFERENCES vehicles(id),
    service_date DATE,
    description TEXT,
    mileage_at_service INT,
    cost DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT NOW()
    );