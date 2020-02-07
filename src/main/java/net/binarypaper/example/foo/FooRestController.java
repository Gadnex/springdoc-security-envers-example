package net.binarypaper.example.foo;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import net.binarypaper.example.config.AuditRevision;
import net.binarypaper.example.config.AuditRevisionHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("foo")
@Transactional
@RolesAllowed({"admin"})
public class FooRestController {

    private final EntityManager em;

    public FooRestController(EntityManager em) {
        this.em = em;
    }

    @PostMapping
    @JsonView(Foo.All.class)
    public Foo addFoo(@RequestBody @JsonView(Foo.Add.class) Foo foo) {
        em.persist(foo);
        em.flush();
        return foo;
    }

    @GetMapping
    @JsonView(Foo.List.class)
    public List<Foo> getAllFoos() {
        TypedQuery<Foo> query = em.createNamedQuery("Foo.findAll", Foo.class);
        return query.getResultList();
    }

    @GetMapping("{id}")
    @JsonView(Foo.All.class)
    public Foo getFooById(@PathVariable Long id) {
        Foo foo = em.find(Foo.class, id);
        if (foo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid foo id");
        }
        return foo;
    }

    @PutMapping("{id}")
    @JsonView(Foo.All.class)
    public Foo updateFoo(
            @PathVariable Long id,
            @RequestBody @JsonView(Foo.Update.class) Foo foo
    ) {
        if (!id.equals(foo.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The foo id in the path does not match the id in the request body");
        }
        Foo fromDB = getFooById(id);
        if (!fromDB.getVersion().equals(foo.getVersion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The foo version in the request body is invalid");
        }
        foo = em.merge(foo);
        em.flush();
        return foo;
    }

    @DeleteMapping("{id}")
    public void deleteFoo(@PathVariable Long id) {
        Foo foo = getFooById(id);
        em.remove(foo);
        em.flush();
    }

    @GetMapping("{id}/revisions")
    @JsonView(AuditRevision.class)
    public List<Foo> getFooAuditRevisions(@PathVariable Long id) {
        AuditRevisionHelper<Foo> auditRevisionHelper = new AuditRevisionHelper<>(Foo.class);
        List<Foo> revisions = auditRevisionHelper.getAllAuditRevisions(em, id);
        if (revisions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid foo id");
        }
        return revisions;
    }
}
