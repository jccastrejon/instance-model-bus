/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package todolist.impl;

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import todolist.Item;
import todolist.Priority;
import todolist.TodolistPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link todolist.impl.ItemImpl#getImbId <em>Imb Id</em>}</li>
 *   <li>{@link todolist.impl.ItemImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link todolist.impl.ItemImpl#getName <em>Name</em>}</li>
 *   <li>{@link todolist.impl.ItemImpl#getDueDate <em>Due Date</em>}</li>
 *   <li>{@link todolist.impl.ItemImpl#getPriority <em>Priority</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ItemImpl extends EObjectImpl implements Item {
    /**
     * The default value of the '{@link #getImbId() <em>Imb Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getImbId()
     * @generated
     * @ordered
     */
    protected static final Long IMB_ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getImbId() <em>Imb Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getImbId()
     * @generated
     * @ordered
     */
    protected Long imbId = IMB_ID_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDueDate() <em>Due Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDueDate()
     * @generated
     * @ordered
     */
    protected static final Date DUE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDueDate() <em>Due Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDueDate()
     * @generated
     * @ordered
     */
    protected Date dueDate = DUE_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected static final Priority PRIORITY_EDEFAULT = Priority.LOW;

    /**
     * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPriority()
     * @generated
     * @ordered
     */
    protected Priority priority = PRIORITY_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TodolistPackage.Literals.ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Long getImbId() {
        return imbId;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setImbId(Long newImbId) {
        Long oldImbId = imbId;
        imbId = newImbId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TodolistPackage.ITEM__IMB_ID, oldImbId, imbId));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescription(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TodolistPackage.ITEM__DESCRIPTION, oldDescription, description));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TodolistPackage.ITEM__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDueDate(Date newDueDate) {
        Date oldDueDate = dueDate;
        dueDate = newDueDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TodolistPackage.ITEM__DUE_DATE, oldDueDate, dueDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPriority(Priority newPriority) {
        Priority oldPriority = priority;
        priority = newPriority == null ? PRIORITY_EDEFAULT : newPriority;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TodolistPackage.ITEM__PRIORITY, oldPriority, priority));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case TodolistPackage.ITEM__IMB_ID:
                return getImbId();
            case TodolistPackage.ITEM__DESCRIPTION:
                return getDescription();
            case TodolistPackage.ITEM__NAME:
                return getName();
            case TodolistPackage.ITEM__DUE_DATE:
                return getDueDate();
            case TodolistPackage.ITEM__PRIORITY:
                return getPriority();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case TodolistPackage.ITEM__IMB_ID:
                setImbId((Long)newValue);
                return;
            case TodolistPackage.ITEM__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case TodolistPackage.ITEM__NAME:
                setName((String)newValue);
                return;
            case TodolistPackage.ITEM__DUE_DATE:
                setDueDate((Date)newValue);
                return;
            case TodolistPackage.ITEM__PRIORITY:
                setPriority((Priority)newValue);
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
            case TodolistPackage.ITEM__IMB_ID:
                setImbId(IMB_ID_EDEFAULT);
                return;
            case TodolistPackage.ITEM__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case TodolistPackage.ITEM__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TodolistPackage.ITEM__DUE_DATE:
                setDueDate(DUE_DATE_EDEFAULT);
                return;
            case TodolistPackage.ITEM__PRIORITY:
                setPriority(PRIORITY_EDEFAULT);
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
            case TodolistPackage.ITEM__IMB_ID:
                return IMB_ID_EDEFAULT == null ? imbId != null : !IMB_ID_EDEFAULT.equals(imbId);
            case TodolistPackage.ITEM__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case TodolistPackage.ITEM__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TodolistPackage.ITEM__DUE_DATE:
                return DUE_DATE_EDEFAULT == null ? dueDate != null : !DUE_DATE_EDEFAULT.equals(dueDate);
            case TodolistPackage.ITEM__PRIORITY:
                return priority != PRIORITY_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (imbId: ");
        result.append(imbId);
        result.append(", description: ");
        result.append(description);
        result.append(", name: ");
        result.append(name);
        result.append(", dueDate: ");
        result.append(dueDate);
        result.append(", priority: ");
        result.append(priority);
        result.append(')');
        return result.toString();
    }

} //ItemImpl
