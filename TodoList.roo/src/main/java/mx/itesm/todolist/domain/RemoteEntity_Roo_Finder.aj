// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package mx.itesm.todolist.domain;

import java.lang.Long;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import mx.itesm.todolist.domain.RemoteEntity;

privileged aspect RemoteEntity_Roo_Finder {
    
    public static TypedQuery<RemoteEntity> RemoteEntity.findRemoteEntitysByImbId(Long imbId) {
        if (imbId == null) throw new IllegalArgumentException("The imbId argument is required");
        EntityManager em = RemoteEntity.entityManager();
        TypedQuery<RemoteEntity> q = em.createQuery("SELECT RemoteEntity FROM RemoteEntity AS remoteentity WHERE remoteentity.imbId = :imbId", RemoteEntity.class);
        q.setParameter("imbId", imbId);
        return q;
    }
    
    public static TypedQuery<RemoteEntity> RemoteEntity.findRemoteEntitysByEntityId(Long entityId) {
        if (entityId == null) throw new IllegalArgumentException("The entityId argument is required");
        EntityManager em = RemoteEntity.entityManager();
        TypedQuery<RemoteEntity> q = em.createQuery("SELECT RemoteEntity FROM RemoteEntity AS remoteentity WHERE remoteentity.entityId = :entityId", RemoteEntity.class);
        q.setParameter("entityId", entityId);
        return q;
    }
    
}
