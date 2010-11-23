/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

import todolist.Item;
import todolist.TodolistPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>System</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link todolist.impl.SystemImpl#getItem <em>Item</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SystemImpl extends EObjectImpl implements todolist.System {
    /**
     * The cached value of the '{@link #getItem() <em>Item</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItem()
     * @generated
     * @ordered
     */
    protected EList<Item> item;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SystemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TodolistPackage.Literals.SYSTEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Item> getItem() {
        if (item == null) {
            item = new EObjectContainmentEList<Item>(Item.class, this, TodolistPackage.SYSTEM__ITEM);
        }
        return item;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case TodolistPackage.SYSTEM__ITEM:
                return ((InternalEList<?>)getItem()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case TodolistPackage.SYSTEM__ITEM:
                return getItem();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case TodolistPackage.SYSTEM__ITEM:
                getItem().clear();
                getItem().addAll((Collection<? extends Item>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case TodolistPackage.SYSTEM__ITEM:
                getItem().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case TodolistPackage.SYSTEM__ITEM:
                return item != null && !item.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //SystemImpl
