CREATE TABLE STATIC_INFO_METHOD_DECLARATION
(
    static_info_id        BIGINT NOT NULL REFERENCES STATIC_INFO (id),
    method_declaration_id BIGINT NOT NULL REFERENCES METHOD_DECLARATION (id) UNIQUE
)