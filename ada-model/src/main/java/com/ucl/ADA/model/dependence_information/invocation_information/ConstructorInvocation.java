package com.ucl.ADA.model.dependence_information.invocation_information;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ConstructorInvocation extends ElementInvocation {

    /**
     * The list of parameters which have been passes to the constructor on invocation.
     */
    private List<PassedParameter> passedParameters = new ArrayList<>();

    /**
     * The constructor of the attribute invocation object.
     *
     * @param name             name of the constructor being invoked
     * @param passedParameters The list of parameters whit which the constructor has been invoked with
     */
    public ConstructorInvocation(String name, List<PassedParameter> passedParameters) {
        super(name);
        if (passedParameters != null) {
            this.passedParameters.addAll(passedParameters);
        }
    }
}
