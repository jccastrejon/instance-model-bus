/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist;

import java.util.Date;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link todolist.Item#getImbId <em>Imb Id</em>}</li>
 *   <li>{@link todolist.Item#getDescription <em>Description</em>}</li>
 *   <li>{@link todolist.Item#getName <em>Name</em>}</li>
 *   <li>{@link todolist.Item#getDueDate <em>Due Date</em>}</li>
 *   <li>{@link todolist.Item#getPriority <em>Priority</em>}</li>
 * </ul>
 * </p>
 *
 * @see todolist.TodolistPackage#getItem()
 * @model
 * @generated
 */
public interface Item extends EObject {
    /**
     * Returns the value of the '<em><b>Imb Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Imb Id</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Imb Id</em>' attribute.
     * @see #setImbId(Long)
     * @see todolist.TodolistPackage#getItem_ImbId()
     * @model
     * @generated
     */
    Long getImbId();

    /**
     * Sets the value of the '{@link todolist.Item#getImbId <em>Imb Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Imb Id</em>' attribute.
     * @see #getImbId()
     * @generated
     */
    void setImbId(Long value);

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see todolist.TodolistPackage#getItem_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link todolist.Item#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription(String value);

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see todolist.TodolistPackage#getItem_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link todolist.Item#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Due Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Due Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Due Date</em>' attribute.
     * @see #setDueDate(Date)
     * @see todolist.TodolistPackage#getItem_DueDate()
     * @model
     * @generated
     */
    Date getDueDate();

    /**
     * Sets the value of the '{@link todolist.Item#getDueDate <em>Due Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Due Date</em>' attribute.
     * @see #getDueDate()
     * @generated
     */
    void setDueDate(Date value);

    /**
     * Returns the value of the '<em><b>Priority</b></em>' attribute.
     * The literals are from the enumeration {@link todolist.Priority}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Priority</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Priority</em>' attribute.
     * @see todolist.Priority
     * @see #setPriority(Priority)
     * @see todolist.TodolistPackage#getItem_Priority()
     * @model
     * @generated
     */
    Priority getPriority();

    /**
     * Sets the value of the '{@link todolist.Item#getPriority <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Priority</em>' attribute.
     * @see todolist.Priority
     * @see #getPriority()
     * @generated
     */
    void setPriority(Priority value);

} // Item
