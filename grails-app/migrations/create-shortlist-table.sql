-- Database migration script for Shortlist table
-- Create this as: grails-app/migrations/create-shortlist-table.sql
-- Or run these commands in your database console

-- Create shortlists table
CREATE TABLE shortlists (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          shortlisted_user_id BIGINT NOT NULL,
                          notes VARCHAR(500),
                          date_created DATETIME NOT NULL,
                          last_updated DATETIME NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_shortlist_user_shortlisted (user_id, shortlisted_user_id),
                          INDEX idx_shortlist_user (user_id),
                          INDEX idx_shortlist_shortlisted_user (shortlisted_user_id),
                          INDEX idx_shortlist_date_created (date_created),
                          FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                          FOREIGN KEY (shortlisted_user_id) REFERENCES user(id) ON DELETE CASCADE,
                          CONSTRAINT chk_no_self_shortlist CHECK (user_id != shortlisted_user_id)
);

-- Add indexes for better performance
CREATE INDEX idx_shortlist_compound ON shortlists (user_id, date_created DESC);
CREATE INDEX idx_shortlist_reverse ON shortlists (shortlisted_user_id, date_created DESC);

-- If using PostgreSQL, use this syntax instead:
/*
CREATE TABLE shortlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    shortlisted_user_id BIGINT NOT NULL,
    notes VARCHAR(500),
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, shortlisted_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (shortlisted_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (user_id != shortlisted_user_id)
);

CREATE INDEX idx_shortlist_user ON shortlists (user_id);
CREATE INDEX idx_shortlist_shortlisted_user ON shortlists (shortlisted_user_id);
CREATE INDEX idx_shortlist_date_created ON shortlists (date_created);
CREATE INDEX idx_shortlist_compound ON shortlists (user_id, date_created DESC);
CREATE INDEX idx_shortlist_reverse ON shortlists (shortlisted_user_id, date_created DESC);
*/

-- Sample data for testing (optional)
/*
INSERT INTO shortlists (user_id, shortlisted_user_id, notes, date_created, last_updated)
VALUES
(1, 2, 'Interesting profile, similar interests', NOW(), NOW()),
(1, 3, 'Good family background', NOW(), NOW()),
(2, 1, 'Mutual interests in music', NOW(), NOW()),
(3, 1, 'Professional compatibility', NOW(), NOW());
*/
