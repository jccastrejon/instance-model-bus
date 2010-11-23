/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link todolist.System#getItem <em>Item</em>}</li>
 * </ul>
 * </p>
 *
 * @see todolist.TodolistPackage#getSystem()
 * @model
 * @generated
 */
public interface System extends EObject {
    /**
     * Returns the value of the '<em><b>Item</b></em>' containment reference list.
     * The list contents are of type {@link todolist.Item}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Item</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Item</em>' containment reference list.
     * @see todolist.TodolistPackage#getSystem_Item()
     * @model containment="true"
     * @generated
     */
    EList<Item> getItem();

} // System
