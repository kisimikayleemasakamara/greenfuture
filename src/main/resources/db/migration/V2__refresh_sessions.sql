CREATE TABLE refresh_session (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(id),
    family_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    replaced_by_session_id UUID REFERENCES refresh_session(id)
);

CREATE INDEX idx_refresh_session_family_id ON refresh_session(family_id);
CREATE INDEX idx_refresh_session_user_id ON refresh_session(user_id);
