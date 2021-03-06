package com.ucl.ADA.model.class_structure;

import com.ucl.ADA.model.BaseEntity;
import com.ucl.ADA.model.dependence_information.DependenceInfo;
import com.ucl.ADA.model.dependence_information.declaration_information.AttributeDeclaration;
import com.ucl.ADA.model.dependence_information.declaration_information.ConstructorDeclaration;
import com.ucl.ADA.model.dependence_information.declaration_information.MethodDeclaration;
import com.ucl.ADA.model.dependence_information.declaration_information.PackageDeclaration;
import com.ucl.ADA.model.dependence_information.invocation_information.*;
import com.ucl.ADA.model.metrics.class_metrics.ClassMetricType;
import com.ucl.ADA.model.metrics.class_metrics.ClassMetricValue;
import com.ucl.ADA.model.metrics.relation_metrics.RelationMetricType;
import com.ucl.ADA.model.metrics.relation_metrics.RelationMetricValue;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CLASS_STRUCTURE")
public class ClassStructure extends BaseEntity {

    // Declaration information corresponding to this class:

    /**
     * Fully qualified class package name (including the name of the class in the end).
     */
    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "package_declaration_id")
    private PackageDeclaration currentPackage = new PackageDeclaration("$");

    /**
     * Attributes declared in this class.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_ATTRIBUTE_DECLARATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_declaration_id")
    )
    private List<AttributeDeclaration> attributeDeclarations = new ArrayList<>();

    /**
     * Constructors declared in this class.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_CONSTRUCTOR_DECLARATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "constructor_declaration_id")
    )
    private List<ConstructorDeclaration> constructorDeclarations = new ArrayList<>();

    /**
     * Methods declared in this class.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_METHOD_DECLARATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "method_declaration_id")
    )
    private List<MethodDeclaration> methodsDeclarations = new ArrayList<>();


    // Dependence relations to other classes:

    /**
     * Information about the invocations of the elements from the other classes from this class. String is the qualified
     * name of the class.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "CLASS_STRUCTURE_OUTGOING_DEPENDENCE_INFO",
            joinColumns = {@JoinColumn(name = "class_structure_id")},
            inverseJoinColumns = {@JoinColumn(name = "dependence_info_id")})
    @MapKeyColumn(name = "class_name")
    private Map<String, DependenceInfo> outgoingDependenceInfo = new HashMap<>();

    /**
     * Information about the invocations of elements from this class by the other classes. String is the qualified
     * name of the class.
     */
    @Transient
    private Map<String, DependenceInfo> incomingDependenceInfo = new HashMap<>();


    // Global invocations:

    /**
     * Global Data present in the class. It can be either declared or invoked. Not really possible in Java.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_GLOBAL_ATTRIBUTE_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_invocation_id")
    )
    private List<AttributeInvocation> globalData = new ArrayList<>();

    /**
     * Global Methods present in the class. They can be either declared or invoked. Not really possible in Java.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_GLOBAL_METHOD_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "method_invocation_id")
    )
    private List<MethodInvocation> globalMethods = new ArrayList<>();


    // External invocations (from outside of the project):

    /**
     * External Attribute Invocations. Includes only calls to classes which cannot be resolved within the project. These
     * include the dependencies and libraries.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_EXTERNAL_PACKAGE_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "package_invocation_id")
    )
    private List<PackageInvocation> externalPackageImports = new ArrayList<>();

    /**
     * External Method Invocations. Includes only calls to classes which cannot be resolved within the project. These
     * include the dependencies and libraries.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_EXTERNAL_METHOD_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "method_invocation_id")
    )
    private List<MethodInvocation> externalMethodInvocations = new ArrayList<>();

    /**
     * External Constructor Invocations. Includes only calls to classes which cannot be resolved within the project. These
     * include the dependencies and libraries.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_EXTERNAL_CONSTRUCTOR_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "constructor_invocation_id")
    )
    private List<ConstructorInvocation> externalConstructorInvocations = new ArrayList<>();

    /**
     * External Attribute Invocations. Includes only calls to classes which cannot be resolved within the project. These
     * include the dependencies and libraries.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(
            name = "CLASS_STRUCTURE_EXTERNAL_ATTRIBUTE_INVOCATION",
            joinColumns = @JoinColumn(name = "class_structure_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_invocation_id")
    )
    private List<AttributeInvocation> externalAttributeInvocations = new ArrayList<>();


    // Metrics:

    /**
     * All of the metric values for the link between the current class and the linking classes.
     */
    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "CLASS_STRUCTURE_RELATION_METRIC_VALUE",
            joinColumns = {@JoinColumn(name = "class_structure_id")},
            inverseJoinColumns = {@JoinColumn(name = "relation_metric_value_id")})
    @MapKeyColumn(name = "class_name")
    private Map<String, RelationMetricValue> relationMetricValues = new HashMap<>();

    /**
     * The metrics corresponding to the current class.
     */
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "class_metric_value_id")
    private ClassMetricValue classMetricValues = new ClassMetricValue();


    /**
     * Updates the package corresponding to the class.
     *
     * @param packageDeclaration the name of the package corresponding to the current class
     */
    public void setCurrentPackage(PackageDeclaration packageDeclaration) {
        this.currentPackage = packageDeclaration;
    }

    /**
     * Adds a Attribute Declaration to the ClassDependenceTree
     *
     * @param attributeDeclaration the attribute declaration object
     */
    public void addAttributeDeclaration(AttributeDeclaration attributeDeclaration) {
        this.attributeDeclarations.add(attributeDeclaration);
    }

    /**
     * Adds a Method Declaration to the ClassDependenceTree
     *
     * @param methodDeclaration the method declaration object
     */
    public void addMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodsDeclarations.add(methodDeclaration);
    }

    /**
     * Adds a Constructor Declaration to the ClassDependenceTree
     *
     * @param constructorDeclaration the constructor declaration object
     */
    public void addConstructorDeclaration(ConstructorDeclaration constructorDeclaration) {
        this.constructorDeclarations.add(constructorDeclaration);
    }

    /**
     * Adds a new global data to the instance.
     *
     * @param attributeInvocationInformation a global data invocation information object containing all of the
     *                                       corresponding information about the method being added
     */
    public void addNewGlobalData(AttributeInvocation attributeInvocationInformation) {
        this.globalData.add(attributeInvocationInformation);
    }

    /**
     * Adds a new global method to the instance.
     *
     * @param methodInvocationInformation a global method invocation information object containing all of the
     *                                    corresponding information about the global method being added
     */
    public void addGlobalMethod(MethodInvocation methodInvocationInformation) {
        this.globalMethods.add(methodInvocationInformation);
    }

    /**
     * Adds a new external package import
     *
     * @param packageInvocation an external package invocation object containing all of the related information about the package invocation being added
     */
    public void addExternalPackageImport(PackageInvocation packageInvocation) {
        this.externalPackageImports.add(packageInvocation);
    }

    /**
     * Adds a new external attribute invocation
     *
     * @param attributeInvocation an external attribute invocation object containing all of the related information about the attribute invocation being added
     */
    public void addExternalAttributeInvocation(AttributeInvocation attributeInvocation) {
        this.externalAttributeInvocations.add(attributeInvocation);
    }

    /**
     * Adds a new external constructor invocation
     *
     * @param constructorInvocation an external constructor invocation object containing all of the related information about the constructor invocation being added
     */
    public void addExternalConstructorInvocation(ConstructorInvocation constructorInvocation) {
        this.externalConstructorInvocations.add(constructorInvocation);
    }

    /**
     * Adds a new external method invocation
     *
     * @param methodInvocation an external method invocation object containing all of the related information about the method invocation being added
     */
    public void addExternalMethodInvocation(MethodInvocation methodInvocation) {
        this.externalMethodInvocations.add(methodInvocation);
    }

    /**
     * Adds a package invocation element as either an incoming or outgoing dependency, depending on the call.
     *
     * @param relatingClass     the class which the invocation relates to
     * @param invocationType    either an incoming invocation type in the case that the package is being invoked from the
     *                          relatingClass, or an outgoing invocation type in the case that the package is invoked from
     *                          the relatingClass
     * @param packageInvocation the package invocation object containing the data corresponding to the
     *                          invocation in question
     */
    public void addPackageInvocationElement(String relatingClass, InvocationType invocationType, PackageInvocation packageInvocation) {
        if (invocationType == InvocationType.OUTGOING) {
            if (this.outgoingDependenceInfo.containsKey(relatingClass)) {
                this.outgoingDependenceInfo.get(relatingClass).addNewPackage(packageInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewPackage(packageInvocation);
                this.outgoingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        } else {
            if (this.incomingDependenceInfo.containsKey(relatingClass)) {
                this.incomingDependenceInfo.get(relatingClass).addNewPackage(packageInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewPackage(packageInvocation);
                this.incomingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        }
    }

    /**
     * Adds an attribute invocation element as either an incoming or outgoing dependency, depending on the call.
     *
     * @param relatingClass       the class which the invocation relates to
     * @param invocationType      either an incoming invocation type in the case that the attribute is being invoked from the
     *                            relatingClass, or an outgoing invocation type in the case that the attribute is invoked from
     *                            the relatingClass
     * @param attributeInvocation the attribute invocation object containing the data corresponding to the
     *                            invocation in question
     */
    public void addAttributeInvocationElement(String relatingClass, InvocationType invocationType, AttributeInvocation attributeInvocation) {
        if (invocationType == InvocationType.OUTGOING) {
            if (this.outgoingDependenceInfo.containsKey(relatingClass)) {
                this.outgoingDependenceInfo.get(relatingClass).addNewAttribute(attributeInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewAttribute(attributeInvocation);
                this.outgoingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        } else {
            if (this.incomingDependenceInfo.containsKey(relatingClass)) {
                this.incomingDependenceInfo.get(relatingClass).addNewAttribute(attributeInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewAttribute(attributeInvocation);
                this.incomingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        }
    }

    /**
     * Adds a constructor invocation element as either an incoming or outgoing dependency, depending on the call.
     *
     * @param relatingClass         the class which the invocation relates to
     * @param invocationType        either an incoming invocation type in the case that the constructor is being invoked from the
     *                              relatingClass, or an outgoing invocation type in the case that the constructor is invoked from
     *                              the relatingClass
     * @param constructorInvocation the constructor invocation object containing the data corresponding to the
     *                              invocation in question
     */
    public void addConstructorInvocationElement(String relatingClass, InvocationType invocationType, ConstructorInvocation constructorInvocation) {
        if (invocationType == InvocationType.OUTGOING) {
            if (this.outgoingDependenceInfo.containsKey(relatingClass)) {
                this.outgoingDependenceInfo.get(relatingClass).addNewConstructor(constructorInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewConstructor(constructorInvocation);
                this.outgoingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        } else {
            if (this.incomingDependenceInfo.containsKey(relatingClass)) {
                this.incomingDependenceInfo.get(relatingClass).addNewConstructor(constructorInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewConstructor(constructorInvocation);
                this.incomingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        }
    }

    /**
     * Adds a method invocation element as either an incoming or outgoing dependency, depending on the call.
     *
     * @param relatingClass    the class which the invocation relates to
     * @param invocationType   either an incoming invocation type in the case that the method is being invoked from the
     *                         relatingClass, or an outgoing invocation type in the case that the method is invoked from
     *                         the relatingClass
     * @param methodInvocation the method invocation object containing the data corresponding to the
     *                         invocation in question
     */
    public void addMethodInvocationElement(String relatingClass, InvocationType invocationType, MethodInvocation methodInvocation) {
        if (invocationType == InvocationType.OUTGOING) {
            if (this.outgoingDependenceInfo.containsKey(relatingClass)) {
                this.outgoingDependenceInfo.get(relatingClass).addNewMethod(methodInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewMethod(methodInvocation);
                this.outgoingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        } else {
            if (this.incomingDependenceInfo.containsKey(relatingClass)) {
                this.incomingDependenceInfo.get(relatingClass).addNewMethod(methodInvocation);
            } else {
                DependenceInfo dependenceInfo = new DependenceInfo();
                dependenceInfo.addNewMethod(methodInvocation);
                this.incomingDependenceInfo.put(relatingClass, dependenceInfo);
            }
        }
    }

    public void computeClassMetric(ClassMetricType classMetricType) {
        float metricValue = 0F;
        // TODO: This could be made nicer by extracting the for loop around the switch. However, the switch would then
        //  be executed for each item, which would make it less efficient.
        Collection<DependenceInfo> incomingDependencyValues = incomingDependenceInfo.values();
        Collection<DependenceInfo> outgoingDependencyValues = outgoingDependenceInfo.values();

        switch (classMetricType) {
            case NUMBER_OF_CLASS_ATTRIBUTE_INVOCATIONS_INCOMING:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getAttributes().size();
                }
                this.classMetricValues.setNumberOfAttributeInvocationsIncoming(metricValue);
                break;

            case NUMBER_OF_CLASS_ATTRIBUTE_INVOCATIONS_OUTGOING:
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getAttributes().size();
                }
                this.classMetricValues.setNumberOfAttributeInvocationsOutgoing(metricValue);
                break;

            case NUMBER_OF_CLASS_METHOD_INVOCATIONS_INCOMING:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getMethods().size();
                }
                this.classMetricValues.setNumberOfMethodInvocationsIncoming(metricValue);
                break;

            case NUMBER_OF_CLASS_METHOD_INVOCATIONS_OUTGOING:
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getMethods().size();
                }
                this.classMetricValues.setNumberOfMethodInvocationsOutgoing(metricValue);
                break;

            case NUMBER_OF_CLASS_PACKAGE_IMPORTS_INCOMING:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getPackages().size();
                }
                this.classMetricValues.setNumberOfPackageImportsIncoming(metricValue);
                break;

            case NUMBER_OF_CLASS_PACKAGE_IMPORTS_OUTGOING:
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getPackages().size();
                }
                this.classMetricValues.setNumberOfPackageImportsOutgoing(metricValue);
                break;

            case NUMBER_OF_CLASS_CONSTRUCTOR_INVOCATIONS_INCOMING:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getConstructors().size();
                }
                this.classMetricValues.setNumberOfConstructorInvocationsIncoming(metricValue);
                break;

            case NUMBER_OF_CLASS_CONSTRUCTOR_INVOCATIONS_OUTGOING:
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getConstructors().size();
                }
                this.classMetricValues.setNumberOfConstructorInvocationsOutgoing(metricValue);
                break;

            case BIDIRECTIONAL_NUMBER_OF_CLASS_ATTRIBUTE_INVOCATIONS:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getAttributes().size();
                }
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getAttributes().size();
                }
                this.classMetricValues.setBidirectionalNumberOfAttributeInvocations(metricValue);
                break;

            case BIDIRECTIONAL_NUMBER_OF_CLASS_METHOD_INVOCATIONS:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getMethods().size();
                }
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getMethods().size();
                }
                this.classMetricValues.setBidirectionalNumberOfMethodInvocations(metricValue);
                break;

            case BIDIRECTIONAL_NUMBER_OF_CLASS_PACKAGE_IMPORTS:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getPackages().size();
                }
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getPackages().size();
                }
                this.classMetricValues.setBidirectionalNumberOfPackageImports(metricValue);
                break;

            case BIDIRECTIONAL_NUMBER_OF_CLASS_CONSTRUCTOR_INVOCATIONS:
                for (DependenceInfo dependenceInfoValue : incomingDependencyValues) {
                    metricValue += dependenceInfoValue.getConstructors().size();
                }
                for (DependenceInfo dependenceInfoValue : outgoingDependencyValues) {
                    metricValue += dependenceInfoValue.getConstructors().size();
                }
                this.classMetricValues.setBidirectionalNumberOfConstructorInvocations(metricValue);
                break;
        }
    }

    /**
     * compute relation metric which collects detailed invocation information from dependence info
     *
     * @param relationMetricType outgoing or incoming relation metric
     */
    public void computeRelationMetric(RelationMetricType relationMetricType) {
        float metricValue = 0F;
        // TODO: This could be made nicer by extracting the for loop around the switch. However, the switch would then
        //  be executed for each item, which would make it less efficient.

        switch (relationMetricType) {
            case NUMBER_OF_RELATION_PACKAGE_IMPORTS_INCOMING:

                // For all of the relating classes get the corresponding metrics
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getPackages().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfPackageImportsIncoming(metricValue);
                }

                break;

            case NUMBER_OF_RELATION_PACKAGE_IMPORTS_OUTGOING:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getPackages().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfPackageImportsOutgoing(metricValue);
                }
                break;

            case NUMBER_OF_RELATION_ATTRIBUTE_INVOCATIONS_INCOMING:
                // For all of the relating classes get the corresponding metrics
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getAttributes().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfAttributeInvocationsIncoming(metricValue);
                }
                break;


            case NUMBER_OF_RELATION_ATTRIBUTE_INVOCATIONS_OUTGOING:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getAttributes().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfAttributeInvocationsOutgoing(metricValue);
                }
                break;

            case NUMBER_OF_RELATION_METHOD_INVOCATIONS_INCOMING:
                // For all of the relating classes get the corresponding metrics
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getMethods().size();


                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfMethodInvocationsIncoming(metricValue);
                }
                break;

            case NUMBER_OF_RELATION_METHOD_INVOCATIONS_OUTGOING:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getMethods().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfMethodInvocationsOutgoing(metricValue);
                }
                break;


            case NUMBER_OF_RELATION_CONSTRUCTOR_INVOCATIONS_INCOMING:
                // For all of the relating classes get the corresponding metrics
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getConstructors().size();


                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfConstructorInvocationsIncoming(metricValue);
                }
                break;

            case NUMBER_OF_RELATION_CONSTRUCTOR_INVOCATIONS_OUTGOING:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getConstructors().size();

                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setNumberOfConstructorInvocationsOutgoing(metricValue);
                }
                break;

            case BIDIRECTIONAL_NUMBER_OF_RELATION_ATTRIBUTE_INVOCATIONS:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getAttributes().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setBidirectionalNumberOfAttributeInvocations(metricValue);
                }
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getAttributes().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    float currentMetricValue = relationMetricValues.get(key).getBidirectionalNumberOfAttributeInvocations();
                    relationMetricValues.get(key).setBidirectionalNumberOfAttributeInvocations(currentMetricValue + metricValue);
                }
                break;

            case BIDIRECTIONAL_NUMBER_OF_RELATION_METHOD_INVOCATIONS:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getMethods().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setBidirectionalNumberOfMethodInvocations(metricValue);
                }
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getMethods().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    float currentMetricValue = relationMetricValues.get(key).getBidirectionalNumberOfMethodInvocations();
                    relationMetricValues.get(key).setBidirectionalNumberOfMethodInvocations(currentMetricValue + metricValue);
                }
                break;

            case BIDIRECTIONAL_NUMBER_OF_RELATION_PACKAGE_IMPORTS:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getPackages().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setBidirectionalNumberOfPackageImports(metricValue);
                }
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getPackages().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    float previousMetricValue = relationMetricValues.get(key).getBidirectionalNumberOfPackageImports();
                    relationMetricValues.get(key).setBidirectionalNumberOfPackageImports(previousMetricValue + metricValue);
                }
                break;

            case BIDIRECTIONAL_NUMBER_OF_RELATION_CONSTRUCTOR_INVOCATIONS:
                // For all of the relating classes get the corresponding metrics
                for (String key : outgoingDependenceInfo.keySet()) {
                    metricValue = (float) outgoingDependenceInfo.get(key).getConstructors().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    relationMetricValues.get(key).setBidirectionalNumberOfConstructorInvocations(metricValue);
                }
                for (String key : incomingDependenceInfo.keySet()) {
                    metricValue = (float) incomingDependenceInfo.get(key).getConstructors().size();
                    // Check if the relation metrics for the class have already been computed
                    if (!relationMetricValues.containsKey(key)) {
                        RelationMetricValue relationMetricValueObject = new RelationMetricValue();
                        relationMetricValues.put(key, relationMetricValueObject);
                    }
                    float currentMetricValue = relationMetricValues.get(key).getBidirectionalNumberOfConstructorInvocations();
                    relationMetricValues.get(key).setBidirectionalNumberOfConstructorInvocations(currentMetricValue + metricValue);
                }
                break;
        }
    }

    /**
     * Computes the overall metric values.
     * PLEASE NOTE: This method assumes that all of the incoming/outgoing relation metrics have been computed for the class
     */
    public void computeCumulativeMetrics(RelationMetricType relationMetricType) {
        float metricValue = 0F;
        float attributesNumberIncoming = 0f;
        float attributesNumberOutgoing = 0f;
        float constructorNumberIncoming = 0f;
        float constructorNumberOutgoing = 0f;
        float methodsNumberIncoming = 0f;
        float methodsNumberOutgoing = 0f;
        float packageNumberIncoming = 0f;
        float packageNumberOutgoing = 0f;
        float weightedSumOfMetrics = 0f;

        switch (relationMetricType) {
            case GENERAL_CUMULATIVE_NORMALISED_BIDIRECTIONAL:
                // For all of the relating classes get the corresponding metrics
                for (String key : relationMetricValues.keySet()) {
                    attributesNumberIncoming = (float) relationMetricValues.get(key).getNumberOfAttributeInvocationsIncoming();
                    attributesNumberOutgoing = (float) relationMetricValues.get(key).getNumberOfAttributeInvocationsOutgoing();
                    constructorNumberIncoming = (float) relationMetricValues.get(key).getNumberOfConstructorInvocationsIncoming();
                    constructorNumberOutgoing = (float) relationMetricValues.get(key).getNumberOfConstructorInvocationsOutgoing();
                    methodsNumberIncoming = (float) relationMetricValues.get(key).getNumberOfMethodInvocationsIncoming();
                    methodsNumberOutgoing = (float) relationMetricValues.get(key).getNumberOfMethodInvocationsOutgoing();
                    packageNumberIncoming = (float) relationMetricValues.get(key).getNumberOfPackageImportsIncoming();
                    packageNumberOutgoing = (float) relationMetricValues.get(key).getNumberOfPackageImportsOutgoing();


                    weightedSumOfMetrics =
                            attributesNumberIncoming +
                                    2 * (constructorNumberIncoming + methodsNumberIncoming) +
                                    attributesNumberOutgoing +
                                    2 * (constructorNumberOutgoing + methodsNumberOutgoing) +
                                    packageNumberIncoming + packageNumberOutgoing;

                    metricValue = 1f - (weightedSumOfMetrics > 0f ? (1f / weightedSumOfMetrics) : 0);

                    relationMetricValues.get(key).setGeneralCumulativeNormalisedBidirectional(metricValue);
                }
        }
    }

    /**
     * compute class metrics for all class metric type
     */
    public void computeAllClassMetrics() {
        for (ClassMetricType classMetricType : ClassMetricType.values()) {
            this.computeClassMetric(classMetricType);
        }
    }

    /**
     * compute relation metrics for all relation types
     */
    public void computeAllRelationMetrics() {
        for (RelationMetricType relationMetricType : RelationMetricType.values()) {
            if (relationMetricType != RelationMetricType.GENERAL_CUMULATIVE_NORMALISED_BIDIRECTIONAL) {
                this.computeRelationMetric(relationMetricType);
            } else {
                this.computeCumulativeMetrics(relationMetricType);
            }
        }
    }
}
