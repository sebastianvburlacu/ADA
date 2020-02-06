package com.ucl.ADA.parser.dependence_information;

import com.ucl.ADA.parser.dependence_information.declaration_information.AttributeDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.declaration_information.ConstructorDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.declaration_information.MethodDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.declaration_information.PackageDeclarationInformation;
import com.ucl.ADA.parser.dependence_information.invocation_information.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;

@Getter @Setter @NoArgsConstructor
public class ProjectDependenceTree {
    private HashMap<String, ClassDependenceTree> classDependenceTrees = new HashMap<>();

    public void addModuleDeclaration(String className, PackageDeclarationInformation packageDeclarationInformation) {
        if (this.classDependenceTrees.containsKey(className)) {
            this.classDependenceTrees.get(className).setCurrentPackage(packageDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.setCurrentPackage(packageDeclarationInformation);
            this.classDependenceTrees.put(className, classDependenceTree);
        }
    }

    public void addAttributeDeclaration(String className, AttributeDeclarationInformation attributeDeclarationInformation) {
        if (this.classDependenceTrees.containsKey(className)) {
            this.classDependenceTrees.get(className).addAttributeDeclaration(attributeDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addAttributeDeclaration(attributeDeclarationInformation);
            this.classDependenceTrees.put(className, classDependenceTree);
        }
    }

    public void addConstructorDeclaration(String className, ConstructorDeclarationInformation constructorDeclarationInformation) {
        if (this.classDependenceTrees.containsKey(className)) {
            this.classDependenceTrees.get(className).addConstructorDeclaration(constructorDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addConstructorDeclaration(constructorDeclarationInformation);
            this.classDependenceTrees.put(className, classDependenceTree);
        }
    }

    public void addMethodDeclaration(String className, MethodDeclarationInformation methodDeclarationInformation) {
        if (this.classDependenceTrees.containsKey(className)) {
            this.classDependenceTrees.get(className).addMethodDeclaration(methodDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addMethodDeclaration(methodDeclarationInformation);
            this.classDependenceTrees.put(className, classDependenceTree);
        }
    }

    public void addPackageInvocation(String consumingClassName, String declaringClassName, PackageInvocationInformation packageDeclarationInformation) {
        if (this.classDependenceTrees.containsKey(consumingClassName)) {
            this.classDependenceTrees.get(consumingClassName).addPackageInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, packageDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addPackageInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, packageDeclarationInformation);
            this.classDependenceTrees.put(consumingClassName, classDependenceTree);
        }

        if (this.classDependenceTrees.containsKey(declaringClassName)) {
            this.classDependenceTrees.get(declaringClassName).addPackageInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, packageDeclarationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addPackageInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, packageDeclarationInformation);
            this.classDependenceTrees.put(declaringClassName, classDependenceTree);
        }
    }

    public void addAttributeInvocation(String consumingClassName, String declaringClassName, AttributeInvocationInformation attributeInvocationInformation) {
        if (this.classDependenceTrees.containsKey(consumingClassName)) {
            this.classDependenceTrees.get(consumingClassName).addAttributeInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, attributeInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addAttributeInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, attributeInvocationInformation);
            this.classDependenceTrees.put(consumingClassName, classDependenceTree);
        }

        if (this.classDependenceTrees.containsKey(declaringClassName)) {
            this.classDependenceTrees.get(declaringClassName).addAttributeInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, attributeInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addAttributeInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, attributeInvocationInformation);
            this.classDependenceTrees.put(declaringClassName, classDependenceTree);
        }
    }

    public void addConstructorInvocation(String consumingClassName, String declaringClassName, ConstructorInvocationInformation constructorInvocationInformation) {
        if (this.classDependenceTrees.containsKey(consumingClassName)) {
            this.classDependenceTrees.get(consumingClassName).addConstructorInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, constructorInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addConstructorInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, constructorInvocationInformation);
            this.classDependenceTrees.put(consumingClassName, classDependenceTree);
        }

        if (this.classDependenceTrees.containsKey(declaringClassName)) {
            this.classDependenceTrees.get(declaringClassName).addConstructorInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, constructorInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addConstructorInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, constructorInvocationInformation);
            this.classDependenceTrees.put(declaringClassName, classDependenceTree);
        }
    }

    public void addMethodInvocation(String consumingClassName, String declaringClassName, MethodInvocationInformation methodInvocationInformation) {
        if (this.classDependenceTrees.containsKey(consumingClassName)) {
            this.classDependenceTrees.get(consumingClassName).addMethodInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, methodInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addMethodInvocationElement(declaringClassName, InvocationType.OUTGOING_INVOCATION, methodInvocationInformation);
            this.classDependenceTrees.put(consumingClassName, classDependenceTree);
        }

        if (this.classDependenceTrees.containsKey(declaringClassName)) {
            this.classDependenceTrees.get(declaringClassName).addMethodInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, methodInvocationInformation);
        } else {
            ClassDependenceTree classDependenceTree = new ClassDependenceTree();
            classDependenceTree.addMethodInvocationElement(consumingClassName, InvocationType.INCOMING_INVOCATION, methodInvocationInformation);
            this.classDependenceTrees.put(declaringClassName, classDependenceTree);
        }
    }
}