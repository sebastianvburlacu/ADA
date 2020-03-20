package com.ucl.ADA.model.static_information.declaration_information;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AttributeDeclaration extends ElementDeclaration {

    /**
     * The type of the attribute.
     */
    private String type;

    /**
     * The value assigned to the attribute.
     */
    private String value;

    /**
     * The access modifier assigned to the attribute.
     */
    private Set<ModifierType> modifierTypes = new HashSet<>();

    /**
     * The constructor of the attribute declaration object.
     *
     * @param modifierTypes the set of access modifiers associated with the attribute
     * @param type          the type of the attribute
     * @param name          name of the attribute
     * @param value         the value assigned to the attribute
     */
    public AttributeDeclaration(Set<ModifierType> modifierTypes, String type, String name, String value) {
        super(name);
        if (modifierTypes != null) {
            this.modifierTypes.addAll(modifierTypes);
        }
        this.type = type;
        this.value = value;
    }
}