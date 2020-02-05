package com.ucl.ADA.parser.dependence_information;

import com.ucl.ADA.parser.dependence_information.declaration_information.ConstructorDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.declaration_information.DataDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.declaration_information.MethodDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.invocation_information.DataInvocationInformation;
import com.ucl.ADA.parser.dependence_information.invocation_information.MethodInvocationInformation;
import com.ucl.ADA.parser.dependence_information.invocation_information.ModuleInvocationInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class ClassDependenceInformation {

    // Attributes declared in this class
    private ArrayList<DataDeclarationInformation> dataDeclarations = new ArrayList<>();

    // Methods declared in this class
    private ArrayList<MethodDeclarationInformation> methodsDeclarations = new ArrayList<>();

    // Constructors declared in this class
    private ArrayList<ConstructorDeclarationInformation> constructorDeclarations = new ArrayList<>();


    // For data and control flow coupling:
    // Data fields from other classes which this class invokes
    private ArrayList<DataInvocationInformation> invokedData = new ArrayList<>();

    // Methods from other classes which this class invokes
    private ArrayList<MethodInvocationInformation> invokedMethods = new ArrayList<>();

    // Data from this class which are invoked by other classes
    private ArrayList<DataInvocationInformation> incomingDataInvocation = new ArrayList<>();

    // Methods from this class which are invoked by other classes
    private ArrayList<MethodInvocationInformation> incomingMethodInvocation = new ArrayList<>();


    // For global coupling:
    // Global data fields which this class invokes
    private ArrayList<DataInvocationInformation> invokedGlobalData = new ArrayList<>();

    // Global methods which this class invokes
    private ArrayList<MethodInvocationInformation> invokedGlobalMethods = new ArrayList<>();


    // For environmental coupling:
    // Packages imported into this package(fan-out)
    private ArrayList<ModuleInvocationInformation> importedModules = new ArrayList<>();

    // Packages importing this package (fan-in)
    private ArrayList<ModuleInvocationInformation> incomingModuleImports = new ArrayList<>();

    public ClassDependenceInformation() {

    }

    public void addDataDeclaration(DataDeclarationInformation dataDeclarationInformation) {
        this.dataDeclarations.add(dataDeclarationInformation);
    }

    public void addMethodDeclaration(MethodDeclarationInformation methodDeclarationInformation) {
        this.methodsDeclarations.add(methodDeclarationInformation);
    }

    public void addConstructorDeclaration(ConstructorDeclarationInformation constructorDeclarationInformation) {
        this.constructorDeclarations.add(constructorDeclarationInformation);
    }
}
