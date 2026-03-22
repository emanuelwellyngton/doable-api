ALTER TABLE tasks ADD COLUMN owner_id BIGINT;

-- Existing tasks cannot be automatically assigned to an owner.
-- Remove all unowned tasks before applying the NOT NULL constraint.
DELETE FROM tasks WHERE owner_id IS NULL;

ALTER TABLE tasks MODIFY COLUMN owner_id BIGINT NOT NULL;

ALTER TABLE tasks ADD CONSTRAINT fk_tasks_owner FOREIGN KEY (owner_id) REFERENCES users(id);

CREATE INDEX idx_tasks_owner_id ON tasks (owner_id);
