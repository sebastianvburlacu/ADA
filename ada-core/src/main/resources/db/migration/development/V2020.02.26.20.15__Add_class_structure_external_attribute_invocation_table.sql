CREATE TABLE CLASS_STRUCTURE_EXTERNAL_ATTRIBUTE_INVOCATION
(
    class_structure_id   BIGINT NOT NULL REFERENCES CLASS_STRUCTURE (id),
    attribute_invocation_id BIGINT NOT NULL REFERENCES ATTRIBUTE_INVOCATION (id) UNIQUE
)