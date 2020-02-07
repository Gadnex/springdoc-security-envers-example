package net.binarypaper.example.foo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.Data;
import net.binarypaper.example.config.AuditRevision;
import org.hibernate.envers.Audited;

// JPA annotations
@Entity
@NamedQueries({
    @NamedQuery(name = "Foo.findAll", query = "SELECT f FROM Foo AS f ORDER BY f.name")
})
// Envers annotations
@Audited
// Jackson annotations
@JsonInclude(Include.NON_NULL)
// Lombok annotations
@Data
public class Foo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({List.class, All.class, Update.class, AuditRevision.class})
    private Long id;

    @Version
    @JsonView({All.class, Update.class})
    private Integer version;

    @JsonView({Add.class, List.class, All.class, Update.class, AuditRevision.class})
    private String name;

    @JsonView({Add.class, All.class, Update.class, AuditRevision.class})
    private String description;
    
    // JPA Annotations
    @Transient
    // Jackson annotations
    @JsonView({AuditRevision.class})
    // OpenAPI annotations
    @Schema(
            description = "The the audit revision details",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private AuditRevision revision;

    public interface Add {
    }

    public interface Update {
    }

    public interface All {
    }

    public interface List {
    }

}
