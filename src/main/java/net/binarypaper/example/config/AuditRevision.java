package net.binarypaper.example.config;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(name = "AUDIT_REVISION")
@RevisionEntity(AuditRevisionListener.class)
@Getter
@Setter
public class AuditRevision extends DefaultRevisionEntity {

    private String username;
    
}
