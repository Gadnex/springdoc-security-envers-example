package net.binarypaper.example.foo;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("foo")
@Transactional
@RolesAllowed({"admin"})
@Tag(name = "foo", description = "The Foo API")
public class FooRestController {

    private final EntityManager em;

    public FooRestController(EntityManager em) {
        this.em = em;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Foo.All.class)
    @Operation(
            summary = "Add a Foo",
            description = "Add a Foo to the database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "201",
                description = "Foo added"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Error in request body",
                content = @Content
        )
    })
    public Foo addFoo(
            @RequestBody
            @JsonView(Foo.Add.class)
            @Parameter(description = "The details of the Foo to add")
            @Valid Foo foo,
            HttpServletResponse response
    ) {
        em.persist(foo);
        em.flush();
        response.addHeader("location", "/foo/" + foo.getId());
        return foo;
    }

    @GetMapping
    @JsonView(Foo.List.class)
    @Operation(
            summary = "Get all Foo",
            description = "Get all Foos from the database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "List of Foos returned"
        )
    })
    public List<Foo> getAllFoos() {
        TypedQuery<Foo> query = em.createNamedQuery("Foo.findAll", Foo.class);
        return query.getResultList();
    }

    @GetMapping("{id}")
    @JsonView(Foo.All.class)
    @Operation(
            summary = "Get Foo by ID",
            description = "Get Foo by ID from the database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Foo returned"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Invalid Foo ID",
                content = @Content
        )
    })
    public Foo getFooById(@PathVariable @Parameter(description = "The ID of the Foo") Long id) {
        Foo foo = em.find(Foo.class, id);
        if (foo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid foo id");
        }
        return foo;
    }

    @PutMapping("{id}")
    @JsonView(Foo.All.class)
    @Operation(
            summary = "Update Foo by ID",
            description = "Update Foo by ID from the database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Foo updated"
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid request data",
                content = @Content
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Invalid Foo ID",
                content = @Content
        )
    })
    public Foo updateFoo(
            @PathVariable @Parameter(description = "The ID of the Foo") Long id,
            @RequestBody
            @JsonView(Foo.Update.class)
            @Parameter(description = "The details of the Foo to update")
            @Valid Foo foo
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
    @Operation(
            summary = "Delete Foo by ID",
            description = "Delete Foo by ID from the database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Foo deleted"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Invalid Foo ID",
                content = @Content
        )
    })
    public void deleteFoo(@PathVariable @Parameter(description = "The ID of the Foo") Long id) {
        Foo foo = getFooById(id);
        em.remove(foo);
        em.flush();
    }

    @GetMapping("{id}/revisions")
    @JsonView(AuditRevision.class)
    @Operation(
            summary = "Get Foo Audit Revisions by ID",
            description = "Get Foo Audit Revisions by ID from the audit database"
    )
    @ApiResponses({
        @ApiResponse(
                responseCode = "200",
                description = "Foo Audit Revisions returned"
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Invalid Foo ID",
                content = @Content
        )
    })
    public List<Foo> getFooAuditRevisions(@PathVariable @Parameter(description = "The ID of the Foo") Long id) {
        AuditRevisionHelper<Foo> auditRevisionHelper = new AuditRevisionHelper<>(Foo.class);
        List<Foo> revisions = auditRevisionHelper.getAllAuditRevisions(em, id);
        if (revisions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid foo id");
        }
        return revisions;
    }
}
