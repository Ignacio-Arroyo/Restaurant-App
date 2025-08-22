-- Script para crear las tablas de consentimientos legales
-- Ejecutar después de que las tablas principales estén creadas

-- Tabla de consentimientos de usuario
CREATE TABLE IF NOT EXISTS user_consents (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    consent_type VARCHAR(50) NOT NULL,
    granted BOOLEAN NOT NULL DEFAULT FALSE,
    granted_at TIMESTAMP,
    revoked_at TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_consents_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT check_consent_type 
        CHECK (consent_type IN (
            'TERMS_AND_CONDITIONS', 
            'PRIVACY_POLICY', 
            'MARKETING_EMAILS', 
            'COOKIES', 
            'DATA_PROCESSING'
        ))
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_user_consents_user_id ON user_consents(user_id);
CREATE INDEX IF NOT EXISTS idx_user_consents_type ON user_consents(consent_type);
CREATE INDEX IF NOT EXISTS idx_user_consents_granted ON user_consents(granted);
CREATE INDEX IF NOT EXISTS idx_user_consents_user_type ON user_consents(user_id, consent_type);

-- Índice para consultas de marketing (usuarios que han dado consentimiento)
CREATE INDEX IF NOT EXISTS idx_marketing_consent 
    ON user_consents(consent_type, granted) 
    WHERE consent_type = 'MARKETING_EMAILS' AND granted = TRUE;

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_user_consents_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_user_consents_updated_at ON user_consents;
CREATE TRIGGER trigger_user_consents_updated_at
    BEFORE UPDATE ON user_consents
    FOR EACH ROW
    EXECUTE FUNCTION update_user_consents_updated_at();

-- Comentarios para documentación
COMMENT ON TABLE user_consents IS 'Almacena los consentimientos de los usuarios para cumplimiento GDPR y legal';
COMMENT ON COLUMN user_consents.consent_type IS 'Tipo de consentimiento: TERMS_AND_CONDITIONS, PRIVACY_POLICY, MARKETING_EMAILS, COOKIES, DATA_PROCESSING';
COMMENT ON COLUMN user_consents.granted IS 'Si el consentimiento fue otorgado (true) o revocado (false)';
COMMENT ON COLUMN user_consents.ip_address IS 'Dirección IP del usuario cuando otorgó/revocó el consentimiento';
COMMENT ON COLUMN user_consents.user_agent IS 'User agent del navegador para trazabilidad';

-- Datos de ejemplo (opcional para testing)
-- INSERT INTO user_consents (user_id, consent_type, granted, ip_address, user_agent) 
-- VALUES 
--     (1, 'TERMS_AND_CONDITIONS', TRUE, '127.0.0.1', 'Test Browser'),
--     (1, 'PRIVACY_POLICY', TRUE, '127.0.0.1', 'Test Browser'),
--     (1, 'MARKETING_EMAILS', TRUE, '127.0.0.1', 'Test Browser'),
--     (1, 'DATA_PROCESSING', TRUE, '127.0.0.1', 'Test Browser');
