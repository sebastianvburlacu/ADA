CREATE TABLE CLASS_STRUCTURE_CONSTRUCTOR_DECLARATION
(
    class_structure_id         BIGINT NOT NULL REFERENCES CLASS_STRUCTURE (id),
    constructor_declaration_id BIGINT NOT NULL REFERENCES CONSTRUCTOR_DECLARATION (id) UNIQUE
)