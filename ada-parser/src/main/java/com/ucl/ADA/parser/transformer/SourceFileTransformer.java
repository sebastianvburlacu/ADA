package com.ucl.ADA.parser.transformer;

import com.ucl.ADA.model.dependence_information.declaration_information.*;
import com.ucl.ADA.model.dependence_information.invocation_information.ConstructorInvocation;
import com.ucl.ADA.model.dependence_information.invocation_information.MethodInvocation;
import com.ucl.ADA.model.dependence_information.invocation_information.PackageInvocation;
import com.ucl.ADA.model.dependence_information.invocation_information.PassedParameter;
import com.ucl.ADA.model.project_structure.ProjectStructure;
import com.ucl.ADA.parser.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SourceFileTransformer {

    private ProjectStructure projectStructure;

    private SourceFile sourceFile;

    protected SourceFileTransformer(ProjectStructure projectStructure, SourceFile sourceFile) {
        this.projectStructure = projectStructure;
        this.sourceFile = sourceFile;
    }

    protected void transformPackageDeclaration() {
        PackageDeclaration packageDeclaration = new PackageDeclaration(sourceFile.getPackageName());

        projectStructure.addPackageDeclaration(sourceFile.getClassName(), packageDeclaration);
    }

    protected void transformAttributeDeclaration() {
        String className = sourceFile.getClassName();

        for (SourceAttribute sourceAttribute : sourceFile.getClassAttributes()) {
            Set<ModifierType> modifierTypes = ModifierTransformer.getModifierTypes(sourceAttribute.getModifiers());

            AttributeDeclaration attributeDeclaration = new AttributeDeclaration
                    (modifierTypes, sourceAttribute.getType(), sourceAttribute.getName(), sourceAttribute.getValue());

            projectStructure.addAttributeDeclaration(className, attributeDeclaration);
        }
    }

    protected void processConstructorDeclaration() {
        String className = sourceFile.getClassName();

        for (SourceConstructor sourceConstructor : sourceFile.getDeclaredSourceConstructors()) {
            Set<ModifierType> modifierTypes = ModifierTransformer.getModifierTypes(sourceConstructor.getModifiers());

            List<ParameterDeclaration> parameters = new ArrayList<>();
            for (Map.Entry<String, String> entry : sourceConstructor.getParameters().entrySet())
                parameters.add(new ParameterDeclaration(entry.getKey(), entry.getValue()));

            ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration
                    (modifierTypes, sourceConstructor.getName(), parameters);

            projectStructure.addConstructorDeclaration(className, constructorDeclaration);
        }
    }

    protected void processMethodDeclaration() {
        String className = sourceFile.getClassName();

        for (SourceMethod sourceMethod : sourceFile.getMethods()) {
            Set<ModifierType> modifierTypes = ModifierTransformer.getModifierTypes(sourceMethod.getModifiers());

            List<ParameterDeclaration> parameters = new ArrayList<>();
            for (Map.Entry<String, String> entry : sourceMethod.getParameters().entrySet())
                parameters.add(new ParameterDeclaration(entry.getKey(), entry.getValue()));

            MethodDeclaration methodDeclaration = new MethodDeclaration
                    (modifierTypes, sourceMethod.getReturnType(), sourceMethod.getName(), parameters);

            projectStructure.addMethodDeclaration(className, methodDeclaration);
        }
    }

    protected void processPackageInvocation() {
        Set<String> importedClasses = sourceFile.getImportedClasses();
        if (importedClasses.isEmpty()) return;

        String className = sourceFile.getClassName();

        for (String importClass : importedClasses) {
            // import class should not end with .*
            assert !importClass.endsWith(".*");

            PackageInvocation packageInvocation = new PackageInvocation(importClass);

            projectStructure.addPackageInvocation(className, importClass, packageInvocation);
        }
    }

    // TODO: ...
    protected void processAttributeInvocation() {

    }

    protected void processConstructorInvocation() {
        String className = sourceFile.getClassName();

        for (SourceMethod sourceMethod : sourceFile.getMethods()) {
            for (SourceConstructorInvocation ci : sourceMethod.getSourceConstructorInvocations()) {
                List<PassedParameter> parameters = new ArrayList<>();
                for (String value : ci.getArguments())
                    parameters.add(new PassedParameter(value));

                ConstructorInvocation constructorInvocation = new ConstructorInvocation(className, parameters);

                projectStructure.addConstructorInvocation(className, ci.getConstructorClassName(), constructorInvocation);
            }
        }
    }

    protected void processMethodInvocation() {
        String className = sourceFile.getClassName();

        for (SourceMethod sourceMethod : sourceFile.getMethods()) {
            for (MethodCall methodCall : sourceMethod.getMethodCalls()) {
                List<PassedParameter> parameters = new ArrayList<>();
                for (String value : methodCall.getArguments())
                    parameters.add(new PassedParameter(value));

                MethodInvocation methodInvocation = new MethodInvocation(methodCall.getMethodCallName(), parameters);

                projectStructure.addMethodInvocation(className, methodCall.getCalleeName(), methodInvocation);
            }
        }
    }

    // TODO: add import attribute into externalInfo model
    protected void processExternalInvocation() {
        String className = sourceFile.getClassName();

//        ExternalInvocationInfo externalInvocationInfo = sourceFile.getExternalInvocationInfo();
    }

}