CREATE TABLE CLASS_STRUCTURE_EXTERNAL_PACKAGE_INVOCATION
(
    class_structure_id    BIGINT NOT NULL REFERENCES CLASS_STRUCTURE (id),
    package_invocation_id BIGINT NOT NULL REFERENCES PACKAGE_INVOCATION (id) UNIQUE
)