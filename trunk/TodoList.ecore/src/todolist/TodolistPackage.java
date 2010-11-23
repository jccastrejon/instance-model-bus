/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see todolist.TodolistFactory
 * @model kind="package"
 * @generated
 */
public interface TodolistPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "todolist";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://todolist/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "todolist";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TodolistPackage eINSTANCE = todolist.impl.TodolistPackageImpl.init();

    /**
     * The meta object id for the '{@link todolist.impl.ItemImpl <em>Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see todolist.impl.ItemImpl
     * @see todolist.impl.TodolistPackageImpl#getItem()
     * @generated
     */
    int ITEM = 0;

    /**
     * The feature id for the '<em><b>Imb Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM__IMB_ID = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM__NAME = 2;

    /**
     * The feature id for the '<em><b>Due Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM__DUE_DATE = 3;

    /**
     * The feature id for the '<em><b>Priority</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM__PRIORITY = 4;

    /**
     * The number of structural features of the '<em>Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ITEM_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link todolist.impl.SystemImpl <em>System</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see todolist.impl.SystemImpl
     * @see todolist.impl.TodolistPackageImpl#getSystem()
     * @generated
     */
    int SYSTEM = 1;

    /**
     * The feature id for the '<em><b>Item</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM__ITEM = 0;

    /**
     * The number of structural features of the '<em>System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SYSTEM_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link todolist.Priority <em>Priority</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see todolist.Priority
     * @see todolist.impl.TodolistPackageImpl#getPriority()
     * @generated
     */
    int PRIORITY = 2;


    /**
     * Returns the meta object for class '{@link todolist.Item <em>Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Item</em>'.
     * @see todolist.Item
     * @generated
     */
    EClass getItem();

    /**
     * Returns the meta object for the attribute '{@link todolist.Item#getImbId <em>Imb Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Imb Id</em>'.
     * @see todolist.Item#getImbId()
     * @see #getItem()
     * @generated
     */
    EAttribute getItem_ImbId();

    /**
     * Returns the meta object for the attribute '{@link todolist.Item#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see todolist.Item#getDescription()
     * @see #getItem()
     * @generated
     */
    EAttribute getItem_Description();

    /**
     * Returns the meta object for the attribute '{@link todolist.Item#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see todolist.Item#getName()
     * @see #getItem()
     * @generated
     */
    EAttribute getItem_Name();

    /**
     * Returns the meta object for the attribute '{@link todolist.Item#getDueDate <em>Due Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Due Date</em>'.
     * @see todolist.Item#getDueDate()
     * @see #getItem()
     * @generated
     */
    EAttribute getItem_DueDate();

    /**
     * Returns the meta object for the attribute '{@link todolist.Item#getPriority <em>Priority</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Priority</em>'.
     * @see todolist.Item#getPriority()
     * @see #getItem()
     * @generated
     */
    EAttribute getItem_Priority();

    /**
     * Returns the meta object for class '{@link todolist.System <em>System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>System</em>'.
     * @see todolist.System
     * @generated
     */
    EClass getSystem();

    /**
     * Returns the meta object for the containment reference list '{@link todolist.System#getItem <em>Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Item</em>'.
     * @see todolist.System#getItem()
     * @see #getSystem()
     * @generated
     */
    EReference getSystem_Item();

    /**
     * Returns the meta object for enum '{@link todolist.Priority <em>Priority</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Priority</em>'.
     * @see todolist.Priority
     * @generated
     */
    EEnum getPriority();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    TodolistFactory getTodolistFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link todolist.impl.ItemImpl <em>Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see todolist.impl.ItemImpl
         * @see todolist.impl.TodolistPackageImpl#getItem()
         * @generated
         */
        EClass ITEM = eINSTANCE.getItem();

        /**
         * The meta object literal for the '<em><b>Imb Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM__IMB_ID = eINSTANCE.getItem_ImbId();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM__DESCRIPTION = eINSTANCE.getItem_Description();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM__NAME = eINSTANCE.getItem_Name();

        /**
         * The meta object literal for the '<em><b>Due Date</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM__DUE_DATE = eINSTANCE.getItem_DueDate();

        /**
         * The meta object literal for the '<em><b>Priority</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ITEM__PRIORITY = eINSTANCE.getItem_Priority();

        /**
         * The meta object literal for the '{@link todolist.impl.SystemImpl <em>System</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see todolist.impl.SystemImpl
         * @see todolist.impl.TodolistPackageImpl#getSystem()
         * @generated
         */
        EClass SYSTEM = eINSTANCE.getSystem();

        /**
         * The meta object literal for the '<em><b>Item</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SYSTEM__ITEM = eINSTANCE.getSystem_Item();

        /**
         * The meta object literal for the '{@link todolist.Priority <em>Priority</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see todolist.Priority
         * @see todolist.impl.TodolistPackageImpl#getPriority()
         * @generated
         */
        EEnum PRIORITY = eINSTANCE.getPriority();

    }

} //TodolistPackage
