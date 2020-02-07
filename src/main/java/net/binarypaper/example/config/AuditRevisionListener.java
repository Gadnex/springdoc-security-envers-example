package net.binarypaper.example.config;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object object) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuditRevision auditRevision = (AuditRevision) object;
        auditRevision.setUsername(user.getUsername());
    }

}
